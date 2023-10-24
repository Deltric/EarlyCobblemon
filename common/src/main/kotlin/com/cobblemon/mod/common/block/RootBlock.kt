/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.world.BigRootPropagatedEvent
import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import net.minecraft.world.event.GameEvent

@Suppress("OVERRIDE_DEPRECATION", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "DEPRECATION")
abstract class RootBlock(settings: Settings) : Block(settings), Fertilizable, ShearableBlock {
    private val possibleDirections = setOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
    override fun hasRandomTicks(state: BlockState) = true

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        // Check for propagation
        if (random.nextDouble() < Cobblemon.config.bigRootPropagationChance && world.getLightLevel(pos) < MAX_PROPAGATING_LIGHT_LEVEL && !hasReachedSpreadCap(world, pos)) {
            this.spreadFrom(world, pos, random)
        }
    }

    fun hasReachedSpreadCap(world: World, pos: BlockPos): Boolean {
        var nearby = 0
        val nearbyPositions = BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4)).iterator()
        while (nearbyPositions.hasNext()) {
            val blockPos = nearbyPositions.next()
            if (world.getBlockState(blockPos).isIn(CobblemonBlockTags.ROOTS)) {
                nearby++
                if (nearby >= Cobblemon.config.maxRootsInArea) {
                    return true
                }
            }
        }
        return false
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean = this.canGoOn(state, world, pos) { true }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (direction == Direction.UP && !this.canPlaceAt(state, world, pos)) Blocks.AIR.defaultState else super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun isFertilizable(world: WorldView, pos: BlockPos, state: BlockState, isClient: Boolean) = this.canSpread(world, pos, state)

    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = this.canSpread(world, pos, state)

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        this.spreadFrom(world, pos, random)
    }

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL

    fun spreadFrom(world: ServerWorld, pos: BlockPos, random: Random) {
        val possibleDirections = this.possibleDirections.toMutableSet()
        while (possibleDirections.isNotEmpty()) {
            val picked = possibleDirections.random()
            possibleDirections.remove(picked)
            val adjacent = pos.offset(picked)
            if (this.canSpreadTo(this.defaultState, world, adjacent)) {
                val resultingSpread = this.spreadingRoot(random)
                val event = BigRootPropagatedEvent(
                    world = world,
                    pos = pos,
                    newRootPosition = adjacent,
                    resultingSpread = resultingSpread
                )
                CobblemonEvents.BIG_ROOT_PROPAGATED.postThen(
                    event = event,
                    ifSucceeded = { ev ->
                        world.setBlockState(
                            ev.newRootPosition,
                            ev.resultingSpread
                        )
                    }
                )
                break
            }
        }
    }

    override fun attemptShear(world: World, state: BlockState, pos: BlockPos, successCallback: () -> Unit): Boolean {
        // We always allow the shearing at the moment but hey if it ever changes at least it's easy to do so.
        world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1F, 1F)
        world.setBlockState(pos, this.shearedResultingState())
        val shearedDrop = this.shearedDrop()
        if (!shearedDrop.isEmpty) {
            dropStack(world, pos, shearedDrop)
        }
        world.emitGameEvent(null, GameEvent.SHEAR, pos)
        return true
    }

    /**
     * Checks if this block can spread to any neighbouring blocks.
     *
     * @param world The [WorldView] being queried.
     * @param pos The [BlockPos] the block is currently on.
     * @param state The [BlockState] of the block.
     * @return If any direction supports spreading.
     */
    protected fun canSpread(world: WorldView, pos: BlockPos, state: BlockState): Boolean = this.possibleDirections.any { direction ->
        val adjacent = pos.offset(direction)
        this.canSpreadTo(this.defaultState, world, adjacent)
    }

    /**
     * Checks if the given coordinates allow for a root to spread to.
     *
     * @param state The base root [BlockState].
     * @param world The [WorldView] being queried.
     * @param pos The [BlockPos] being queried.
     * @return If the given coordinates allow for a root to spread onto.
     */
    protected fun canSpreadTo(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val existingState = world.getBlockState(pos)
        // Isn't this useless since it's always air or replaceable if this is happening, not! See the spread implementation for the need of a valid block in the target pos as well.
        return (existingState.isAir || existingState.isReplaceable) && this.canGoOn(state, world, pos) { ceiling -> ceiling.isIn(CobblemonBlockTags.ROOTS_SPREADABLE) }
    }

    /**
     * Picks the [BlockState] that results from a spread of this block.
     *
     * @param random The [Random] instance used during a spread attempt.
     * @return The [BlockState] that will represent the spread root.
     */
    protected fun spreadingRoot(random: Random): BlockState = if (random.nextFloat() < Cobblemon.config.energyRootChance) CobblemonBlocks.ENERGY_ROOT.defaultState else this.defaultState

    /**
     * Checks if the given coordinates allow for a root to be placed with some context.
     *
     * @param state The base root [BlockState].
     * @param world The [WorldView] being queried.
     * @param pos The [BlockPos] being queried.
     * @param ceilingValidator An extra condition to validate if the block it will be placed on allows for it based on context.
     * @return If the given coordinates allow for a root to be set.
     */
    protected fun canGoOn(state: BlockState, world: WorldView, pos: BlockPos, ceilingValidator: (ceiling: BlockState) -> Boolean): Boolean {
        val up = pos.up()
        val upState = world.getBlockState(up)
        return upState.isSideSolidFullSquare(world, up, Direction.DOWN) && ceilingValidator(upState)
    }

    /**
     * Resolves the [BlockState] when sheared.
     *
     * @return The resulting [BlockState] when this [ShearableBlock] is sheared.
     */
    protected abstract fun shearedResultingState(): BlockState

    /**
     * Resolves the [ItemStack] drop when sheared.
     *
     * @return The resulting [ItemStack] to be dropped when this [ShearableBlock] is sheared.
     */
    protected abstract fun shearedDrop(): ItemStack

    companion object {
        const val MAX_PROPAGATING_LIGHT_LEVEL = 11
    }
}
