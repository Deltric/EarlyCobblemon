/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.pokemon.update.evolution

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.pokemon.transformation.evolution.EvolutionDisplay
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import java.util.*

class AcceptEvolutionPacket(val pokemonUUID: UUID, val evolutionId: String) : NetworkPacket<AcceptEvolutionPacket> {

    constructor(pokemon: Pokemon, evolution: EvolutionDisplay) : this(pokemon.uuid, evolution.id)

    override val id = ID

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(this.pokemonUUID)
        buffer.writeString(this.evolutionId)
    }

    companion object {
        val ID = cobblemonResource("accept_evolution")
        fun decode(buffer: PacketByteBuf) = AcceptEvolutionPacket(buffer.readUuid(), buffer.readString())
    }
}