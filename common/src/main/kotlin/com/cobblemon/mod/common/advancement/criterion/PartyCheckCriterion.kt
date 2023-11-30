/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.party
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class PartyCheckCriterion : AbstractCriterion<PartyCheckCondition>() {

    override fun getConditionsCodec() = PartyCheckCondition.CODEC

    fun trigger(player: ServerPlayerEntity) {
        return this.trigger(player) {
            it.matches(player)
        }
    }

}

data class PartyCheckCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val party: Optional<List<Identifier>>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<PartyCheckCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(PartyCheckCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Identifier.CODEC.listOf(), "party").forGetter(PartyCheckCondition::party)
        ).apply(it, ::PartyCheckCondition) }
    }

    override fun player() = this.playerCtx

    fun matches(player: ServerPlayerEntity): Boolean {
        val otherParty = this.party.orElse(listOf())
        val playerParty = player.party()
        val matches = mutableListOf<Identifier>()

        otherParty.forEach {
            if (it == "any".asIdentifierDefaultingNamespace()) {
                matches.add(it)
            }
        }
        val partyCount = playerParty.count()
        if (matches.containsAll(otherParty) && otherParty.size == partyCount && matches.size == partyCount) return true
        playerParty.iterator().forEach {
            if (otherParty.contains(it.species.resourceIdentifier)) {
                matches.add(it.species.resourceIdentifier)
            }
        }
        return matches.containsAll(otherParty) && matches.size == partyCount
    }
}