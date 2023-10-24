/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import net.minecraft.block.*
import net.minecraft.item.ItemConvertible
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

@Suppress("OVERRIDE_DEPRECATION")
class MedicinalLeekBlock(settings: Settings) : CropBlock(settings) {

    override fun getAgeProperty(): IntProperty = AGE

    override fun getMaxAge(): Int = MATURE_AGE

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(this.ageProperty)
    }

    override fun getSeedsItem(): ItemConvertible = CobblemonItems.MEDICINAL_LEEK

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = AGE_TO_SHAPE[this.getAge(state)]

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        // This is specified as growing fast like sugar cane
        // They have 15 age stages until they grow upwards, this is an attempt at a chance based but likely event
        if (this.isMature(state) || random.nextInt(4) != 0) {
            return
        }
        this.applyGrowth(world, pos, state)
    }

    // These 3 are still around for the sake of compatibility, vanilla won't trigger it but some mods might
    // We implement applyGrowth & getGrowthAmount for them
    override fun isFertilizable(world: WorldView?, pos: BlockPos?, state: BlockState?, isClient: Boolean): Boolean = !this.isMature(state)

    override fun applyGrowth(world: World, pos: BlockPos, state: BlockState) {
        world.setBlockState(pos, state.with(this.ageProperty, (this.getAge(state) + 1).coerceAtMost(this.maxAge)), NOTIFY_LISTENERS)
    }

    override fun getGrowthAmount(world: World): Int = 1

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        // We don't care about the sky & light level, sugar cane doesn't either
        return this.canPlantOnTop(state, world, pos)
    }

    override fun canPlantOnTop(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        val down = pos.down()
        val floor = world.getBlockState(down)
        val fluidState = world.getFluidState(down)
        return floor.isIn(CobblemonBlockTags.MEDICINAL_LEEK_PLANTABLE) && fluidState.level == 8 && fluidState.isIn(FluidTags.WATER)
    }

    companion object {

        const val MATURE_AGE = 3
        val AGE: IntProperty = Properties.AGE_3
        val AGE_TO_SHAPE = arrayOf(
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
                createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0)
        )

    }
}