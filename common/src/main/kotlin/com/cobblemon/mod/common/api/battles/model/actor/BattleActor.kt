/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.battles.model.actor

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.battles.ActiveBattlePokemon
import com.cobblemon.mod.common.battles.BallActionResponse
import com.cobblemon.mod.common.battles.ShowdownActionRequest
import com.cobblemon.mod.common.battles.ShowdownActionResponse
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.exception.IllegalActionChoiceException
import com.cobblemon.mod.common.net.messages.client.battle.BattleMakeChoicePacket
import com.cobblemon.mod.common.net.messages.client.battle.BattleMessagePacket
import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text

abstract class BattleActor(
    val uuid: UUID,
    val pokemonList: MutableList<BattlePokemon>
) {
    init {
        pokemonList.forEach { it.actor = this }
    }

    lateinit var showdownId: String
    lateinit var battle: PokemonBattle

    val activePokemon = mutableListOf<ActiveBattlePokemon>()
    var canDynamax = false

    var request: ShowdownActionRequest? = null
    var responses = mutableListOf<ShowdownActionResponse>()
    var expectingCaptureActions = 0
    var mustChoose = false

    abstract val type: ActorType

    fun getSide() = if (this in battle.side1.actors) battle.side1 else battle.side2
    open fun getPlayerUUIDs(): Iterable<UUID> = emptyList()

    open fun isForPlayer(serverPlayerEntity: ServerPlayerEntity) = serverPlayerEntity.uuid in getPlayerUUIDs()
    open fun isForPokemon(pokemonEntity: PokemonEntity) = activePokemon.any { it.battlePokemon?.effectedPokemon?.entity == pokemonEntity }

    fun turn() {
        val request = request ?: return
        responses.clear()
        mustChoose = true
        sendUpdate(BattleMakeChoicePacket())

        val requestActive = request.active
        if (requestActive == null || requestActive.isEmpty() || request.wait) {
            this.request = null
            expectingCaptureActions = 0
            return
        }
    }

    fun upkeep() {
        val request = request ?: return
        val forceSwitchPokemon = request.forceSwitch.mapIndexedNotNull { index, b -> if (b) activePokemon[index] else null }
        if (forceSwitchPokemon.isEmpty()) {
            return
        }

        sendUpdate(BattleMakeChoicePacket())
        mustChoose = true
    }

    fun setActionResponses(responses: List<ShowdownActionResponse>) {
        val request = request ?: return
        val originalCaptureActions = expectingCaptureActions
        responses.forEachIndexed { index, response ->
            val activeBattlePokemon = activePokemon.let { if (it.size > index) it[index] else return }
            val showdownMoveSet = request.active?.let { if (it.size > index) it[index] else null }
            val forceSwitch = request.forceSwitch.let { if (it.size > index) it[index] else false }
            if (!response.isValid(activeBattlePokemon, showdownMoveSet, forceSwitch)) {
                expectingCaptureActions = originalCaptureActions
                throw IllegalActionChoiceException(this, "Invalid action choice for ${activeBattlePokemon.battlePokemon!!.getName().string}: $response")
            } else if (response is BallActionResponse) {
                expectingCaptureActions--
            }
            this.responses.add(response)
        }
        if (expectingCaptureActions > 0) {
            throw IllegalActionChoiceException(this, "Invalid action choice: a capture was expected. Are you hacking me?")
        }
        expectingCaptureActions = originalCaptureActions
        mustChoose = false
        battle.checkForInputDispatch()
    }

    fun writeShowdownResponse() {
        val showdownMessages = mutableListOf<String>()
        var index = 0
        request!!.iterate(activePokemon) { activeBattlePokemon, showdownMoveSet, forceSwitch ->
//            if (!activeBattlePokemon.isGone() && (activeBattlePokemon.isAlive() || forceSwitch)) {
                showdownMessages.add(responses[index].toShowdownString(activeBattlePokemon, showdownMoveSet))
//            }
            index++
        }
        responses.clear()
        request = null
        expectingCaptureActions = 0
        battle.writeShowdownAction(">$showdownId ${showdownMessages.joinToString()}")
    }

    abstract fun getName(): MutableText
    open fun sendMessage(component: Text) {
        sendUpdate(BattleMessagePacket(component))
    }
    open fun awardExperience(battlePokemon: BattlePokemon, experience: Int) {}
    open fun sendUpdate(packet: NetworkPacket) {}
}