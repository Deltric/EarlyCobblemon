/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.settings

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.client.net.settings.ServerSettingsPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.settings.ServerSettingsPacket

/**
 * A holder for config options the server wants to sync with the client.
 * See [ServerSettingsPacket] & [ServerSettingsPacketHandler] for more information.
 *
 * @author Licious
 * @since September 27th, 2022
 */
object ServerSettings {

    var preventCompletePartyDeposit = PokemonCobbled.config.preventCompletePartyDeposit

}