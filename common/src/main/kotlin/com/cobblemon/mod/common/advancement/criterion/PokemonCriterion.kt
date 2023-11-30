/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

/**
 * A criteria that is triggered when a player picks a starter.
 *
 * @author Licious, Hiroku
 * @since October 26th, 2022
 */
class PokemonCriterion : AbstractCriterion<PokemonCondition>() {

    override fun getConditionsCodec(): Codec<PokemonCondition> {
        return PokemonCondition.CODEC
    }

    fun trigger(player: ServerPlayerEntity, context: Pokemon) {
        return this.trigger(player) {
            it.matches(context)
        }
    }

}

data class PokemonCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val properties: Optional<PokemonProperties>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<PokemonCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(LootContextPredicate.CODEC, "player").forGetter(PokemonCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(PokemonProperties.CODEC, "properties").forGetter(PokemonCondition::properties)
        ).apply(it, ::PokemonCondition) }
    }

    override fun player(): Optional<LootContextPredicate> {
        return this.playerCtx
    }

    fun matches(context: Pokemon): Boolean {
        return this.properties.getOrNull()?.matches(context) ?: false
    }

}