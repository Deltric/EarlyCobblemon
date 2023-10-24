/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.server.SendOutPokemonPacket
import com.cobblemon.mod.common.pokemon.activestate.ActivePokemonState
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState
import com.cobblemon.mod.common.util.toVec3d
import com.cobblemon.mod.common.util.traceBlockCollision
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d

object SendOutPokemonHandler : ServerNetworkPacketHandler<SendOutPokemonPacket> {

    const val SEND_OUT_DURATION = 1.5F

    override fun handle(packet: SendOutPokemonPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val slot = packet.slot.takeIf { it >= 0 } ?: return
        val party = Cobblemon.storage.getParty(player)
        val pokemon = party.get(slot) ?: return
        if (pokemon.isFainted()) {
            return
        }
        val state = pokemon.state

        if (state is ShoulderedState || state !is ActivePokemonState) {
            val trace = player.traceBlockCollision(maxDistance = 15F)
            if (trace != null && !player.world.getBlockState(trace.blockPos.up()).isSolid) {
                val position = Vec3d(trace.location.x, trace.blockPos.up().toVec3d().y, trace.location.z)
                    pokemon.sendOutWithAnimation(player, player.serverWorld, position)
            }
        } else {
            val entity = state.entity
            if (entity != null) {
                entity.recallWithAnimation()
            } else {
                pokemon.recall()
            }
        }
    }
}