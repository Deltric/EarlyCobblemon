/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.advancement.criterion.AspectCriterion
import com.cobblemon.mod.common.advancement.criterion.BattleCountableCriterion
import com.cobblemon.mod.common.advancement.criterion.CaughtPokemonCriterion
import com.cobblemon.mod.common.advancement.criterion.CountableCriterion
import com.cobblemon.mod.common.advancement.criterion.EvolvePokemonCriterion
import com.cobblemon.mod.common.advancement.criterion.LevelUpCriterion
import com.cobblemon.mod.common.advancement.criterion.PartyCheckCriterion
import com.cobblemon.mod.common.advancement.criterion.PokemonCriterion
import com.cobblemon.mod.common.advancement.criterion.PokemonInteractCriterion
import com.cobblemon.mod.common.advancement.criterion.TradePokemonCriterion
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.advancement.criterion.Criterion

/**
 * Contains all the advancement criteria in Cobblemon.
 *
 * @author Licious
 * @since October 26th, 2022
 */
object CobblemonCriteria {
    val PICK_STARTER = this.create("pick_starter", PokemonCriterion())

    val CATCH_POKEMON = this.create("catch_pokemon", CaughtPokemonCriterion())

    val CATCH_SHINY_POKEMON = this.create("catch_shiny_pokemon", CountableCriterion())

    /*val EGG_HATCH = this.create(
        SimpleCriterionTrigger(
            cobblemonResource("eggs_hatched"),
            SimpleCountableCriterionCondition::class.java
        )
    )*/

    val WIN_BATTLE = this.create("battle_won", BattleCountableCriterion())

    val DEFEAT_POKEMON = this.create("pokemon_defeated", CountableCriterion())

    val EVOLVE_POKEMON = this.create("pokemon_evolved", EvolvePokemonCriterion())

    val COLLECT_ASPECT = this.create("aspects_collected", AspectCriterion())

    val POKEMON_INTERACT = this.create("pokemon_interact", PokemonInteractCriterion())

    val PARTY_CHECK = this.create("party", PartyCheckCriterion())

    val LEVEL_UP = this.create("level_up", LevelUpCriterion())

    val PASTURE_USE = this.create("pasture_use", PokemonCriterion())

    val TRADE_POKEMON = this.create("trade_pokemon", TradePokemonCriterion())

    private fun <T : Criterion<*>> create(name: String, criteria: T): T = Cobblemon.implementation.registerCriteria(
        cobblemonResource(name), criteria)

}