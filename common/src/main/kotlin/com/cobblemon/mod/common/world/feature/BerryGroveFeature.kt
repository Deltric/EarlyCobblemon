/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature

import com.cobblemon.mod.common.api.berry.BerryHelper
import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.util.weightedSelection
import net.minecraft.block.Blocks
import net.minecraft.block.GrassBlock
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier
import net.minecraft.world.gen.stateprovider.BlockStateProvider
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider

class BerryGroveFeature : Feature<DefaultFeatureConfig>(DefaultFeatureConfig.CODEC){
    override fun generate(context: FeatureContext<DefaultFeatureConfig>): Boolean {
        val worldGenLevel: StructureWorldAccess = context.world!!
        val random = context.random!!
        val origin = context.origin!!

        val isGenerating = worldGenLevel.getChunk(origin).status != ChunkStatus.FULL

        if (!isGenerating) return false
        val biome = worldGenLevel.getBiome(origin)
        //This basically goes through and finds the berries whose preferred biome we are in
        //Maybe cache these per biome in a map?
        val validTrees = BerryHelper.getBerriesForBiome(biome)
        if (validTrees.isEmpty()) return false
        val pickedTree = validTrees.weightedSelection { it.berry()!!.weight }!!
        val berry = pickedTree.berry()!!
        val numTreesToGen = pickedTree.berry()?.spawnConditions?.sumOf { cond ->
            (cond.getGroveSize(random).takeIf { cond.canSpawn(berry, biome) } ) ?: 0
        } ?: 0
        var numTreesLeftToGen = numTreesToGen
        val defTreeState = BlockStateProvider.of(pickedTree.defaultState.with(BerryBlock.WAS_GENERATED, true))

        val randomTreeStateProvider = RandomizedIntBlockStateProvider(
            defTreeState, BerryBlock.AGE,
            ClampedNormalIntProvider.of(4f, 1f, 3, 5))
        val blockPlaceFeature = PlacedFeatures.createEntry(
            SIMPLE_BLOCK,
            SimpleBlockFeatureConfig(randomTreeStateProvider),
            BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(CobblemonBlockTags.BERRY_REPLACEABLE)),
            BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(Vec3i(0, -1, 0), CobblemonBlockTags.BERRY_WILD_SOIL))
        ).value()
        val possiblePositions = listOf(
            origin.north(),
            origin.north().east(),
            origin.east(),
            origin.south().east(),
            origin.south(),
            origin.south().west(),
            origin.west(),
            origin.north().west(),
            origin.up().north(),
            origin.up().north().east(),
            origin.up().east(),
            origin.up().south().east(),
            origin.up().south(),
            origin.up().south().west(),
            origin.up().west(),
            origin.up().north().west(),
            origin.down().north(),
            origin.down().north().east(),
            origin.down().east(),
            origin.down().south().east(),
            origin.down().south(),
            origin.down().south().west(),
            origin.down().west(),
            origin.down().north().west(),
        ).shuffled()
        for (dir in possiblePositions) {
            if (numTreesLeftToGen > 0) {
                if (blockPlaceFeature?.generate(worldGenLevel, context.generator, random, dir) == true) {
                    worldGenLevel.updateNeighbors(dir, worldGenLevel.getBlockState(dir).block)
                    numTreesLeftToGen--
                    val below = worldGenLevel.getBlockState(dir.down())
                    if (below.isOf(Blocks.GRASS_BLOCK) && below.get(GrassBlock.SNOWY)) {
                        worldGenLevel.setBlockState(dir.down(), below.with(GrassBlock.SNOWY, false), 2)
                    }
                    worldGenLevel.setBlockState(dir.up(), Blocks.AIR.defaultState, 2)
                }
            }
        }
        return numTreesToGen != numTreesLeftToGen
    }

}
