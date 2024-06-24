package com.illusivesoulworks.polymorph.mixin.integration.fastbench;

import dev.shadowsoffire.fastbench.util.FastBenchUtil;
import org.spongepowered.asm.mixin.Mixin;

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
