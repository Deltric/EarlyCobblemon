/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.*
import net.minecraft.util.math.MathHelper.ceil
import net.minecraft.util.math.MathHelper.floor
import net.minecraft.world.BlockView
import net.minecraft.world.World

fun World.playSoundServer(
    position: Vec3d,
    sound: SoundEvent,
    category: SoundCategory = SoundCategory.NEUTRAL,
    volume: Float = 1F,
    pitch: Float = 1F
) = (this as ServerWorld).playSound(null, position.x, position.y, position.z, sound, category, volume, pitch)

fun <T : ParticleEffect> World.sendParticlesServer(
    particleType: T,
    position: Vec3d,
    particles: Int,
    offset: Vec3d,
    speed: Double
) = (this as ServerWorld).spawnParticles(particleType, position.x, position.y, position.z, particles, offset.x, offset.y, offset.z, speed)

fun World.squeezeWithinBounds(pos: BlockPos): BlockPos {
    val border = worldBorder
    return BlockPos(
        pos.x.coerceIn(border.boundWest.toInt(), border.boundEast.toInt()),
        pos.y.coerceIn(bottomY, topY),
        pos.z.coerceIn(border.boundNorth.toInt(), border.boundSouth.toInt())
    )
}

fun ServerWorld.isBoxLoaded(box: Box): Boolean {
    val startChunkX = ChunkSectionPos.getSectionCoord(box.minX)
    val startChunkZ = ChunkSectionPos.getSectionCoord(box.minZ)
    val endChunkX = ChunkSectionPos.getSectionCoord(box.maxX)
    val endChunkZ = ChunkSectionPos.getSectionCoord(box.maxZ)

    for (chunkX in startChunkX..endChunkX) {
        for (chunkZ in startChunkZ..endChunkZ) {
            if (!this.isChunkLoaded(ChunkPos.toLong(chunkX, chunkZ))) {
                return false
            }
        }
    }

    return true
}

fun Box.getRanges(): Triple<IntRange, IntRange, IntRange> {
    return Triple(floor(minX)..ceil(maxX), minY.toInt()..ceil(maxY), minZ.toInt()..ceil(maxZ))
}

fun BlockView.doForAllBlocksIn(box: Box, useMutablePos: Boolean, action: (BlockState, BlockPos) -> Unit) {
    val mutable = BlockPos.Mutable()
    val (xRange, yRange, zRange) = box.getRanges()
    for (x in xRange) {
        for (y in yRange) {
            for (z in zRange) {
                val pos = if (useMutablePos) mutable.set(x, y, z) else BlockPos(x, y, z)
                val state = getBlockState(pos)
                action(state, pos)
            }
        }
    }
}

fun BlockView.getBlockStates(box: Box): Iterable<BlockState> {
    val states = mutableListOf<BlockState>()
    doForAllBlocksIn(box, useMutablePos = true) { state, _ -> states.add(state) }
    return states
}

fun BlockView.getBlockStatesWithPos(box: Box): Iterable<Pair<BlockState, BlockPos>> {
    val states = mutableListOf<Pair<BlockState, BlockPos>>()
    doForAllBlocksIn(box, useMutablePos = true) { state, pos -> states.add(state to pos) }
    return states
}

fun BlockView.getWaterAndLavaIn(box: Box): Pair<Boolean, Boolean> {
    var hasWater = false
    var hasLava = false

    doForAllBlocksIn(box, useMutablePos = true) { state, _ ->
        if (!hasWater && state.fluidState.isIn(FluidTags.WATER)) {
            hasWater = true
        }
        if (!hasLava && state.fluidState.isIn(FluidTags.LAVA)) {
            hasLava = true
        }
    }

    return hasWater to hasLava
}

fun Entity.canFit(pos: BlockPos) = canFit(pos.toVec3d())

fun Entity.canFit(vec: Vec3d): Boolean {
    val box = boundingBox.offset(vec.subtract(this.pos))
    return world.isSpaceEmpty(box)
}