package com.illusivesoulworks.polymorph.common.integration.fastbench;

import com.illusivesoulworks.polymorph.common.integration.AbstractCompatibilityModule;
import com.illusivesoulworks.polymorph.mixin.core.AccessorCraftingMenu;
import com.illusivesoulworks.polymorph.mixin.core.AccessorInventoryMenu;
import dev.shadowsoffire.fastbench.FastBench;
import dev.shadowsoffire.fastbench.net.RecipeMessage;
import dev.shadowsoffire.placebo.network.PacketDistro;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class FastBenchModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(AbstractContainerMenu container, RecipeHolder<?> recipe) {

//    if (recipe.value() instanceof CraftingRecipe craftingRecipe) {
//      CraftingContainer inv = null;
//      ResultContainer result = null;
//      Player player = null;
//
//      if (container instanceof CraftingMenu) {
//        AccessorCraftingMenu accessor = (AccessorCraftingMenu) container;
//        inv = accessor.getCraftSlots();
//        result = accessor.getResultSlots();
//        player = accessor.getPlayer();
//      } else if (container instanceof InventoryMenu) {
//        AccessorInventoryMenu accessor = (AccessorInventoryMenu) container;
//        inv = accessor.getCraftSlots();
//        result = accessor.getResultSlots();
//        player = accessor.getOwner();
//      }
//
//      if (inv != null && result != null && player != null) {
//        ItemStack stack = craftingRecipe.assemble(inv, player.level().registryAccess());
//
//        // Some mods seem to be violating the non-null contract so this check is necessary
//        // https://github.com/TheIllusiveC4/Polymorph/issues/163
//        // noinspection ConstantConditions
//        if (stack != null && !ItemStack.matches(stack, result.getItem(0))) {
//          PacketDistro.sendTo(new RecipeMessage((RecipeHolder<CraftingRecipe>) recipe, stack), player);
//          result.setItem(0, stack);
//          result.setRecipeUsed(recipe);
//        }
//      }
//    }
    return false;
  }
}
