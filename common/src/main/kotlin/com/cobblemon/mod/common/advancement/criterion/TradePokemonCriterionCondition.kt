/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.JsonObject
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class TradePokemonCriterionCondition(id: Identifier, entity: LootContextPredicate) : SimpleCriterionCondition<TradePokemonContext>(id, entity) {
    var traded = "any"
    var received = "any"
    var tradedHeldItem = "any"
    var receivedHeldItem = "any"
    override fun toJson(json: JsonObject) {
        json.addProperty("traded", traded)
        json.addProperty("received", received)
        json.addProperty("traded_held_item", tradedHeldItem)
        json.addProperty("received_held_item", receivedHeldItem)
    }

    override fun fromJson(json: JsonObject) {
        traded = json.get("traded")?.asString ?: "any"
        received = json.get("received")?.asString ?: "any"
        tradedHeldItem = json.get("traded_held_item")?.asString ?: "minecraft:air"
        receivedHeldItem = json.get("received_held_item")?.asString ?: "minecraft:air"
    }

    override fun matches(player: ServerPlayerEntity, context: TradePokemonContext): Boolean {
        val heldItem1 = context.traded.heldItem().item.registryEntry.registryKey().value
        val heldItem2 = context.received.heldItem().item.registryEntry.registryKey().value
        return (context.traded.species.resourceIdentifier == traded.asIdentifierDefaultingNamespace() || traded == "any") &&
                (context.received.species.resourceIdentifier == received.asIdentifierDefaultingNamespace() || received == "any") &&
                (heldItem1 == tradedHeldItem.asIdentifierDefaultingNamespace() || heldItem1 == "minecraft:air".asIdentifierDefaultingNamespace()) &&
                (heldItem2 == receivedHeldItem.asIdentifierDefaultingNamespace() || heldItem2 == "minecraft:air".asIdentifierDefaultingNamespace())
    }
}

open class TradePokemonContext(val traded: Pokemon, val received: Pokemon)