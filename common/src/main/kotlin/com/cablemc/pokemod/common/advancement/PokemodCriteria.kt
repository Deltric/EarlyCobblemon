/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.advancement

import com.cablemc.pokemod.common.advancement.criterion.*
import net.minecraft.advancement.criterion.Criteria

/**
 * Contains all the advancement criteria in Pokemod.
 *
 * @author Licious
 * @since October 26th, 2022
 */
object PokemodCriteria {

    val PICK_STARTER = Criteria.register(PickStarterCriterion())
    val CATCH_POKEMON = Criteria.register(CatchPokemonCriterion())
    val CATCH_SHINY_POKEMON = Criteria.register(CatchShinyPokemonCriterion())
    val EGG_HATCH = Criteria.register(EggHatchCriterion())
    val EVOLVE_POKEMON = Criteria.register(EvolvePokemonCriterion())
    val WIN_BATTLE = Criteria.register(BattleVictoryCriterion())

}