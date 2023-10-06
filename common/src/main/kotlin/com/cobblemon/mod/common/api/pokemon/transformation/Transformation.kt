/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.transformation

import com.cobblemon.mod.common.api.pokemon.transformation.requirement.TransformationRequirement
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.ContextTrigger
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.PassiveTrigger
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.TransformationTrigger
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.transformation.triggers.ItemInteractionTrigger
import com.cobblemon.mod.common.pokemon.transformation.triggers.LevelUpTrigger
import com.cobblemon.mod.common.pokemon.transformation.triggers.TradeTrigger

/**
 * A visual and/or species change in a Pokemon.
 *
 * These can be triggered by 3 possible events: [LevelUpTrigger], [TradeTrigger] & [ItemInteractionTrigger].
 * Also see [PassiveTrigger] & [ContextTrigger].
 *
 * @author Segfault Guy
 * @since Sept 8th, 2023
 */
interface Transformation {

    /** The [TransformationTrigger] to attempt this transformation. */
    val trigger: TransformationTrigger

    /** The [TransformationRequirement]s that need to be satisfied for this transformation. */
    val requirements: Set<TransformationRequirement>

    /**
     * Checks if the given [Pokemon] passes all the conditions and is ready to transform.
     *
     * @param pokemon The [Pokemon] being queried.
     * @return If the [Transformation] can start.
     */
    fun test(pokemon: Pokemon) = this.requirements.all { requirement -> requirement.check(pokemon) }

    /**
     * Starts this transformation if requirements are met.
     *
     * @param pokemon The [Pokemon] being evolved.
     */
    fun start(pokemon: Pokemon): Boolean {
        if (this.test(pokemon)) {
            forceStart(pokemon)
            return true
        }
        return false
    }

    /**
     * Starts this transformation without evaluating the requirements.
     *
     * @param pokemon The [Pokemon] being evolved.
     */
    fun forceStart(pokemon: Pokemon) { this.requirements.forEach { it.fulfill(pokemon) } }
}
