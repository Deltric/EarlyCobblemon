/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.events

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.advancement.PokemodCriteria
import com.cablemc.pokemod.common.api.battles.model.actor.ActorType
import com.cablemc.pokemod.common.api.events.battles.BattleVictoryEvent
import com.cablemc.pokemod.common.api.events.pokemon.HatchEggEvent
import com.cablemc.pokemod.common.api.events.pokemon.PokemonCapturedEvent
import com.cablemc.pokemod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cablemc.pokemod.common.api.storage.player.PlayerAdvancementDataExtension
import com.cablemc.pokemod.common.util.getPlayer
import net.minecraft.entity.player.PlayerEntity

object AdvancementHandler {

    fun onCapture(event : PokemonCapturedEvent) {
        val playerData = Pokemod.playerData.get(event.player)
        var advancementData = playerData.extraData["advancements"]
        if(advancementData == null)
        {
            advancementData = PlayerAdvancementDataExtension()
        }
        if(advancementData is PlayerAdvancementDataExtension) {
            advancementData.updateTotalCaptureCount()
            if(advancementData.getTotalCaptureCount() <= 1)
            {
                playerData.extraData["advancements"] = advancementData
            }
            playerData.extraData.replace("advancements", advancementData)
            Pokemod.playerData.saveSingle(playerData)
            PokemodCriteria.CATCH_POKEMON.trigger(event.player, advancementData.getTotalCaptureCount())
        }
    }

    fun onHatch(event: HatchEggEvent) {
        val playerData = Pokemod.playerData.get(event.player)
        var advancementData = playerData.extraData["advancements"]
        if(advancementData == null)
        {
            advancementData = PlayerAdvancementDataExtension()
        }
        if(advancementData is PlayerAdvancementDataExtension) {
            advancementData.updateTotalEggsHatched()
            if(advancementData.getTotalEggsHatched() <= 1)
            {
                playerData.extraData["advancements"] = advancementData
            }
            playerData.extraData.replace("advancements", advancementData)
            Pokemod.playerData.saveSingle(playerData)
            PokemodCriteria.EGG_HATCH.trigger(event.player, advancementData.getTotalEggsHatched())
        }
    }

    fun onEvolve(event: EvolutionCompleteEvent) {
        val playerData = event.pokemon.getOwnerPlayer()?.let { Pokemod.playerData.get(it) }
        if(playerData != null) {
            var advancementData = playerData.extraData["advancements"]
            if (advancementData == null) {
                advancementData = PlayerAdvancementDataExtension()
            }
            if (advancementData is PlayerAdvancementDataExtension) {
                advancementData.updateTotalEvolvedCount()
                if (advancementData.getTotalEvolvedCount() <= 1) {
                    playerData.extraData["advancements"] = advancementData
                }
                playerData.extraData.replace("advancements", advancementData)
                Pokemod.playerData.saveSingle(playerData)
                PokemodCriteria.EVOLVE_POKEMON.trigger(event.pokemon.getOwnerPlayer()!!, advancementData.getTotalEvolvedCount())
            }
        }
    }

    fun onWinBattle(event: BattleVictoryEvent) {
        if(event.battle.isPvW)
        {
            return
        }
        event.battle.actors.forEach {
            if(it.type != ActorType.PLAYER) {
                return
            }
            if(it.uuid.getPlayer() is PlayerEntity) {
                val player = it.uuid.getPlayer()
                if (player != null) {
                    if(!event.victorIDs.contains(player.uuid.toString())) {
                        return
                    }
                }
                val playerData = player?.let { it1 -> Pokemod.playerData.get(it1) }
                if(playerData != null) {
                    var advancementData = playerData.extraData["advancements"]
                    if (advancementData == null) {
                        advancementData = PlayerAdvancementDataExtension()
                    }
                    if (advancementData is PlayerAdvancementDataExtension) {
                        advancementData.updateTotalBattleVictoryCount()
                        if (advancementData.getTotalBattleVictoryCount() <= 1) {
                            playerData.extraData["advancements"] = advancementData
                        }
                        playerData.extraData.replace("advancements", advancementData)
                        Pokemod.playerData.saveSingle(playerData)
                        PokemodCriteria.WIN_BATTLE.trigger(player, advancementData.getTotalBattleVictoryCount())
                    }
                }
            }
        }
    }
}