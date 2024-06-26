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

package com.illusivesoulworks.polymorph.common;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IPlayerRecipeData;
import com.illusivesoulworks.polymorph.common.capability.PlayerRecipeData;
import com.illusivesoulworks.polymorph.server.PolymorphCommands;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class CommonEventsListener {

  @SubscribeEvent
  public void registerCommands(final RegisterCommandsEvent evt) {
    PolymorphCommands.register(evt.getDispatcher());
  }

  @SubscribeEvent
  public void registerCapabilities(final RegisterCapabilitiesEvent evt) {
    evt.register(IPlayerRecipeData.class);
    evt.register(IBlockEntityRecipeData.class);
  }

  @SubscribeEvent
  public void playerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent evt) {

    if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
      PolymorphCommonEvents.playerDisconnected(serverPlayer);
    }
  }

  @SubscribeEvent
  public void openContainer(final PlayerContainerEvent.Open evt) {
    PolymorphCommonEvents.openContainer(evt.getEntity(), evt.getContainer());
  }

  @SubscribeEvent
  public void levelTick(final TickEvent.LevelTickEvent evt) {

    if (evt.phase == TickEvent.Phase.END) {
      PolymorphCommonEvents.levelTick(evt.level);
    }
  }

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<BlockEntity> evt) {
    BlockEntity be = evt.getObject();
    IBlockEntityRecipeData recipeData = PolymorphApi.getInstance().createBlockEntityRecipeData(be);

    if (recipeData != null) {
      LazyOptional<IBlockEntityRecipeData> cap = LazyOptional.of(() -> recipeData);
      evt.addCapability(PolymorphForgeCapabilities.BLOCK_ENTITY_RECIPE_DATA_ID,
          new BlockEntityRecipeDataProvider(be, cap));
    }
  }

  @SubscribeEvent
  public void attachCapabilitiesPlayer(final AttachCapabilitiesEvent<Entity> evt) {
    Entity entity = evt.getObject();

    if (entity instanceof Player) {
      PlayerRecipeData data = new PlayerRecipeData((Player) entity);
      LazyOptional<IPlayerRecipeData> cap = LazyOptional.of(() -> data);
      evt.addCapability(PolymorphForgeCapabilities.PLAYER_RECIPE_DATA_ID,
          new PlayerRecipeDataProvider(cap));
    }
  }

  private record PlayerRecipeDataProvider(LazyOptional<IPlayerRecipeData> capability)
      implements ICapabilitySerializable<Tag> {

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
                                             @Nullable Direction direction) {
      return PolymorphForgeCapabilities.PLAYER_RECIPE_DATA.orEmpty(capability, this.capability);
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
      return this.capability.map(recipeData -> recipeData.writeNBT(provider))
          .orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {

      if (tag instanceof CompoundTag) {
        this.capability.ifPresent(recipeData -> recipeData.readNBT(provider, (CompoundTag) tag));
      }
    }
  }

  private record BlockEntityRecipeDataProvider(BlockEntity blockEntity,
                                               LazyOptional<IBlockEntityRecipeData> capability)
      implements ICapabilitySerializable<Tag> {

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
                                             @Nullable Direction direction) {
      return PolymorphForgeCapabilities.BLOCK_ENTITY_RECIPE_DATA.orEmpty(capability,
          this.capability);
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
      return this.capability.map(recipeData -> recipeData.writeNBT(provider))
          .orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {

      if (tag instanceof CompoundTag) {
        this.capability.ifPresent(recipeData -> recipeData.readNBT(provider, (CompoundTag) tag));
      }
    }
  }
}
