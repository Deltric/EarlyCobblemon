/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.trade

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.TradeCompletedEvent
import com.cobblemon.mod.common.api.scheduling.after
import com.cobblemon.mod.common.net.messages.client.trade.TradeOfferExpiredPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeOfferNotificationPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeStartedPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeStartedPacket.TradeablePokemon
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.lang
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object TradeManager {
    class TradeRequest(val tradeOfferId: UUID, val senderId: UUID, val receiverId: UUID)

    val requests = mutableListOf<TradeRequest>()
    val activeTrades = mutableListOf<ActiveTrade>()

    fun getExistingRequest(playerId: UUID) = requests.find { it.senderId == playerId || it.receiverId == playerId }
    fun getActiveTrade(playerId: UUID) =
        activeTrades.find { it.player1.uuid == playerId || it.player2.uuid == playerId }

    fun offerTrade(player: ServerPlayerEntity, otherPlayerEntity: ServerPlayerEntity) {
        val existingFromPlayer = requests.find { it.senderId == player.uuid }
        if (existingFromPlayer != null) {
            existingFromPlayer.receiverId.getPlayer()?.sendPacket(TradeOfferExpiredPacket(existingFromPlayer.tradeOfferId))
        }
        if (getActiveTrade(otherPlayerEntity.uuid) != null) {
            player.sendMessage(lang("trade.occupied", otherPlayerEntity.name), true)
        } else {
            val request = TradeRequest(UUID.randomUUID(), player.uuid, otherPlayerEntity.uuid)
            requests.add(request)
            after(seconds = 60F) {
                if (requests.remove(request)) {
                    player.sendMessage(lang("trade.request_expired", otherPlayerEntity.name), true)
                }
            }

            otherPlayerEntity.sendPacket(TradeOfferNotificationPacket(request.tradeOfferId, player.uuid, player.name.copy()))
            player.sendMessage(lang("trade.request_sent", otherPlayerEntity.name), true)
        }
    }

    fun acceptTradeRequest(player: ServerPlayerEntity, tradeOfferId: UUID) {
        val request = requests.find { it.tradeOfferId == tradeOfferId }
        if (request == null) {
            player.sendMessage(lang("trade.request_already_expired"), true)
        } else {
            requests.remove(request)
            val otherPlayer = request.senderId.getPlayer() ?: return
            val trade = ActiveTrade(PlayerTradeParticipant(player), PlayerTradeParticipant(otherPlayer))
            activeTrades.add(trade)
            player.sendPacket(TradeStartedPacket(otherPlayer.uuid, otherPlayer.name.copy(), trade.player2.party.mapNullPreserving(::TradeablePokemon)))
            otherPlayer.sendPacket(TradeStartedPacket(player.uuid, player.name.copy(), trade.player1.party.mapNullPreserving(::TradeablePokemon)))
        }
    }

    fun onLogoff(player: ServerPlayerEntity) {
        val request = requests.find { it.senderId == player.uuid || it.receiverId == player.uuid }
        if (request != null) {
            val otherPlayer = if (request.receiverId == player.uuid) request.senderId.getPlayer() else request.receiverId.getPlayer()
            otherPlayer?.sendPacket(TradeOfferExpiredPacket(request.tradeOfferId))
            requests.remove(request)
        }

        val trade = getActiveTrade(player.uuid)
        if (trade != null) {
            val tradeParticipant = trade.getTradeParticipant(player.uuid)
            val oppositeParticipant = trade.getOppositePlayer(tradeParticipant)
            oppositeParticipant.cancelTrade(trade)
            activeTrades.remove(trade)
        }
    }

    fun performTrade(player1: TradeParticipant, pokemon1: Pokemon, player2: TradeParticipant, pokemon2: Pokemon) {
        val party1 = player1.party
        val party2 = player2.party

        party1.remove(pokemon1)
        party2.remove(pokemon2)

        pokemon1.setFriendship(0)
        pokemon2.setFriendship(0)

        party2.add(pokemon1)
        party1.add(pokemon2)

        pokemon1.evolutions.filterIsInstance<TradeEvolution>().firstOrNull {
            it.attemptEvolution(pokemon1, pokemon2)
        }

        pokemon2.evolutions.filterIsInstance<TradeEvolution>().firstOrNull {
            it.attemptEvolution(pokemon2, pokemon1)
        }
        CobblemonEvents.TRADE_COMPLETED.post(TradeCompletedEvent(player1, pokemon2, player2, pokemon1))
    }
}