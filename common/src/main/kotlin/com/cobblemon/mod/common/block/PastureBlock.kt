/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.pasture.PastureLink
import com.cobblemon.mod.common.api.pasture.PastureLinkManager
import com.cobblemon.mod.common.api.pasture.PasturePermissionControllers
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket
import com.cobblemon.mod.common.util.isInBattle
import com.cobblemon.mod.common.util.playSoundServer
import com.cobblemon.mod.common.util.toVec3d
import com.cobblemon.mod.common.util.voxelShape
import java.util.UUID
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.Waterloggable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldEvents
import net.minecraft.world.WorldView

@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
class PastureBlock(properties: Settings): BlockWithEntity(properties), Waterloggable, PreEmptsExplosion {
    companion object {
        val PART: EnumProperty<PasturePart> = EnumProperty.of("part", PasturePart::class.java)
        val ON: BooleanProperty = BooleanProperty.of("on")
        val WATERLOGGED: BooleanProperty = BooleanProperty.of("waterlogged")

        private val SOUTH_AABB_TOP = buildCollider(top = true, Direction.NORTH)
        private val NORTH_AABB_TOP = buildCollider(top = true, Direction.SOUTH)
        private val WEST_AABB_TOP = buildCollider(top = true, Direction.WEST)
        private val EAST_AABB_TOP = buildCollider(top = true, Direction.EAST)

        private val SOUTH_AABB_BOTTOM = buildCollider(top = false, Direction.SOUTH)
        private val NORTH_AABB_BOTTOM = buildCollider(top = false, Direction.NORTH)
        private val WEST_AABB_BOTTOM = buildCollider(top = false, Direction.WEST)
        private val EAST_AABB_BOTTOM = buildCollider(top = false, Direction.EAST)

        private fun buildCollider(top: Boolean, direction: Direction): VoxelShape {
            if (top) {
                return VoxelShapes.union(
                    voxelShape(0.1875, 0.0, 0.0625, 0.8125, 0.0625, 0.3125, direction),
                    voxelShape(0.125, 0.0, 0.375, 0.875, 0.1875, 0.9375, direction),
                    voxelShape(0.1875, 0.1875, 0.4375, 0.8125, 0.6875, 0.9375, direction),
                    voxelShape(0.8125, 0.1875, 0.375, 0.875, 0.6875, 0.9375, direction),
                    voxelShape(0.125, 0.1875, 0.375, 0.1875, 0.6875, 0.9375, direction),
                    voxelShape(0.125, 0.6875, 0.375, 0.875, 0.75, 0.9375, direction)
                )
            } else {
                return VoxelShapes.union(
                    voxelShape(0.875, 0.0, 0.0, 1.0, 1.0, 0.125, direction),
                    voxelShape(0.125, 0.0, 0.0, 0.875, 0.125, 0.125, direction),
                    voxelShape(0.125, 0.875, 0.0, 0.875, 1.0, 0.125, direction),
                    voxelShape(0.125, 0.125, 0.0625, 0.875, 0.875, 0.125, direction),
                    voxelShape(0.0625, 0.125, 0.125, 0.125, 0.875, 0.875, direction),
                    voxelShape(0.0, 0.0, 0.0, 0.125, 1.0, 0.125, direction),
                    voxelShape(0.125, 0.0, 0.125, 0.875, 0.125, 1.0, direction),
                    voxelShape(0.875, 0.125, 0.125, 0.9375, 0.875, 0.875, direction),
                    voxelShape(0.125, 0.875, 0.125, 0.875, 1.0, 1.0, direction),
                    voxelShape(0.875, 0.0, 0.875, 1.0, 1.0, 1.0, direction),
                    voxelShape(0.875, 0.0, 0.125, 1.0, 0.125, 0.875, direction),
                    voxelShape(0.875, 0.875, 0.125, 1.0, 1.0, 0.875, direction),
                    voxelShape(0.0, 0.875, 0.125, 0.125, 1.0, 0.875, direction),
                    voxelShape(0.0, 0.0, 0.125, 0.125, 0.125, 0.875, direction),
                    voxelShape(0.0, 0.0, 0.875, 0.125, 1.0, 1.0, direction),
                    voxelShape(0.0, 0.125, 0.375, 0.0625, 0.875, 0.625, direction),
                    voxelShape(0.9375, 0.125, 0.375, 1.0, 0.875, 0.625, direction),
                    voxelShape(0.1875, 0.1875, 0.05625, 0.8125, 0.75, 0.05625, direction),
                    voxelShape(0.1875, 0.125, 0.3125, 0.8125, 0.3125, 0.875, direction),
                    voxelShape(0.1875, 0.125, 0.3125, 0.8125, 0.3125, 0.875, direction),
                    voxelShape(0.1875, 0.0625, 0.875, 0.8125, 0.25, 0.875, direction),
                    voxelShape(0.1875, 0.25, 0.25, 0.1875, 0.4375, 0.875, direction),
                    voxelShape(0.8125, 0.25, 0.25, 0.8125, 0.4375, 0.875, direction),
                    voxelShape(0.1875, 0.3125, 0.3125, 0.8125, 0.5, 0.3125, direction),
                    voxelShape(0.25, 0.75, 0.3125, 0.75, 1.0, 0.8125, direction)
                )
            }
        }
    }

