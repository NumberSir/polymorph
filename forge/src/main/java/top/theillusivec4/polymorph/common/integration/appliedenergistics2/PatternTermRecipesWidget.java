/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.CraftingTermSlot;
import appeng.menu.slot.PatternTermSlot;
import appeng.parts.encoding.EncodingMode;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import top.theillusivec4.polymorph.api.client.base.ITickingRecipesWidget;
import top.theillusivec4.polymorph.client.impl.PolymorphClient;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermMenu;

public class PatternTermRecipesWidget extends PlayerRecipesWidget implements ITickingRecipesWidget {

  private final PatternEncodingTermMenu container;
  private int lastContainerHeight;
  private Slot changeableOutputSlot;

  public PatternTermRecipesWidget(AbstractContainerScreen<?> containerScreen,
                                  PatternEncodingTermMenu container, Slot outputSlot) {
    super(containerScreen, outputSlot);
    this.container = container;
    this.changeableOutputSlot = outputSlot;
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    super.selectRecipe(pResourceLocation);
    this.container.getPlayerInventory().player.level.getRecipeManager()
        .byKey(pResourceLocation).ifPresent(recipe -> {
          ((AccessorPatternTermMenu) this.container).setCurrentRecipe((CraftingRecipe) recipe);
          ((AccessorPatternTermMenu) this.container).callGetAndUpdateOutput();
        });
  }

  @Override
  public void render(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pRenderPartialTicks) {

    if (container.mode == EncodingMode.CRAFTING) {
      super.render(pMatrixStack, pMouseX, pMouseY, pRenderPartialTicks);
    }
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

    if (container.mode != EncodingMode.CRAFTING) {
      return false;
    }
    return super.mouseClicked(pMouseX, pMouseY, pButton);
  }

  @Override
  public Slot getOutputSlot() {
    return this.changeableOutputSlot;
  }

  @Override
  public void tick() {

    if (this.containerScreen.getYSize() != this.lastContainerHeight) {

      for (Slot inventorySlot : this.containerScreen.getMenu().slots) {

        if (inventorySlot instanceof PatternTermSlot patternTermSlot) {
          this.changeableOutputSlot = patternTermSlot;
          this.resetWidgetOffsets();
          break;
        }
      }
      this.lastContainerHeight = this.containerScreen.getYSize();
    }
  }
}
