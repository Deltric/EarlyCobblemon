/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.wheel

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket
import com.cobblemon.mod.common.net.messages.server.pokemon.interact.InteractPokemonPacket
import com.cobblemon.mod.common.net.messages.server.trade.AcceptTradeRequestPacket
import com.cobblemon.mod.common.net.messages.server.trade.OfferTradePacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import org.joml.Vector3f

fun createPokemonInteractGui(pokemonID: UUID, canMountShoulder: Boolean, canRide: Boolean): InteractWheelGUI {
    val mountShoulder = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_shoulder.png"),
        onPress = {
            if (canMountShoulder) {
                InteractPokemonPacket(pokemonID, true, false).sendToServer()
                closeGUI()
            }
        }
    )
    val giveItem = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_held_item.png"),
        onPress = {
            InteractPokemonPacket(pokemonID, false, false).sendToServer()
            closeGUI()
        }
    )

    val ride = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_held_item.png"),
        onPress = {
            if (canRide) {
                InteractPokemonPacket(pokemonID, false, true).sendToServer()
                closeGUI()
            }
        }
    )

    val options = mutableMapOf(
        Orientation.TOP_RIGHT to giveItem,
        Orientation.BOTTOM_LEFT to ride
    )
    if (canMountShoulder) {
        options[Orientation.TOP_LEFT] = mountShoulder
    }
    return InteractWheelGUI(options, Text.translatable("cobblemon.ui.interact.pokemon"))
}

fun createPlayerInteractGui(targetPlayer: PlayerEntity, pokemon: Pokemon): InteractWheelGUI {
    val trade = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_trade.png"),
        colour = { if (CobblemonClient.requests.tradeOffers.any { it.traderId == targetPlayer.uuid }) Vector3f(0F, 0.6F, 0F) else null },
        onPress = {
            val tradeOffer = CobblemonClient.requests.tradeOffers.find { it.traderId == targetPlayer.uuid }
            if (tradeOffer == null) {
                CobblemonNetwork.sendToServer(OfferTradePacket(targetPlayer.uuid))
            } else {
                CobblemonClient.requests.tradeOffers -= tradeOffer
                CobblemonNetwork.sendToServer(AcceptTradeRequestPacket(tradeOffer.tradeOfferId))
            }
            closeGUI()
        }
    )
    val battle = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"),
        colour = { if (CobblemonClient.requests.battleChallenges.any { it.challengerId == targetPlayer.uuid }) Vector3f(0F, 0.6F, 0F) else null },
        onPress = {
            val battleRequest = CobblemonClient.requests.battleChallenges.find { it.challengerId == targetPlayer.uuid }
            // This can be improved in future with more detailed battle challenge data.
            BattleChallengePacket(targetPlayer.id, pokemon.uuid).sendToServer()
            closeGUI()
        }
    )
    val options = mutableMapOf(
        Orientation.TOP_LEFT to trade,
        Orientation.TOP_RIGHT to battle
    )
    return InteractWheelGUI(options, Text.translatable("cobblemon.ui.interact.player"))
}

private fun closeGUI() {
    MinecraftClient.getInstance().setScreen(null)
}