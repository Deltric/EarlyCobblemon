/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import java.util.Optional
import kotlin.jvm.optionals.getOrDefault

class EvolvePokemonCriterion : AbstractCriterion<EvolvePokemonCondition>() {

    override fun getConditionsCodec(): Codec<EvolvePokemonCondition> {
        return EvolvePokemonCondition.CODEC
    }

    fun trigger(player: ServerPlayerEntity, context: EvolvePokemonContext) {
        return this.trigger(player) {
            it.matches(context)
        }
    }

}

data class EvolvePokemonCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val species: Optional<String>,
    val evolution: Optional<String>,
    val times: Optional<Int>
) : AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<EvolvePokemonCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(EvolvePokemonCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "species").forGetter(EvolvePokemonCondition::species),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "evolution").forGetter(EvolvePokemonCondition::evolution),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "times").forGetter(EvolvePokemonCondition::times)
        ).apply(it, ::EvolvePokemonCondition) }
    }

    override fun player(): Optional<LootContextPredicate> {
        return this.playerCtx
    }

    fun matches(context: EvolvePokemonContext): Boolean {
        val species = this.species.getOrDefault("any")
        val evolution = this.evolution.getOrDefault("any")
        val count = this.times.getOrDefault(0)

        return context.times >= count && (context.species == species.asIdentifierDefaultingNamespace() || species == "any") &&
                (context.evolution == evolution.asIdentifierDefaultingNamespace() || evolution == "any")
    }
}

open class EvolvePokemonContext(val species : Identifier, val evolution : Identifier, val times: Int)