/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class CountableCriterion : AbstractCriterion<CountableCriterionCondition>() {

    override fun getConditionsCodec(): Codec<CountableCriterionCondition> = CountableCriterionCondition.CODEC

    fun trigger(player: ServerPlayerEntity, times: Int) {
        return this.trigger(player) {
            it.matches(times)
        }
    }

}

data class CountableCriterionCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val times: Optional<Int>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<CountableCriterionCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(CountableCriterionCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "times").forGetter(CountableCriterionCondition::times)
        ).apply(it, ::CountableCriterionCondition) }
    }

    override fun player() = this.playerCtx

    fun matches(times: Int): Boolean {
        val otherTimes = this.times.orElse(0)

        return times >= otherTimes
    }
}