/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command.argument

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.pc
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EnumArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.StringIdentifiable.EnumCodec

class PokemonStoreArgumentType : EnumArgumentType<StoreType>(StoreType.CODEC, StoreType::values) {
    companion object {
        fun pokemonStore() = PokemonStoreArgumentType()
        fun pokemonStoreFrom(context: CommandContext<ServerCommandSource>, id: String): StoreType = context.getArgument(id, StoreType::class.java)
    }
}

enum class StoreType(val storeFetcher: (ServerPlayerEntity) -> Collection<Pokemon>) : StringIdentifiable {

    PARTY({ player -> player.party().filterNotNull() }),
    PC({ player -> player.pc().filterNotNull() }),
    ALL({ player -> PARTY.storeFetcher(player) + PC.storeFetcher(player) });

    override fun asString(): String = this.name.lowercase()

    companion object {
        val CODEC: EnumCodec<StoreType> = StringIdentifiable.createCodec(::values)
    }
}