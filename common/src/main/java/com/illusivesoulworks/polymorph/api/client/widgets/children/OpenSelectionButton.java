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

package com.illusivesoulworks.polymorph.api.client.widgets.children;

import com.illusivesoulworks.polymorph.platform.Services;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class OpenSelectionButton extends ImageButton {

  private final AbstractContainerScreen<?> containerScreen;
  private int xOffset;
  private int yOffset;

  public OpenSelectionButton(AbstractContainerScreen<?> containerScreen, int x, int y,
                             WidgetSprites sprites, OnPress onPress) {
    super(0, 0, 16, 16, sprites, onPress);
    this.containerScreen = containerScreen;
    this.xOffset = x;
    this.yOffset = y;
  }

  public void setOffsets(int x, int y) {
    this.xOffset = x;
    this.yOffset = y;
  }

  @Override
  public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY,
                           float partialTicks) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    this.setX(Services.CLIENT_PLATFORM.getScreenLeft(this.containerScreen) + this.xOffset);
    this.setY(Services.CLIENT_PLATFORM.getScreenTop(this.containerScreen) + this.yOffset);
    super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
  }
}
