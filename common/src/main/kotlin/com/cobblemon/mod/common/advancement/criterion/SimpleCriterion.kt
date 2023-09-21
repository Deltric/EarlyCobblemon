/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.google.gson.JsonObject
import java.util.Optional
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * A wrapping around the AbstractCriterion system of Minecraft. This allows the same base class to be used
 * for all advancements by wrapping the advancement-specific contextual data in the generic T, the instance of
 * the advancement conditions in the generic C, and allowing the condition to do all serialization, deserialization,
 * and matching of context against conditions.
 *
 * @author Hiroku
 * @since November 4th, 2022
 */
open class SimpleCriterionTrigger<T, C : SimpleCriterionCondition<T>>(
    val _id: Identifier,
    val criterionClass: Class<C>
) : AbstractCriterion<C>() {
    override fun conditionsFromJson(
        obj: JsonObject,
        playerPredicate: Optional<LootContextPredicate>,
        predicateDeserializer: AdvancementEntityPredicateDeserializer
    ): C {
        val instance = criterionClass.getConstructor(Optional::class.java).newInstance(playerPredicate)
        instance.fromJson(obj)
        return instance
    }

    fun trigger(player: ServerPlayerEntity, context: T) = this.trigger(player) { it.matches(player, context) }
}

/**
 * The base of the conditions for Cobblemon's wrapping around Minecraft advancements. This is parameterized by whatever
 * contextual information this condition needs to be checked, and must handle serialization and deserialization from JSON.
 *
 * @author Hiroku
 * @since November 4th, 2022
 */
abstract class SimpleCriterionCondition<T>(
    playerPredicate: Optional<LootContextPredicate>
): AbstractCriterionConditions(playerPredicate) {
    override fun toJson(): JsonObject {
        val json = super.toJson()
        toJson(json)
        return json
    }

    abstract fun toJson(json: JsonObject)
    abstract fun fromJson(json: JsonObject)

    abstract fun matches(player: ServerPlayerEntity, context: T): Boolean
}