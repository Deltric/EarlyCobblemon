/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class LevelUpCriterion : AbstractCriterion<LevelUpCondition>() {

    override fun getConditionsCodec(): Codec<LevelUpCondition> = LevelUpCondition.CODEC

    fun trigger(player: ServerPlayerEntity, level: Int, pokemon: Pokemon) {
        return this.trigger(player) {
            it.matches(player, level, pokemon)
        }
    }

}

data class LevelUpCondition(
    val playerCtx: Optional<LootContextPredicate>,
    var level: Optional<Int>,
    var evolved: Optional<Boolean>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<LevelUpCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(LevelUpCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "level").forGetter(LevelUpCondition::level),
            Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "evolved").forGetter(LevelUpCondition::evolved)
        ).apply(it, ::LevelUpCondition) }
    }

    override fun player() = this.playerCtx

    fun matches(player: ServerPlayerEntity, level : Int, pokemon : Pokemon): Boolean {
        val otherLevel = this.level.orElse(0)
        val otherEvolved = this.evolved.orElse(true)

        val preEvo = pokemon.preEvolution == null
        val hasEvolution = !pokemon.evolutions.none()
        var evolutionCheck = true

        if (preEvo || hasEvolution) {
            evolutionCheck = preEvo != hasEvolution
        }
        return otherLevel == level && evolutionCheck == otherEvolved
    }
}