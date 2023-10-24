/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.placementmodifier

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.stream.Stream
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import net.minecraft.world.gen.feature.FeaturePlacementContext
import net.minecraft.world.gen.placementmodifier.PlacementModifier

/**
 * It's like the count placement modifier, but conditional. If condition fails, doesn't alter the stream.
 *
 * @author Hiroku
 * @since September 23rd, 2023
 */
class ConditionalCountPlacementModifier(
    val predicate: BlockPredicate,
    val count: Int
) : PlacementModifier() {
    companion object {
        val MODIFIER_CODEC: Codec<ConditionalCountPlacementModifier> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    BlockPredicate.BASE_CODEC.fieldOf("predicate").forGetter { it.predicate },
                    PrimitiveCodec.INT.fieldOf("count").forGetter { it.count }
                )
                .apply(instance, ::ConditionalCountPlacementModifier)
        }
    }

    override fun getType() = CobblemonPlacementModifierTypes.CONDITIONAL_COUNT
    override fun getPositions(context: FeaturePlacementContext, random: Random, pos: BlockPos): Stream<BlockPos> {
        return if (predicate.test(context.world, pos)) {
            val new = mutableListOf<BlockPos>()
            repeat(times = count) { new.add(pos) }
            new.stream()
        } else {
            Stream.of(pos)
        }
    }
}