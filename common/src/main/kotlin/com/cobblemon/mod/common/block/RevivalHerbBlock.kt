/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.mulch.MulchVariant
import com.cobblemon.mod.common.api.mulch.Mulchable
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.CropBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemConvertible
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView

@Suppress("OVERRIDE_DEPRECATION", "MemberVisibilityCanBePrivate", "unused")
class RevivalHerbBlock(settings: Settings) : CropBlock(settings), Mulchable {

    init {
        defaultState = this.stateManager.defaultState.with(AGE, MIN_AGE)
            .with(IS_WILD, false)
            .with(MUTATION, Mutation.NONE)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
        builder.add(IS_WILD)
        builder.add(MUTATION)
    }

    override fun getAgeProperty(): IntProperty = AGE

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val floor = world.getBlockState(pos.down())
        val block = world.getBlockState(pos)
        // A bit of a copy pasta but we don't have access to the BlockState being attempted to be placed above on the canPlantOnTop
        return (block.isIn(BlockTags.REPLACEABLE_BY_TREES) || block.isOf(Blocks.AIR) || block.isOf(this)) && ((state.get(IS_WILD) && floor.isIn(BlockTags.DIRT)) || this.canPlantOnTop(floor, world, pos))
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = AGE_SHAPES.getOrNull(this.getAge(state)) ?: VoxelShapes.fullCube()

    override fun getSeedsItem(): ItemConvertible = CobblemonItems.REVIVAL_HERB

    override fun canHaveMulchApplied(world: ServerWorld, pos: BlockPos, state: BlockState, variant: MulchVariant): Boolean =
        variant == MulchVariant.SURPRISE && this.getAge(state) <= MUTABLE_MAX_AGE && !this.isMutated(state)

    override fun applyMulch(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState, variant: MulchVariant) {
        val picked = Mutation.values().filterNot { it == Mutation.NONE }.random()
        world.setBlockState(pos, state.with(MUTATION, picked))//.with(AGE, MUTABLE_MAX_AGE + 1))
    }

    /**
     * Resolves the current [Mutation] of the given [state].
     *
     * @param state The [BlockState] being queried.
     * @return The current [Mutation].
     */
    fun mutationOf(state: BlockState): Mutation = state.get(MUTATION)

    /**
     * Checks if the given [state] has a [Mutation] different from [Mutation.NONE].
     *
     * @param state The [BlockState] being queried.
     * @return If a mutation has occurred.
     */
    fun isMutated(state: BlockState): Boolean = this.mutationOf(state) != Mutation.NONE

    override fun getMaxAge(): Int = MAX_AGE

    // DO NOT use withAge
    // Explanation for these 2 beautiful copy pasta are basically that we need to keep the blockstate and that's not possible with the default impl :(
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.getBaseLightLevel(pos, 0) < 9 || this.isMature(state)) {
            return
        }
        val currentMoisture = getAvailableMoisture(this, world, pos)
        if (random.nextInt((25F / currentMoisture).toInt() + 1) == 0) {
            this.applyGrowth(world, pos, state, false)
        }
    }

    override fun applyGrowth(world: World, pos: BlockPos, state: BlockState) {
        this.applyGrowth(world, pos, state, true)
    }

    private fun applyGrowth(world: World, pos: BlockPos, state: BlockState, useRandomGrowthAmount: Boolean) {
        val growthAmount = if (useRandomGrowthAmount) this.getGrowthAmount(world) else 1
        val newAge = (this.getAge(state) + growthAmount).coerceAtMost(this.maxAge)
        world.setBlockState(pos, state.with(AGE, newAge), NOTIFY_LISTENERS)
    }

    /**
     * Represents the possible mutation states of this plant.
     */
    enum class Mutation : StringIdentifiable {

        NONE,
        MENTAL,
        POWER,
        WHITE,
        MIRROR;

        override fun asString(): String = this.name.lowercase()

    }

    companion object {

        const val MIN_AGE = 0
        const val MAX_AGE = 8

        /**
         * This represents the max age the plant can be at before no longer being able to mutate.
         */
        const val MUTABLE_MAX_AGE = 6
        val AGE = IntProperty.of("age", MIN_AGE, MAX_AGE)
        val IS_WILD = BooleanProperty.of("is_wild")
        val MUTATION = EnumProperty.of("mutation", Mutation::class.java)
//        val MULCH = EnumProperty.of("mulch", MulchVariant::class.java)

        /**
         * The [VoxelShape] equivalent to a certain age.
         * Highest index is [MAX_AGE].
         */
        val AGE_SHAPES = arrayOf(
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.1, 1.0),
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.2, 1.0),
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.3, 1.0),
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.4, 1.0),
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.5, 1.0),
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.7, 1.0),
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.7, 1.0),
            VoxelShapes.cuboid(0.0, -0.9, 0.0, 1.0, 0.9, 1.0),
            VoxelShapes.fullCube()
        )

    }

}