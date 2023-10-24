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
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import net.minecraft.world.gen.feature.FeaturePlacementContext
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier

/**
 * It's like the rarity filter placement modifier, but conditional. If condition fails, doesn't alter the stream.
 *
 * @author Hiroku
 * @since September 23rd, 2023
 */
class ConditionalRarityFilterPlacementModifier(
    val predicate: BlockPredicate,
    val chance: Int
) : AbstractConditionalPlacementModifier() {
    companion object {
        val MODIFIER_CODEC: Codec<ConditionalRarityFilterPlacementModifier> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    BlockPredicate.BASE_CODEC.fieldOf("predicate").forGetter { it.predicate },
                    PrimitiveCodec.INT.fieldOf("chance").forGetter { it.chance }
                )
                .apply(instance, ::ConditionalRarityFilterPlacementModifier)
        }
    }

    override fun getType() = CobblemonPlacementModifierTypes.CONDITIONAL_RARITY_FILTER

    override fun shouldPlace(context: FeaturePlacementContext, random: Random, pos: BlockPos): Boolean {
        return if (predicate.test(context.world, pos)) random.nextFloat() < 1.0f / this.chance.toFloat() else true
    }
}