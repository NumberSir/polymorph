package com.illusivesoulworks.polymorph.common.capability;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class RecipeCache {

  private final Entry<? extends RecipeInput, ? extends Recipe<?>>[] entries;
  private WeakReference<RecipeManager> cachedRecipeManager = new WeakReference<>(null);

  public RecipeCache(int size) {
    this.entries = new Entry<?, ?>[size];
  }

  @SuppressWarnings("unchecked")
  public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> get(Level level,
                                                                                RecipeType<T> recipeType,
                                                                                I recipeInput) {

    if (recipeInput.isEmpty()) {
      return List.of();
    } else {
      this.validateRecipeManager(level);

      for (int i = 0; i < this.entries.length; i++) {
        Entry<?, ?> entry = this.entries[i];

        if (entry != null && entry.matches(recipeInput)) {
          this.moveEntryToFront(i);
          return (List<RecipeHolder<T>>) (Object) entry.recipes();
        }
      }
      return this.compute(level, recipeType, recipeInput);
    }
  }

  private void validateRecipeManager(Level level) {
    RecipeManager recipeManager = level.getRecipeManager();

    if (recipeManager != this.cachedRecipeManager.get()) {
      this.cachedRecipeManager = new WeakReference<>(recipeManager);
      Arrays.fill(this.entries, null);
    }
  }

  private <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> compute(Level level,
                                                                                     RecipeType<T> recipeType,
                                                                                     I recipeInput) {
    List<RecipeHolder<T>> list =
        level.getRecipeManager().getRecipesFor(recipeType, recipeInput, level);
    this.insert(recipeInput, list);
    return list;
  }

  private void moveEntryToFront(int index) {

    if (index > 0) {
      Entry<?, ?> entry = this.entries[index];
      System.arraycopy(this.entries, 0, this.entries, 1, index);
      this.entries[0] = entry;
    }
  }

  private <I extends RecipeInput, T extends Recipe<I>> void insert(I recipeInput,
                                                                   List<RecipeHolder<T>> recipes) {
    NonNullList<ItemStack> list = NonNullList.withSize(recipeInput.size(), ItemStack.EMPTY);

    for (int i = 0; i < recipeInput.size(); i++) {
      list.set(i, recipeInput.getItem(i).copyWithCount(1));
    }
    System.arraycopy(this.entries, 0, this.entries, 1, this.entries.length - 1);
    this.entries[0] = new Entry<>(list, recipeInput.size(), recipes);
  }

  record Entry<I extends RecipeInput, T extends Recipe<I>>(NonNullList<ItemStack> key, int size,
                                                           List<RecipeHolder<T>> recipes) {

    public boolean matches(RecipeInput recipeInput) {

      if (this.size() == recipeInput.size()) {

        for (int i = 0; i < this.key.size(); i++) {

          if (!ItemStack.isSameItemSameComponents(this.key.get(i), recipeInput.getItem(i))) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    }
  }
}
