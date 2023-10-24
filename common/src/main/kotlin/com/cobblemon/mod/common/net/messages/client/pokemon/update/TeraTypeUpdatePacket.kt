/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.pokemon.update

import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

/**
 * Updates the Tera Type of the Pokémon.
 *
 * @author Segfault Guy
 * @since July 19, 2023
 */
class TeraTypeUpdatePacket(pokemon: () -> Pokemon, value: ElementalType) : SingleUpdatePacket<ElementalType, TeraTypeUpdatePacket>(pokemon, value) {
    override val id = ID

    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeString(value.name)
    }

    override fun set(pokemon: Pokemon, value: ElementalType) {
        pokemon.teraType = value
    }

    companion object {
        val ID = cobblemonResource("tera_type_update")
        fun decode(buffer: PacketByteBuf) = TeraTypeUpdatePacket(decodePokemon(buffer), ElementalTypes.getOrException(buffer.readString()))
    }
}
