/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.JsonObject
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class EvolvePokemonCriterionCondition(id: Identifier, entity: LootContextPredicate) : CountableCriterionCondition<EvolvePokemonContext>(id, entity) {
    var species = "any"
    var evolution = "any"
    override fun toJson(json: JsonObject) {
        super.toJson(json)
        json.addProperty("species", species)
        json.addProperty("evolution", evolution)
    }

    override fun fromJson(json: JsonObject) {
        super.fromJson(json)
        species = json.get("species")?.asString ?: "any"
        evolution = json.get("evolution")?.asString ?: "any"
    }

    override fun matches(player: ServerPlayerEntity, context: EvolvePokemonContext): Boolean {
        return context.times >= count && (context.species == species.asIdentifierDefaultingNamespace() || species == "any") &&
                (context.evolution == evolution.asIdentifierDefaultingNamespace() || evolution == "any")
    }
}

open class EvolvePokemonContext(val species : Identifier, val evolution : Identifier, times: Int) : CountableContext(times)