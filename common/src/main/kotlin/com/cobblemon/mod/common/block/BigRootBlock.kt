/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
class BigRootBlock(settings: Settings) : RootBlock(settings) {
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = AABB

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val stack = player.getStackInHand(hand)
        if (stack.isOf(Items.SHEARS) && this.attemptShear(world, state, pos) { stack.damage(1, player) { affected -> affected.sendToolBreakStatus(hand) } }) {
            return ActionResult.success(world.isClient)
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun shearedResultingState(): BlockState = Blocks.HANGING_ROOTS.defaultState

    override fun shearedDrop(): ItemStack = Items.STRING.defaultStack

    companion object {

        private val AABB = VoxelShapes.cuboid(0.2, 0.3, 0.2, 0.8, 1.0, 0.8)

    }

}