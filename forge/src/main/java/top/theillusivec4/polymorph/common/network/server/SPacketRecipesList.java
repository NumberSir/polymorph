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

package top.theillusivec4.polymorph.common.network.server;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;
import top.theillusivec4.polymorph.common.impl.RecipePair;

public class SPacketRecipesList {

  private final SortedSet<IRecipePair> recipeList;
  private final ResourceLocation selected;

  public SPacketRecipesList(SortedSet<IRecipePair> pRecipeList, ResourceLocation pSelected) {
    this.recipeList = new TreeSet<>();

    if (pRecipeList != null) {
      this.recipeList.addAll(pRecipeList);
    }
    this.selected = pSelected;
  }

  public static void encode(SPacketRecipesList pPacket, PacketBuffer pBuffer) {

    if (!pPacket.recipeList.isEmpty()) {
      pBuffer.writeInt(pPacket.recipeList.size());

      for (IRecipePair data : pPacket.recipeList) {
        pBuffer.writeResourceLocation(data.getResourceLocation());
        pBuffer.writeItemStack(data.getOutput());
      }

      if (pPacket.selected != null) {
        pBuffer.writeResourceLocation(pPacket.selected);
      }
    }
  }

  public static SPacketRecipesList decode(PacketBuffer pBuffer) {
    SortedSet<IRecipePair> recipeDataset = new TreeSet<>();
    ResourceLocation selected = null;

    if (pBuffer.isReadable()) {
      int size = pBuffer.readInt();

      for (int i = 0; i < size; i++) {
        recipeDataset.add(new RecipePair(pBuffer.readResourceLocation(), pBuffer.readItemStack()));
      }

      if (pBuffer.isReadable()) {
        selected = pBuffer.readResourceLocation();
      }
    }
    return new SPacketRecipesList(recipeDataset, selected);
  }

  public static void handle(SPacketRecipesList pPacket, Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        Optional<IRecipesWidget> maybeWidget = RecipesWidget.get();
        maybeWidget.ifPresent(widget -> widget.setRecipesList(pPacket.recipeList, pPacket.selected));

        if (!maybeWidget.isPresent()) {
          RecipesWidget.enqueueRecipesList(pPacket.recipeList, pPacket.selected);
        }
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
