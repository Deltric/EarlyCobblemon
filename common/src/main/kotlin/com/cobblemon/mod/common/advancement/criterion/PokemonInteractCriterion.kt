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

open class PokemonInteractContext(var type: Identifier, var item : Identifier)
class PokemonInteractCriterion(id: Identifier, entity: LootContextPredicate) : SimpleCriterionCondition<PokemonInteractContext>(id, entity) {
    var type = "any"
    var item = "any"
    override fun toJson(json: JsonObject) {
        json.addProperty("type", type)
        json.addProperty("item", item)
    }

    override fun fromJson(json: JsonObject) {
        type = json.get("type")?.asString ?: "any"
        item = json.get("item")?.asString ?: "any"
    }

    override fun matches(player: ServerPlayerEntity, context: PokemonInteractContext): Boolean {
        return (context.type == type.asIdentifierDefaultingNamespace() || type == "any") && (context.item == item.asIdentifierDefaultingNamespace() || item == "any")
    }
}