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
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class PokemonInteractCriterion : AbstractCriterion<PokemonInteractCondition>() {

    override fun getConditionsCodec(): Codec<PokemonInteractCondition> = PokemonInteractCondition.CODEC

    fun trigger(player: ServerPlayerEntity, type: Identifier, item: Identifier) {
        return this.trigger(player) {
            it.matches(player, type, item)
        }
    }

}

data class PokemonInteractCondition(
    val playerCtx: Optional<LootContextPredicate>,
    val type: Optional<String>,
    val item: Optional<String>
): AbstractCriterion.Conditions {

    companion object {
        val CODEC: Codec<PokemonInteractCondition> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(PokemonInteractCondition::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "type").forGetter(PokemonInteractCondition::type),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "item").forGetter(PokemonInteractCondition::item)
        ).apply(it, ::PokemonInteractCondition) }
    }

    override fun player() = this.playerCtx

    fun matches(player: ServerPlayerEntity, type: Identifier, item : Identifier): Boolean {
        val otherType = this.type.orElse("any")
        val otherItem = this.item.orElse("any")
        return (type == otherType.asIdentifierDefaultingNamespace() || otherType == "any") && (item == otherItem.asIdentifierDefaultingNamespace() || otherItem == "any")
    }

}