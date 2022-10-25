/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.net.NetworkPacket

/**
 * A simple packet handler as an SAM interface. If this packet handler is invoked, it
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
interface PacketHandler<T: NetworkPacket> {
    operator fun invoke(packet: T, ctx: PokemodNetwork.NetworkContext)
}
