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

package com.illusivesoulworks.polymorph.common.network.server;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.impl.RecipePair;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record SPacketRecipesList(Optional<SortedSet<IRecipePair>> recipeList,
                                 Optional<ResourceLocation> selected)
    implements CustomPacketPayload {

  public static final Type<SPacketRecipesList> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(PolymorphApi.MOD_ID, "recipes_list"));

  private static final StreamCodec<RegistryFriendlyByteBuf, SortedSet<IRecipePair>> SET_CODEC =
      new StreamCodec<>() {
        @Nonnull
        @Override
        public SortedSet<IRecipePair> decode(@Nonnull RegistryFriendlyByteBuf buf) {
          SortedSet<IRecipePair> recipeDataset = new TreeSet<>();

          if (buf.isReadable()) {
            int size = buf.readInt();

            for (int i = 0; i < size; i++) {
              recipeDataset.add(
                  new RecipePair(buf.readResourceLocation(), ItemStack.STREAM_CODEC.decode(buf)));
            }
          }
          return recipeDataset;
        }

        @Override
        public void encode(@Nonnull RegistryFriendlyByteBuf buf,
                           @Nonnull SortedSet<IRecipePair> list) {
          SortedSet<IRecipePair> list1 = new TreeSet<>(list);

          if (!list1.isEmpty()) {
            buf.writeInt(list1.size());

            for (IRecipePair data : list1) {
              buf.writeResourceLocation(data.getResourceLocation());
              ItemStack.STREAM_CODEC.encode(buf, data.getOutput());
            }
          } else {
            buf.writeInt(0);
          }
        }
      };

  public static final StreamCodec<RegistryFriendlyByteBuf, SPacketRecipesList> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.optional(SET_CODEC),
          SPacketRecipesList::recipeList,
          ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
          SPacketRecipesList::selected,
          SPacketRecipesList::new);

  public static void handle(SPacketRecipesList packet) {
    ClientPacketHandler.handle(packet);
  }

  @Nonnull
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
