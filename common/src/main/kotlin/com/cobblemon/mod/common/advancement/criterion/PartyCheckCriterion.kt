/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.party
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class PartyCheckCriterion(id: Identifier, entity: LootContextPredicate) : SimpleCriterionCondition<PartyCheckContext>(id, entity){
    val party = mutableListOf<Identifier>()
    override fun toJson(json: JsonObject) {
        json.add("party", JsonArray(party.size).also {
            party.forEach { pokemon -> it.add(pokemon.toString()) }
        })
    }

    override fun fromJson(json: JsonObject) {
        party.clear()
        json.getAsJsonArray("party").forEach { element ->
            party.add(element.asString.asIdentifierDefaultingNamespace())
        }
    }

    override fun matches(player: ServerPlayerEntity, context: PartyCheckContext): Boolean {
        val playerParty = player.party()
        val matches = mutableListOf<Identifier>()
        party.forEach {
            if (it == "any".asIdentifierDefaultingNamespace()) {
                matches.add(it)
            }
        }
        val partyCount = playerParty.count()
        if (matches.containsAll(party) && party.size == partyCount && matches.size == partyCount) return true
        playerParty.iterator().forEach {
            if (party.contains(it.species.resourceIdentifier)) {
                matches.add(it.species.resourceIdentifier)
            }
        }
        return matches.containsAll(party) && matches.size == partyCount
    }
}

open class PartyCheckContext(val party : PlayerPartyStore)