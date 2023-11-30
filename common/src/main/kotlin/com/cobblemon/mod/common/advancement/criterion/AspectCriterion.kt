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
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class AspectCriterion : AbstractCriterion<AspectCondition>() {

    override fun getConditionsCodec(): Codec<AspectCondition> {
        return AspectCondition.CODEC
    }

    fun trigger(player: ServerPlayerEntity, context: MutableMap<Identifier, MutableSet<String>>) {
        return this.trigger(player) {
            it.matches(context)
        }
    }
}

data class AspectCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val pokemon: Optional<Identifier>,
    val aspects: Optional<List<String>>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<AspectCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(AspectCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Identifier.CODEC, "pokemon").forGetter(AspectCondition::pokemon),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING.listOf(), "aspects").forGetter(AspectCondition::aspects)
        ).apply(it, ::AspectCondition) }
    }

    override fun player(): Optional<LootContextPredicate> {
        return this.playerCtx
    }

    fun matches(context: MutableMap<Identifier, MutableSet<String>>): Boolean {
        if (!this.pokemon.isPresent) {
            return false
        }

        val caughtAspects = context.getOrDefault(pokemon.get(), mutableSetOf())
        return this.aspects.getOrNull()?.all { it in caughtAspects } ?: false
    }

}