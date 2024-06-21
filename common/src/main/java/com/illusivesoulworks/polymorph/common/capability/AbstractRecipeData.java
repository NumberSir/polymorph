/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.common.capability;

import com.illusivesoulworks.polymorph.PolymorphConstants;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.api.common.capability.IRecipeData;
import com.illusivesoulworks.polymorph.common.impl.RecipePair;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class AbstractRecipeData<E> implements IRecipeData<E> {

  private final SortedSet<IRecipePair> recipesList;
  private final E owner;

  private RecipeHolder<?> lastRecipe;
  private RecipeHolder<?> selectedRecipe;
  private ResourceLocation loadedRecipe;
  private boolean isFailing;

  private NonNullList<Item> input;

  public AbstractRecipeData(E owner) {
    this.recipesList = new TreeSet<>();
    this.owner = owner;
    this.input = NonNullList.create();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipe(
      RecipeType<T> type,
      I inventory, Level level,
      List<RecipeHolder<T>> recipesList) {
    boolean isEmpty = this.isEmpty(inventory);
    this.getLoadedRecipe().flatMap(id -> level.getRecipeManager().byKey(id))
        .ifPresent(selected -> {
          try {
            if (selected.value().getType() == type &&
                (((T) selected.value()).matches(inventory, level) || isEmpty)) {
              this.setSelectedRecipe(selected);
            }
          } catch (ClassCastException e) {
            PolymorphConstants.LOG.error("Recipe {} does not match inventory {}", selected.id(),
                inventory);
          }
          this.loadedRecipe = null;
        });

    if (isEmpty) {
      this.setFailing(false);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    AtomicReference<RecipeHolder<T>> ref = new AtomicReference<>(null);
    this.getLastRecipe().ifPresent(recipe -> {
      try {
        if (recipe.value().getType() == type && ((T) recipe.value()).matches(inventory, level)) {
          this.getSelectedRecipe().ifPresent(selected -> {
            try {
              if (selected.value().getType() == type &&
                  ((T) selected.value()).matches(inventory, level)) {
                ref.set((RecipeHolder<T>) selected);
              }
            } catch (ClassCastException e) {
              PolymorphConstants.LOG.error("Recipe {} does not match inventory {}", selected.id(),
                  inventory);
            }
          });
        }
      } catch (ClassCastException e) {
        PolymorphConstants.LOG.error("Recipe {} does not match inventory {}", recipe.id(),
            inventory);
      }
    });
    RecipeHolder<T> result = ref.get();

    if (result != null && !(this instanceof AbstractBlockEntityRecipeData)) {
      boolean inputChanged = false;
      int size = inventory.size();
      NonNullList<Item> currentInput = NonNullList.withSize(size, Items.AIR);

      if (size != this.input.size()) {
        inputChanged = true;
      }

      for (int i = 0; i < size; i++) {
        ItemStack stack = inventory.getItem(i);
        Item item = stack.getItem();

        if (!inputChanged && i < this.input.size() && item != this.input.get(i)) {
          inputChanged = true;
        }

        if (!stack.isEmpty()) {
          currentInput.set(i, item);
        }
      }
      this.input = currentInput;

      if (!inputChanged) {
        this.setFailing(false);
        this.sendRecipesListToListeners(false);
        return Optional.of(result);
      }
    }
    SortedSet<IRecipePair> newDataset = new TreeSet<>();
    List<RecipeHolder<T>> recipes =
        recipesList.isEmpty() ? level.getRecipeManager().getRecipesFor(type, inventory, level) :
            recipesList;

    if (recipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    List<RecipeHolder<T>> validRecipes = new ArrayList<>();

    for (RecipeHolder<T> entry : recipes) {
      ResourceLocation id = entry.id();

      if (ref.get() == null &&
          this.getSelectedRecipe().map(recipe -> recipe.id().equals(id)).orElse(false)) {
        ref.set(entry);
      }
      ItemStack output = entry.value().getResultItem(level.registryAccess());

      // noinspection ConstantConditions
      if (output == null || output.isEmpty() || entry.value() instanceof CustomRecipe) {
        output = entry.value().assemble(inventory, level.registryAccess());
      }

      if (output.isEmpty()) {
        continue;
      }
      newDataset.add(new RecipePair(id, output));
      validRecipes.add(entry);
    }

    if (validRecipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    this.setRecipesList(newDataset);
    result = ref.get();

    if (result == null) {
      ResourceLocation rl = newDataset.first().getResourceLocation();

      for (RecipeHolder<T> recipe : validRecipes) {

        if (recipe.id().equals(rl)) {
          result = recipe;
          break;
        }
      }
    }

    if (result == null) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    this.lastRecipe = result;
    this.setSelectedRecipe(result);
    this.setFailing(false);
    this.sendRecipesListToListeners(false);
    return Optional.of(result);
  }

  @Override
  public Optional<RecipeHolder<?>> getSelectedRecipe() {
    return Optional.ofNullable(this.selectedRecipe);
  }

  @Override
  public void setSelectedRecipe(@Nonnull RecipeHolder<?> recipe) {
    this.selectedRecipe = recipe;
  }

  public Optional<RecipeHolder<?>> getLastRecipe() {
    return Optional.ofNullable(this.lastRecipe);
  }

  public Optional<ResourceLocation> getLoadedRecipe() {
    return Optional.ofNullable(this.loadedRecipe);
  }

  @Nonnull
  @Override
  public SortedSet<IRecipePair> getRecipesList() {
    return this.recipesList;
  }

  @Override
  public void setRecipesList(@Nonnull SortedSet<IRecipePair> recipesList) {
    this.recipesList.clear();
    this.recipesList.addAll(recipesList);
  }

  @Override
  public boolean isEmpty(RecipeInput inventory) {

    if (inventory != null) {

      for (int i = 0; i < inventory.size(); i++) {

        if (!inventory.getItem(i).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public E getOwner() {
    return this.owner;
  }

  @Override
  public void selectRecipe(@Nonnull RecipeHolder<?> recipe) {
    this.setSelectedRecipe(recipe);
  }

  @Override
  public abstract Set<ServerPlayer> getListeners();

  @Override
  public void sendRecipesListToListeners(boolean isEmpty) {
    Pair<SortedSet<IRecipePair>, ResourceLocation> packetData =
        isEmpty ? new Pair<>(new TreeSet<>(), null) : this.getPacketData();

    for (ServerPlayer listener : this.getListeners()) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C(listener, packetData.getFirst(), packetData.getSecond());
    }
  }

  @Override
  public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
    return new Pair<>(this.getRecipesList(), null);
  }

  @Override
  public boolean isFailing() {
    return this.isFailing;
  }

  @Override
  public void setFailing(boolean isFailing) {
    this.isFailing = isFailing;
  }

  @Override
  public void readNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {

    if (compoundTag.contains("SelectedRecipe")) {
      this.loadedRecipe = ResourceLocation.tryParse(compoundTag.getString("SelectedRecipe"));
    }

    if (compoundTag.contains("RecipeDataSet")) {
      Set<IRecipePair> dataset = this.getRecipesList();
      dataset.clear();
      ListTag list = compoundTag.getList("RecipeDataSet", Tag.TAG_COMPOUND);

      for (Tag inbt : list) {
        CompoundTag tag = (CompoundTag) inbt;
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("Id"));
        ItemStack stack = ItemStack.parseOptional(provider, tag.getCompound("ItemStack"));
        dataset.add(new RecipePair(id, stack));
      }
    }
  }

  @Nonnull
  @Override
  public CompoundTag writeNBT(HolderLookup.Provider provider) {
    CompoundTag nbt = new CompoundTag();
    this.getSelectedRecipe().ifPresent(
        selected -> nbt.putString("SelectedRecipe", this.selectedRecipe.id().toString()));
    Set<IRecipePair> dataset = this.getRecipesList();

    if (!dataset.isEmpty()) {
      ListTag list = new ListTag();

      for (IRecipePair data : dataset) {
        CompoundTag tag = new CompoundTag();
        tag.put("ItemStack", data.getOutput().save(provider, new CompoundTag()));
        tag.putString("Id", data.getResourceLocation().toString());
        list.add(tag);
      }
      nbt.put("RecipeDataSet", list);
    }
    return nbt;
  }
}
