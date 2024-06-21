package com.illusivesoulworks.polymorph.mixin.integration.fastbench;

import com.illusivesoulworks.polymorph.common.crafting.RecipeSelection;
import dev.shadowsoffire.fastbench.util.CraftingInventoryExt;
import dev.shadowsoffire.fastbench.util.FastBenchUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(FastBenchUtil.class)
public class MixinFastBenchUtil {

//  @Redirect(
//      at = @At(
//          value = "INVOKE",
//          target = "dev/shadowsoffire/fastbench/util/FastBenchUtil.findRecipe(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/RecipeHolder;"),
//      method = "slotChangedCraftingGrid",
//      remap = false)
//  private static RecipeHolder<CraftingRecipe> polymorph$findRecipe(CraftingContainer inv,
//                                                                   Level world,
//                                                                   Level unused1, Player player,
//                                                                   CraftingInventoryExt unused2,
//                                                                   ResultContainer result) {
//    return RecipeSelection.getPlayerRecipe(player.containerMenu, RecipeType.CRAFTING, inv, world,
//        player).orElse(null);
//  }
}
