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

package com.illusivesoulworks.polymorph.platform;

import com.illusivesoulworks.polymorph.api.common.base.IPolymorphNetwork;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IPlayerRecipeData;
import com.illusivesoulworks.polymorph.common.PolymorphForgeCapabilities;
import com.illusivesoulworks.polymorph.common.PolymorphForgeNetwork;
import com.illusivesoulworks.polymorph.platform.services.IPlatform;
import java.nio.file.Path;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgePlatform implements IPlatform {

  private static final IPolymorphNetwork PACKET_DISTRIBUTOR =
      new PolymorphForgeNetwork();

  @Override
  public Path getGameDir() {
    return FMLPaths.GAMEDIR.get();
  }

  @Override
  public Path getConfigDir() {
    return FMLPaths.CONFIGDIR.get();
  }

  @Override
  public boolean isModLoaded(String id) {
    return ModList.get().isLoaded(id);
  }

  @Override
  public boolean isModFileLoaded(String id) {
    return FMLLoader.getLoadingModList().getModFileById(id) != null;
  }

  @Override
  public boolean isShaped(Recipe<?> recipe) {
    return recipe instanceof IShapedRecipe;
  }

  @Override
  public boolean isSameShape(Recipe<?> recipe1, Recipe<?> recipe2) {

    if (isShaped(recipe1) && isShaped(recipe2)) {
      IShapedRecipe<?> shaped = (IShapedRecipe<?>) recipe1;
      IShapedRecipe<?> otherShaped = (IShapedRecipe<?>) recipe2;
      return shaped.getRecipeHeight() == otherShaped.getRecipeHeight() &&
          shaped.getRecipeWidth() == otherShaped.getRecipeWidth();
    }
    return true;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public IPlayerRecipeData getRecipeData(Player player) {
    return player.getCapability(PolymorphForgeCapabilities.PLAYER_RECIPE_DATA).orElse(null);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public IBlockEntityRecipeData getRecipeData(BlockEntity blockEntity) {
    return blockEntity.getCapability(PolymorphForgeCapabilities.BLOCK_ENTITY_RECIPE_DATA)
        .orElse(null);
  }

  @Override
  public IPolymorphNetwork getPacketDistributor() {
    return PACKET_DISTRIBUTOR;
  }
}
