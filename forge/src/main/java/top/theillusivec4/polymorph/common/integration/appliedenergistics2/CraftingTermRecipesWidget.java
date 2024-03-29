package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.CraftingTermSlot;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import top.theillusivec4.polymorph.api.client.base.ITickingRecipesWidget;
import top.theillusivec4.polymorph.client.impl.PolymorphClient;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermMenu;

public class CraftingTermRecipesWidget extends PlayerRecipesWidget implements
    ITickingRecipesWidget {

  private final CraftingTermMenu menu;
  private int lastContainerHeight;
  private Slot changeableOutputSlot;

  public CraftingTermRecipesWidget(CraftingTermMenu menu, AbstractContainerScreen<?> pHandledScreen,
                                   Slot pOutputSlot) {
    super(pHandledScreen, pOutputSlot);
    this.menu = menu;
    this.changeableOutputSlot = pOutputSlot;
  }

  @Override
  public void selectRecipe(ResourceLocation resourceLocation) {
    super.selectRecipe(resourceLocation);
    this.menu.getPlayerInventory().player.getLevel().getRecipeManager()
        .byKey(resourceLocation).ifPresent(recipe -> {
          ((AccessorCraftingTermMenu) this.menu).setCurrentRecipe((CraftingRecipe) recipe);
          ((AccessorCraftingTermMenu) this.menu).callUpdateCurrentRecipeAndOutput(true);
        });
  }

  @Override
  public Slot getOutputSlot() {
    return this.changeableOutputSlot;
  }

  @Override
  public void tick() {

    if (this.containerScreen.getYSize() != this.lastContainerHeight) {

      for (Slot inventorySlot : this.containerScreen.getMenu().slots) {

        if (inventorySlot instanceof CraftingTermSlot craftingTermSlot) {
          this.changeableOutputSlot = craftingTermSlot;
          this.resetWidgetOffsets();
          break;
        }
      }
      this.lastContainerHeight = this.containerScreen.getYSize();
    }
  }
}
