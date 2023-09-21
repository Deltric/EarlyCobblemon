/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.Optional
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

open class AspectCriterionTrigger(identifier: Identifier, criterionClass: Class<AspectCriterionCondition>) : SimpleCriterionTrigger<MutableMap<Identifier, MutableSet<String>>, AspectCriterionCondition>(identifier, criterionClass) {

}

class AspectCriterionCondition(
    predicate: Optional<LootContextPredicate>
) : SimpleCriterionCondition<MutableMap<Identifier, MutableSet<String>>>(predicate) {
    var pokemon = Identifier("cobblemon:pikachu")
    var aspects = mutableListOf<String>()
    override fun toJson(json: JsonObject) {
        json.add("aspects", JsonArray(aspects.size).also {
            aspects.forEach { aspect -> it.add(aspect) }
        })
        json.addProperty("pokemon", pokemon.toString())
    }

    override fun fromJson(json: JsonObject) {
        aspects.clear()
        json.getAsJsonArray("aspects").forEach { element ->
            aspects.add(element.asString)
        }
        pokemon = json.get("pokemon").asString.asIdentifierDefaultingNamespace()
    }

    override fun matches(player: ServerPlayerEntity, context: MutableMap<Identifier, MutableSet<String>>): Boolean {
        val caughtAspects = context.getOrDefault(pokemon, mutableSetOf())
        return this.aspects.all { it in caughtAspects }
    }

}