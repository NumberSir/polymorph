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

package com.illusivesoulworks.polymorph.common.util;

import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record RecipePair(ResourceLocation resourceLocation,
                         ItemStack output) implements IRecipePair {

  public static final StreamCodec<RegistryFriendlyByteBuf, IRecipePair> STREAM_CODEC =
      StreamCodec.composite(
          ResourceLocation.STREAM_CODEC,
          IRecipePair::getResourceLocation,
          ItemStack.STREAM_CODEC,
          IRecipePair::getOutput,
          RecipePair::new);

  @Override
  public ItemStack getOutput() {
    return output;
  }

  @Override
  public ResourceLocation getResourceLocation() {
    return resourceLocation;
  }

  @Override
  public int compareTo(@Nonnull IRecipePair other) {
    ItemStack output1 = this.getOutput();
    ItemStack output2 = other.getOutput();
    int compare = output1.getDescriptionId().compareTo(output2.getDescriptionId());

    if (compare == 0) {
      int diff = output1.getCount() - output2.getCount();

      if (diff == 0) {
        DataComponentMap components1 = output1.getComponents();
        DataComponentMap components2 = output2.getComponents();

        if (Objects.equals(components1, components2)) {
          return 0;
        }
        return components1.hashCode() - components2.hashCode();
      } else {
        return diff;
      }
    } else {

      // Sort vanilla recipes after modded recipes if they appear in the same list
      if (this.getResourceLocation().getNamespace().equals("minecraft") &&
          !other.getResourceLocation().getNamespace().equals("minecraft")) {
        return 1;
      } else if (!this.getResourceLocation().getNamespace().equals("minecraft") &&
          other.getResourceLocation().getNamespace().equals("minecraft")) {
        return -1;
      }
      return compare;
    }
  }
}
