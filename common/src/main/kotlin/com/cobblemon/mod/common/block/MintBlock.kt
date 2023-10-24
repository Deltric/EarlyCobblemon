/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.item.MintLeafItem
import net.minecraft.block.*
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

@Suppress("OVERRIDE_DEPRECATION", "MemberVisibilityCanBePrivate")
class MintBlock(private val mintType: MintType, settings: Settings) : CropBlock(settings), Fertilizable {

    init {
        defaultState = stateManager.defaultState
            .with(AGE, 0)
            .with(IS_WILD, false)
    }

    // DO NOT use withAge
    // Explanation for these 2 beautiful copy pasta are basically that we need to keep the blockstate and that's not possible with the default impl :(
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.getBaseLightLevel(pos, 0) < 9 || this.isMature(state) || random.nextInt(8) != 0) {
            return
        }
        this.applyGrowth(world, pos, state, false)
    }

    override fun applyGrowth(world: World, pos: BlockPos, state: BlockState) {
        this.applyGrowth(world, pos, state, true)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
        builder.add(IS_WILD)
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val floor = world.getBlockState(pos.down())
        // A bit of a copy pasta but we don't have access to the BlockState being attempted to be placed above on the canPlantOnTop
        return (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && ((this.isWild(state) && floor.isIn(BlockTags.DIRT)) || this.canPlantOnTop(floor, world, pos))
    }

    override fun getSeedsItem(): ItemConvertible = this.mintType.getSeed()

    override fun getGrowthAmount(world: World): Int = 1

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = AGE_TO_SHAPE[this.getAge(state)]

    fun isWild(state: BlockState): Boolean = state.get(IS_WILD)

    private fun applyGrowth(world: World, pos: BlockPos, state: BlockState, useRandomGrowthAmount: Boolean) {
        val growthAmount = if (useRandomGrowthAmount) this.getGrowthAmount(world) else 1
        val newAge = (this.getAge(state) + growthAmount).coerceAtMost(MATURE_AGE)
        world.setBlockState(pos, state.with(AGE, newAge), NOTIFY_LISTENERS)
    }

    @Suppress("unused")
    enum class MintType {

        RED,
        BLUE,
        CYAN,
        PINK,
        GREEN,
        WHITE;

        fun getSeed(): Item = when (this) {
            RED -> CobblemonItems.RED_MINT_SEEDS
            BLUE -> CobblemonItems.BLUE_MINT_SEEDS
            CYAN -> CobblemonItems.CYAN_MINT_SEEDS
            PINK -> CobblemonItems.PINK_MINT_SEEDS
            GREEN -> CobblemonItems.GREEN_MINT_SEEDS
            WHITE -> CobblemonItems.WHITE_MINT_SEEDS
        }

        fun getLeaf(): MintLeafItem = when (this) {
            RED -> CobblemonItems.RED_MINT_LEAF
            BLUE -> CobblemonItems.BLUE_MINT_LEAF
            CYAN -> CobblemonItems.CYAN_MINT_LEAF
            PINK -> CobblemonItems.PINK_MINT_LEAF
            GREEN -> CobblemonItems.GREEN_MINT_LEAF
            WHITE -> CobblemonItems.WHITE_MINT_LEAF
        }

        fun getCropBlock(): MintBlock = when (this) {
            RED -> CobblemonBlocks.RED_MINT
            BLUE -> CobblemonBlocks.BLUE_MINT
            CYAN -> CobblemonBlocks.CYAN_MINT
            PINK -> CobblemonBlocks.PINK_MINT
            GREEN -> CobblemonBlocks.GREEN_MINT
            WHITE -> CobblemonBlocks.WHITE_MINT
        }

    }

    companion object {

        val AGE: IntProperty = CropBlock.AGE
        const val MATURE_AGE = MAX_AGE
        val IS_WILD: BooleanProperty = BooleanProperty.of("is_wild")

        private val AGE_0_SHAPE = VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.1, 1.0)
        private val AGE_1_TO_2_SHAPE = VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.2, 1.0)
        private val AGE_3_SHAPE = VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.3, 1.0)
        private val AGE_4_SHAPE = VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.4, 1.0)
        private val AGE_5_SHAPE = VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.5, 1.0)
        private val AGE_6_SHAPE = VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.6, 1.0)
        private val AGE_7_SHAPE = VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.7, 1.0)

        val AGE_TO_SHAPE = arrayOf(
            AGE_0_SHAPE,
            AGE_1_TO_2_SHAPE,
            AGE_1_TO_2_SHAPE,
            AGE_3_SHAPE,
            AGE_4_SHAPE,
            AGE_5_SHAPE,
            AGE_6_SHAPE,
            AGE_7_SHAPE
        )

    }
}
