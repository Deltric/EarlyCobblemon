/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.storage.party

import com.cablemc.pokemod.common.api.storage.party.PartyPosition
import com.cablemc.pokemod.common.api.storage.party.PartyPosition.Companion.readPartyPosition
import com.cablemc.pokemod.common.api.storage.party.PartyPosition.Companion.writePartyPosition
import com.cablemc.pokemod.common.net.messages.client.storage.MoveClientPokemonPacket
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Moves a Pokémon from one party place to another on the client side.
 *
 * Handled by [com.cablemc.pokemod.common.client.net.storage.party.MoveClientPartyPokemonHandler]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class MoveClientPartyPokemonPacket() : MoveClientPokemonPacket<PartyPosition>() {
    constructor(storeID: UUID, pokemonID: UUID, newPosition: PartyPosition) : this() {
        this.storeID = storeID
        this.pokemonID = pokemonID
        this.newPosition = newPosition
    }

    override fun encodePosition(buffer: PacketByteBuf, position: PartyPosition) = buffer.writePartyPosition(newPosition)
    override fun decodePosition(buffer: PacketByteBuf) = buffer.readPartyPosition()
}