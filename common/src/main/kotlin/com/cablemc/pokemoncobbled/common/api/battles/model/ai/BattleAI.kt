/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.battles.model.ai

import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionResponse
import com.cablemc.pokemoncobbled.common.battles.ShowdownMoveset
import java.util.UUID

/**
 * Interface for an actors battle AI
 *
 * @since January 16th, 2022
 * @author Deltric
 */
interface BattleAI {
    /**
     * Requests that the AI choose an action for the given Pokémon
     * @param activeBattlePokemon The Pokémon slot that is choosing an action
     * @param moveset The [ShowdownMoveset] for this slot. This can be null if [forceSwitch] is true. Otherwise it is the available move information from Showdown.
     * @param forceSwitch Whether or not this is a force switch situation.
     * @return the action response
     */
    fun choose(activeBattlePokemon: ActiveBattlePokemon, moveset: ShowdownMoveset?, forceSwitch: Boolean): ShowdownActionResponse
}