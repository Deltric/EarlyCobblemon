/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.mulch.MulchVariant
import com.cobblemon.mod.common.api.mulch.Mulchable
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEvents

class MulchItem(val variant: MulchVariant) : CobblemonItem(Settings()) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val pos = context.blockPos
        if (this.useOnMulchAble(context.stack, world, pos)) {
            // Plays bone meal effect
            if (!world.isClient) {
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0)
            }
            return ActionResult.success(true)
        }
        return ActionResult.PASS
    }

    private fun useOnMulchAble(stack: ItemStack, world: World, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        if (state.block is Mulchable) {
            val mulchAble = state.block as? Mulchable ?: return false
            if (world is ServerWorld && mulchAble.canHaveMulchApplied(world, pos, state, this.variant)) {
                mulchAble.applyMulch(world, world.random, pos, state, this.variant)
                stack.decrement(1)
                return true
            }
        }
        return false
    }

}