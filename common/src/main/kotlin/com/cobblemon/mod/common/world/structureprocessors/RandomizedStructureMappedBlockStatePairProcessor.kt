/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.structureprocessors

import com.cobblemon.mod.common.util.codec.pairCodec
import com.cobblemon.mod.common.world.placementmodifier.BlockStateTransformer
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.processor.StructureProcessor
import net.minecraft.structure.rule.RuleTest
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.WorldView

/**
 * A [StructureProcessor] that picks a [Block] pair per structure to replace other blocks with
 *
 * Basically, each structure is assigned a random pair of blocks from the given possibilities.
 * If a block matches any of the rules, it is overwritten by one of the two BlockStates assigned to the
 * blocks structure
 *
 * @param targetBlockPairs The block pairs for a structure to select from
 * @param transformer The block state transformer to apply to the blocks before they place.
 * @param rules The list of rules to check for each block
 * @param probability The probability for this processor to run
 */
class RandomizedStructureMappedBlockStatePairProcessor(
    val targetBlockPairs: List<Pair<Block, Block>>,
    val transformer: BlockStateTransformer,
    val rules: List<RuleTest>,
    val probability: Float = 1.0f
) : StructureProcessor() {
    override fun process(
        world: WorldView,
        pos: BlockPos,
        pivot: BlockPos,
        originalBlockInfo: StructureTemplate.StructureBlockInfo,
        currentBlockInfo: StructureTemplate.StructureBlockInfo,
        data: StructurePlacementData
    ): StructureTemplate.StructureBlockInfo {
        val random = Random.create(pivot.hashCode().toLong())
        val active = random.nextFloat() < probability
        if (!active) {
            return currentBlockInfo
        }

        for (rule in rules) {
            if (rule.test(currentBlockInfo.state, data.getRandom(pos))) {
                val (block1, block2) = targetBlockPairs[random.nextInt(targetBlockPairs.size)]
                val targetState = setOf(block1, block2).random().defaultState.let(transformer::transform)
                return StructureTemplate.StructureBlockInfo(
                    currentBlockInfo.pos,
                    targetState,
                    currentBlockInfo.nbt
                )
            }
        }
        return currentBlockInfo
    }

    override fun getType() = CobblemonProcessorTypes.RANDOM_POOLED_STATES

    companion object {
        val CODEC: Codec<RandomizedStructureMappedBlockStatePairProcessor> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    pairCodec(Registries.BLOCK.codec, Registries.BLOCK.codec).listOf().fieldOf("targetBlockPairs").forGetter { it.targetBlockPairs },
                    BlockStateTransformer.codec.fieldOf("transformer").forGetter { it.transformer },
                    RuleTest.TYPE_CODEC.listOf().fieldOf("rules").forGetter { it.rules },
                    PrimitiveCodec.FLOAT.fieldOf("probability").forGetter { it.probability }
                )
                .apply(instance) { targetBlockPairs, transformer, rules, probability ->
                    RandomizedStructureMappedBlockStatePairProcessor(
                        targetBlockPairs,
                        transformer,
                        rules,
                        probability
                    )
                }
        }
    }
}
