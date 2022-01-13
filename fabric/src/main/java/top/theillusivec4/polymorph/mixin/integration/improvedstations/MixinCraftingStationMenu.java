package top.theillusivec4.polymorph.mixin.integration.improvedstations;

import java.util.Optional;
import me.shedaniel.istations.containers.CraftingStationMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(CraftingStationMenu.class)
public class MixinCraftingStationMenu {

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/recipe/RecipeManager.getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "updateResult")
  private <C extends Inventory, T extends Recipe<C>> Optional<T> polymorph$getFirstMatch(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world, ScreenHandler menu,
      World unused, PlayerEntity player, CraftingInventory craftingInventory,
      CraftingResultInventory resultInventory) {
    return RecipeSelection.getPlayerRecipe(menu, type, inventory, world, player);
  }
}
