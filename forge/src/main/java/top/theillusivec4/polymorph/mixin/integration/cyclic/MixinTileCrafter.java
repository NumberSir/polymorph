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

package top.theillusivec4.polymorph.mixin.integration.cyclic;

import com.lothrazar.cyclic.base.TileEntityBase;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.integration.cyclic.CyclicModule;

@SuppressWarnings("unused")
@Mixin(TileCrafter.class)
public abstract class MixinTileCrafter extends TileEntityBase {

  public MixinTileCrafter(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  @SuppressWarnings("ConstantConditions")
  @Inject(
      at = @At("HEAD"),
      method = "tryRecipes",
      remap = false,
      cancellable = true)
  private void polymorph$tryRecipes(ArrayList<ItemStack> stacks,
                                    CallbackInfoReturnable<IRecipe<?>> cir) {
    CyclicModule.getRecipe(stacks, this.world, (TileCrafter) (Object) this)
        .ifPresent(cir::setReturnValue);
  }
}