    enum class PasturePart(private val label: String) : StringIdentifiable {
        TOP("top"),
        BOTTOM("bottom");
        override fun asString() = label
    }

    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState) = if (blockState.get(PART) == PasturePart.BOTTOM) PokemonPastureBlockEntity(blockPos, blockState) else null


    init {
        defaultState = this.stateManager.defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(PART, PasturePart.BOTTOM)
            .with(ON, false)
    }

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL
    override fun getPlacementState(blockPlaceContext: ItemPlacementContext): BlockState? {
        val abovePosition = blockPlaceContext.blockPos.up()
        val world = blockPlaceContext.world
        if (world.getBlockState(abovePosition).canReplace(blockPlaceContext) && !world.isOutOfHeightLimit(abovePosition)) {
            return defaultState
                .with(HorizontalFacingBlock.FACING, blockPlaceContext.horizontalPlayerFacing)
                .with(PART, PasturePart.BOTTOM)
                .with(WATERLOGGED, blockPlaceContext.world.getFluidState(blockPlaceContext.blockPos).fluid == Fluids.WATER)
        }

        return null
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return true
    }

    fun getPositionOfOtherPart(state: BlockState, pos: BlockPos): BlockPos {
        return if (state.contains(PART) && state.get(PART) == PasturePart.BOTTOM) {
            pos.up()
        } else {
            pos.down()
        }
    }

    fun getBasePosition(state: BlockState, pos: BlockPos): BlockPos {
        return if (isBase(state)) {
            pos
        } else {
            pos.down()
        }
    }

    private fun isBase(state: BlockState): Boolean = state.contains(PART) && state.get(PART) == PasturePart.BOTTOM

    override fun canPathfindThrough(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, pathComputationType: NavigationType) = false

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(HorizontalFacingBlock.FACING)
        builder.add(PART)
        builder.add(ON)
        builder.add(WATERLOGGED)
    }

    fun checkBreakEntity(world: WorldAccess, state: BlockState, pos: BlockPos) {
        if (state.get(PART) == PasturePart.TOP) {
            return
        }
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is PokemonPastureBlockEntity) {
            blockEntity.onBroken()
        }
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        checkBreakEntity(world, state, pos)
        if (!world.isClient && player?.isCreative == true) {
            var blockPos: BlockPos = BlockPos.ORIGIN
            var blockState: BlockState = state
            val part = state.get(PART)
            if (part == PasturePart.TOP && world.getBlockState(pos.down().also { blockPos = it }).also { blockState = it }.isOf(state.block) && blockState.get(PART) == PasturePart.BOTTOM) {
                checkBreakEntity(world, blockState, blockPos)
                val blockState2 = if (blockState.fluidState.isOf(Fluids.WATER)) Blocks.WATER.defaultState else Blocks.AIR.defaultState
                world.setBlockState(blockPos, blockState2, NOTIFY_ALL or SKIP_DROPS)
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, getRawIdFromState(blockState))
            }
        }
        super.onBreak(world, pos, state, player)
    }

    override fun whenExploded(world: World, state: BlockState, pos: BlockPos) {
        val blockEntity = world.getBlockEntity(pos) as? PokemonPastureBlockEntity ?: return
        blockEntity.onBroken()
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return checkType(type, CobblemonBlockEntities.PASTURE, PokemonPastureBlockEntity.TICKER::tick)
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack?) {
        world.setBlockState(
            pos.up(),
            state.with(PART, PasturePart.TOP).with(WATERLOGGED, world.getFluidState((pos.up())).fluid == Fluids.WATER) as BlockState,
            3
        )
        world.updateNeighbors(pos, Blocks.AIR)
        state.updateNeighbors(world, pos, 3)

        if (world is ServerWorld && placer is ServerPlayerEntity) {
            val blockEntity = world.getBlockEntity(pos) as? PokemonPastureBlockEntity ?: return
            blockEntity.ownerId = placer.uuid
            blockEntity.ownerName = placer.gameProfile.name
            blockEntity.markDirty()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (player is ServerPlayerEntity && !player.isInBattle()) {
            val basePos = getBasePosition(state, pos)

            // Remove any duplicate block entities that may exist
            world.getBlockEntity(basePos.up())?.markRemoved()

            val baseEntity = world.getBlockEntity(basePos)
            if (baseEntity !is PokemonPastureBlockEntity) return ActionResult.SUCCESS


            val pcId = Cobblemon.storage.getPC(player.uuid).uuid
            val linkId = UUID.randomUUID()

            val perms = PasturePermissionControllers.permit(player, baseEntity)
            CobblemonNetwork.sendPacketToPlayer(
                player = player,
                packet = OpenPasturePacket(
                    pcId = pcId,
                    pastureId = linkId,
                    permissions = perms,
                    limit = baseEntity.getMaxTethered(),
                    tetheredPokemon = baseEntity.tetheredPokemon.mapNotNull { it.toDTO(player) }
                )
            )

            PastureLinkManager.createLink(player.uuid, PastureLink(linkId, pcId, world.dimensionKey.value, getBasePosition(state, pos), perms))

            world.playSoundServer(
                position = pos.toVec3d(),
                sound = CobblemonSounds.PC_ON,
                volume = 0.5F,
                pitch = 1F
            )

            return ActionResult.SUCCESS
        }

        return ActionResult.SUCCESS
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(blockState: BlockState, blockGetter: BlockView, blockPos: BlockPos, collisionContext: ShapeContext): VoxelShape {
        return if (blockState.get(PART) == PasturePart.TOP)  {
            when (blockState.get(HorizontalFacingBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_TOP
                Direction.WEST -> WEST_AABB_TOP
                Direction.EAST -> EAST_AABB_TOP
                else -> NORTH_AABB_TOP
            }
        } else {
            when (blockState.get(HorizontalFacingBlock.FACING)) {
                Direction.SOUTH -> SOUTH_AABB_BOTTOM
                Direction.WEST -> WEST_AABB_BOTTOM
                Direction.EAST -> EAST_AABB_BOTTOM
                else -> NORTH_AABB_BOTTOM
            }
        }
    }

    override fun rotate(blockState: BlockState, rotation: BlockRotation): BlockState = blockState.with(HorizontalFacingBlock.FACING, rotation.rotate(blockState.get(HorizontalFacingBlock.FACING)))

    override fun mirror(blockState: BlockState, mirror: BlockMirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.get(HorizontalFacingBlock.FACING)))
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }

        val isPasture = neighborState.isOf(this)
        val part = state.get(PART)
        if (!isPasture && part == PasturePart.TOP && neighborPos == pos.down()) {
            return Blocks.AIR.defaultState
        } else if (!isPasture && part == PasturePart.BOTTOM && neighborPos == pos.up()) {
            checkBreakEntity(world, state, pos)
            return Blocks.AIR.defaultState
        }

        return state
    }
}