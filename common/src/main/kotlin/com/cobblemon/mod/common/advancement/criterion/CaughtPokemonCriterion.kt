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

class CaughtPokemonCriterion : AbstractCriterion<CaughtPokemonCondition>() {

    override fun getConditionsCodec(): Codec<CaughtPokemonCondition> = CaughtPokemonCondition.CODEC

    fun trigger(player: ServerPlayerEntity, type: String, times: Int) {
        return this.trigger(player) {
            it.matches(type, times)
        }
    }

}

data class CaughtPokemonCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val type: Optional<String>,
    val times: Optional<Int>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<CaughtPokemonCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(CaughtPokemonCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "type").forGetter(CaughtPokemonCondition::type),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "times").forGetter(CaughtPokemonCondition::times)
        ).apply(it, ::CaughtPokemonCondition) }
    }

    override fun player() = this.playerCtx

    fun matches(type: String, times: Int): Boolean {
        val otherType = this.type.orElse("any")
        val otherTimes = this.times.orElse(0)

        return times >= otherTimes && (type == otherType || otherType == "any")
    }

}