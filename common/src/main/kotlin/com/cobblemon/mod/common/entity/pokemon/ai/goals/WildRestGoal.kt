/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.goals

import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer
import java.util.EnumSet
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.registry.RegistryKeys

/**
 * AI goal for sleeping in the wild.
 *
 * @author Hiroku
 * @since July 18th, 2022
 */
class WildRestGoal(private val pokemonEntity: PokemonEntity) : Goal() {
    override fun getControls(): EnumSet<Control> = EnumSet.allOf(Control::class.java)
    override fun canStart(): Boolean {
        val rest = pokemonEntity.behaviour.resting
        if (!pokemonEntity.pokemon.isWild() || pokemonEntity.random.nextFloat() < 1 - rest.sleepChance || !pokemonEntity.canSleep() || pokemonEntity.isBusy || !rest.depth.canSleep(pokemonEntity)) {
            return false
        }
        return true
    }

    override fun canStop(): Boolean {
        return false
    }

    override fun shouldContinue(): Boolean {
        return if (pokemonEntity.canSleep() && !pokemonEntity.behaviour.resting.depth.shouldWake(pokemonEntity)) {
            true
        } else {
            wake()
            false
        }
    }

    override fun start() {
        pokemonEntity.pokemon.status = PersistentStatusContainer(Statuses.SLEEP)
    }

    fun wake() {
        if (pokemonEntity.battleId.get().isEmpty) {
            pokemonEntity.pokemon.status = null
        }
    }
}