package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.CraftingTermSlot;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.client.base.TickingRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermContainer;

public class CraftingTermRecipesWidget extends PlayerRecipesWidget implements TickingRecipesWidget {

  private final CraftingTermMenu menu;
  private int lastContainerHeight;
  private Slot changeableOutputSlot;

  public CraftingTermRecipesWidget(CraftingTermMenu menu, HandledScreen<?> pHandledScreen,
                                   Slot pOutputSlot) {
    super(pHandledScreen, pOutputSlot);
    this.menu = menu;
    this.changeableOutputSlot = pOutputSlot;
  }

  @Override
  public void selectRecipe(Identifier pIdentifier) {
    super.selectRecipe(pIdentifier);
    this.menu.getPlayerInventory().player.getEntityWorld().getRecipeManager()
        .get(pIdentifier).ifPresent(recipe -> {
          ((AccessorCraftingTermContainer) this.menu).setCurrentRecipe((CraftingRecipe) recipe);
          ((AccessorCraftingTermContainer) this.menu).callUpdateCurrentRecipeAndOutput(true);
        });
  }

  @Override
  public Slot getOutputSlot() {
    return this.changeableOutputSlot;
  }

  @Override
  public void tick() {

    if (((AccessorHandledScreen) this.handledScreen).getBackgroundHeight() !=
        this.lastContainerHeight) {

      for (Slot inventorySlot : this.handledScreen.getScreenHandler().slots) {

        if (inventorySlot instanceof CraftingTermSlot craftingTermSlot) {
          this.changeableOutputSlot = craftingTermSlot;
          this.resetWidgetOffsets();
          break;
        }
      }
      this.lastContainerHeight = ((AccessorHandledScreen) this.handledScreen).getBackgroundHeight();
    }
  }
}
