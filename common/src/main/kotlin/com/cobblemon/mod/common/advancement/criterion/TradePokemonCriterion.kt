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
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class TradePokemonCriterion : AbstractCriterion<TradePokemonCondition>() {

    override fun getConditionsCodec(): Codec<TradePokemonCondition> = TradePokemonCondition.CODEC

    fun trigger(player: ServerPlayerEntity, traded: Pokemon, received: Pokemon) {
        return this.trigger(player) {
            it.matches(player, traded, received)
        }
    }

}

data class TradePokemonCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val traded: Optional<String>,
    val received: Optional<String>,
    val tradedHeldItem: Optional<String>,
    val receivedHeldItem: Optional<String>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<TradePokemonCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(TradePokemonCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "traded").forGetter(TradePokemonCondition::traded),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "received").forGetter(TradePokemonCondition::received),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "traded_held_item").forGetter(TradePokemonCondition::tradedHeldItem),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "received_held_item").forGetter(TradePokemonCondition::receivedHeldItem)
        ).apply(it, ::TradePokemonCondition) }
    }

    override fun player() = this.playerCtx

    fun matches(player: ServerPlayerEntity, traded: Pokemon, received: Pokemon): Boolean {
        val otherTraded = this.traded.orElse("any")
        val otherReceived = this.received.orElse("any")
        val otherTradedHeldItem = this.tradedHeldItem.orElse("minecraft:air")
        val otherReceivedHeldItem = this.receivedHeldItem.orElse("minecraft:air")

        val heldItem1 = traded.heldItem().item.registryEntry.registryKey().value
        val heldItem2 = received.heldItem().item.registryEntry.registryKey().value

        return (traded.species.resourceIdentifier == otherTraded.asIdentifierDefaultingNamespace() || otherTraded == "any") &&
                (received.species.resourceIdentifier == otherReceived.asIdentifierDefaultingNamespace() || otherReceived == "any") &&
                (heldItem1 == otherTradedHeldItem.asIdentifierDefaultingNamespace() || heldItem1 == "minecraft:air".asIdentifierDefaultingNamespace()) &&
                (heldItem2 == otherReceivedHeldItem.asIdentifierDefaultingNamespace() || heldItem2 == "minecraft:air".asIdentifierDefaultingNamespace())
    }
}