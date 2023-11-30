/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class BattleCountableCriterion : AbstractCriterion<BattleCountableCriterionCondition>() {

    override fun getConditionsCodec(): Codec<BattleCountableCriterionCondition> = BattleCountableCriterionCondition.CODEC

    fun trigger(player: ServerPlayerEntity, battle: PokemonBattle, times: Int) {
        return this.trigger(player) {
            it.matches(player, battle, times)
        }
    }

}

data class BattleCountableCriterionCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val battleTypes: Optional<List<String>>,
    val times: Optional<Int>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<BattleCountableCriterionCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(BattleCountableCriterionCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING.listOf(), "battleTypes").forGetter(BattleCountableCriterionCondition::battleTypes),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "times").forGetter(BattleCountableCriterionCondition::times)
        ).apply(it, ::BattleCountableCriterionCondition) }
    }

    override fun player() = this.playerCtx

    fun matches(player: ServerPlayerEntity, battle: PokemonBattle, times: Int): Boolean {
        val otherTimes = this.times.orElse(0)
        var totalTimes = times
        val types = this.battleTypes.orElse(emptyList())

        var typeCheck = false
        val advancementData = Cobblemon.playerData.get(player).advancementData
        if (types.isEmpty() || types.contains("any")) {
            typeCheck = true
        }
        if (types.contains("pvp")) {
            typeCheck = battle.isPvP
            totalTimes = advancementData.totalPvPBattleVictoryCount
        }
        if (types.contains("pvw")) {
            typeCheck = battle.isPvW
            totalTimes = advancementData.totalPvWBattleVictoryCount
        }
        if (types.contains("pvn")) {
            typeCheck = battle.isPvN
            totalTimes = advancementData.totalPvWBattleVictoryCount
        }
        if (types.size > 1) {
            totalTimes = advancementData.totalBattleVictoryCount
        }
        return totalTimes >= otherTimes && typeCheck
    }
}