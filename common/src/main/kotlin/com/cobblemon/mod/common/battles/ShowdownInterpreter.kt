/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.interpreter.*
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.scheduling.after
import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.battles.dispatch.BattleDispatch
import com.cobblemon.mod.common.battles.dispatch.DispatchResult
import com.cobblemon.mod.common.battles.dispatch.GO
import com.cobblemon.mod.common.battles.dispatch.UntilDispatch
import com.cobblemon.mod.common.battles.dispatch.WaitDispatch
import com.cobblemon.mod.common.battles.interpreter.ContextManager
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.net.messages.client.battle.*
import com.cobblemon.mod.common.pokemon.evolution.progress.DamageTakenEvolutionProgress
import com.cobblemon.mod.common.pokemon.evolution.progress.RecoilEvolutionProgress
import com.cobblemon.mod.common.pokemon.evolution.progress.UseMoveEvolutionProgress
import com.cobblemon.mod.common.pokemon.status.PersistentStatus
import com.cobblemon.mod.common.util.*
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Text

object ShowdownInterpreter {
    private val updateInstructions = mutableMapOf<String, (PokemonBattle, BattleMessage, MutableList<String>) -> Unit>()
    private val sideUpdateInstructions = mutableMapOf<String, (PokemonBattle, BattleActor, BattleMessage) -> Unit>()
    private val splitUpdateInstructions = mutableMapOf<String, (PokemonBattle, BattleActor, BattleMessage, BattleMessage) -> Unit>()
    // Stores a reference to the previous ability, activate, or move message in a battle so a minor action can refer back to it (Battle UUID :  BattleMessage)
    private val lastCauser = mutableMapOf<UUID, BattleMessage>()

    init {
        // Note '-cureteam' is a legacy thing that is only used in generation 2 and 4 mods for heal bell and aromatherapy respectively as such we can just ignore that
        updateInstructions["|player|"] = this::handlePlayerInstruction
        updateInstructions["|teamsize|"] = this::handleTeamSizeInstruction
        updateInstructions["|gametype|"] = this::handleGameTypeInstruction
        updateInstructions["|gen|"] = this::handleGenInstruction
        updateInstructions["|tier|"] = this::handleTierInstruction
        updateInstructions["|rated"] = this::handleRatedInstruction
        updateInstructions["|rule|"] = this::handleRuleInstruction
        updateInstructions["|clearpoke"] = this::handleClearPokeInstruction
        updateInstructions["|poke|"] = this::handlePokeInstruction
        updateInstructions["|teampreview"] = this::handleTeamPreviewInstruction
        updateInstructions["|start"] = this::handleStartInstruction
        updateInstructions["|turn|"] = this::handleTurnInstruction
        updateInstructions["|upkeep"] = this::handleUpkeepInstruction
        updateInstructions["|faint|"] = this::handleFaintInstruction
        updateInstructions["|win|"] = this::handleWinInstruction
        updateInstructions["|move|"] = this::handleMoveInstruction
        updateInstructions["|cant|"] = this::handleCantInstruction
        updateInstructions["|bagitem|"] = this::handleBagItemInstruction
        updateInstructions["|-supereffective|"] = this::handleSuperEffectiveInstruction
        updateInstructions["|-resisted|"] = this::handleResistInstruction
        updateInstructions["|-crit"] = this::handleCritInstruction
        updateInstructions["|-weather|"] = this::handleWeatherInstruction
        updateInstructions["|-mustrecharge|"] = this::handleRechargeInstructions
        updateInstructions["|-fail|"] = this::handleFailInstruction
        updateInstructions["|-start|"] = this::handleStartInstructions
        updateInstructions["|-activate|"] = this::handleActivateInstructions
        updateInstructions["|-curestatus|"] = this::handleCureStatusInstruction
        updateInstructions["|-fieldstart|"] = this::handleFieldStartInstructions
        updateInstructions["|-fieldend|"] = this::handleFieldEndInstructions
        updateInstructions["|-ability|"] = this::handleAbilityInstructions
        //To-Do updateInstructions["|-endability|"] = this::handleEndAbilityInstruction
        updateInstructions["|-nothing"] = { battle, _, _ ->
            battle.dispatchGo { battle.broadcastChatMessage(battleLang("nothing")) }
        }
        updateInstructions["|-clearallboost"] = this::handleClearAllBoostInstructions
        updateInstructions["|-singleturn|"] = this::handleSingleTurnInstruction
        updateInstructions["|-singlemove|"] = this::handleSingleMoveInstruction
        updateInstructions["|-prepare|"] = this::handlePrepareInstruction
        updateInstructions["|-swapboost"] = this::handleSwapBoostInstruction
        updateInstructions["|-swapsideconditions|"] = this::handleSilently
        updateInstructions["|-unboost|"] = { battle, line, remainingLines -> boostInstruction(battle, line, remainingLines, false) }
        updateInstructions["|-boost|"] = { battle, line, remainingLines -> boostInstruction(battle, line, remainingLines, true) }
        updateInstructions["|-setboost|"] = this::handleSetBoostInstruction
        updateInstructions["|t:|"] = {_, _, _ -> }
        updateInstructions["|pp_update|"] = this::handlePpUpdateInstruction
        updateInstructions["|-immune"] = this::handleImmuneInstruction
        updateInstructions["|-status|"] = this::handleStatusInstruction
        updateInstructions["|-end|"] = this::handleEndInstruction
        updateInstructions["|-miss|"] = this::handleMissInstruction
        updateInstructions["|-hitcount|"] = this::handleHitCountInstruction
        updateInstructions["|-item|"] = this::handleItemInstruction
        updateInstructions["|-enditem|"] = this::handleEndItemInstruction
        updateInstructions["|-sidestart|"] = this::handleSideStartInstructions
        updateInstructions["|-sideend|"] = this::handleSideEndInstructions
        updateInstructions["|-fieldactivate|"] = this::handleFieldActivateInstructions
        updateInstructions["|-clearnegativeboost|"] = this::handleClearNegativeBoostInstructions
        updateInstructions["|-zpower|"] = this::handleZPowerInstructions
        updateInstructions["|-zbroken|"] = this::handleZBrokenInstructions
        updateInstructions["|-terastallize|"] = this::handleTerastallizeInstructions
        updateInstructions["|detailschange|"] = this::handleDetailsChangeInstructions
        updateInstructions["|-mega|"] = this::handleMegaInstructions

        sideUpdateInstructions["|request|"] = this::handleRequestInstruction
        splitUpdateInstructions["|switch|"] = this::handleSwitchInstruction
        splitUpdateInstructions["|-damage|"] = this::handleDamageInstruction
        splitUpdateInstructions["|drag|"] = this::handleDragInstruction
        splitUpdateInstructions["|-heal|"] = this::handleHealInstruction
        splitUpdateInstructions["|-sethp|"] = this::handleSetHpInstructions
        sideUpdateInstructions["|error|"] = this::handleErrorInstructions
    }

    /**
     * Format:
     * |-boost|POKEMON|STAT|AMOUNT && |-unboost|POKEMON|STAT|AMOUNT
     *
     * The specified Pokémon POKEMON has gained or lost AMOUNT in STAT, using the standard rules for Pokémon stat changes in-battle.
     * STAT is a standard three-letter abbreviation fot the stat in question.
     */
    private fun boostInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>, isBoost: Boolean) {
        val pokemon = message.getBattlePokemon(0, battle) ?: return
        val statKey = message.argumentAt(1) ?: return
        val stages = message.argumentAt(2)?.toInt() ?: return
        val stat = getStat(statKey).displayName
        val severity = getSeverity(stages)
        val rootKey = if (isBoost) "boost" else "unboost"

        if (stages == 0) {
            val othersExist = remainingLines.removeIf {
                val isAlsoBoost = it.startsWith(if (isBoost) "|-boost" else "|-unboost")
                // Same type boost targeting the same person and both zero
                return@removeIf isAlsoBoost && it.split("|")[2] == message.rawMessage.split("|")[2] && it.split("|")[4] == "0"
            }
            if (othersExist) {
                battle.dispatchGo {
                    battle.broadcastChatMessage(battleLang("$rootKey.cap.multiple", pokemon.getName()))
                }
                return
            }
        }

        battle.dispatchWaiting(1.5F) {
            val lang = when {
                message.hasOptionalArgument("zeffect") -> battleLang("$rootKey.$severity.zeffect", pokemon.getName(), stat)
                else -> battleLang("$rootKey.$severity", pokemon.getName(), stat)
            }
            battle.broadcastChatMessage(lang)

            val boostBucket = if (isBoost) BattleContext.Type.BOOST else BattleContext.Type.UNBOOST
            val context = getContextFromAction(message, boostBucket, battle)
            // TODO: replace with context that tracks detailed information such as # of stages
            repeat(stages) { pokemon.contextManager.add(context) }
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun getStat(statKey: String) = when(statKey) {
        "atk" -> Stats.ATTACK
        "def" -> Stats.DEFENCE
        "spa" -> Stats.SPECIAL_ATTACK
        "spd" -> Stats.SPECIAL_DEFENCE
        "spe" -> Stats.SPEED
        "evasion" -> Stats.EVASION
        else -> Stats.ACCURACY
    }

    private fun getSeverity(stages: Int) = when(stages) {
        0 -> "cap.single"
        1 -> "slight"
        2 -> "sharp"
        else -> "severe"
    }

    /**
     * Format:
     * |-setboost|POKEMON|STAT|AMOUNT
     *
     * Same as -boost and -unboost, but STAT is set to AMOUNT instead of boosted by AMOUNT.
     */
    private fun handleSetBoostInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val effectID = message.effect()?.id ?: return@dispatchWaiting
            val lang = battleLang("setboost.$effectID", pokemonName)
            battle.broadcastChatMessage(lang)
            pokemon.contextManager.add(getContextFromAction(message, BattleContext.Type.BOOST, battle))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    fun interpretMessage(battleId: UUID, message: String) {
        // Check key map and use function if matching
        if (message.startsWith("{\"winner\":\"")) {
            // The post-win message is something we don't care about just yet. It's basically a summary of what happened in the battle.
            // Check /docs/example-post-win-message.json for its format.
            return
        }

        val battle = BattleRegistry.getBattle(battleId)

        if (battle == null) {
            LOGGER.info("No battle could be found with the id: $battleId")
            return
        }

        runOnServer {
            battle.showdownMessages.add(message)
            interpret(battle, message)
        }
    }

    fun interpret(battle: PokemonBattle, rawMessage: String) {
        battle.log()
        battle.log(rawMessage)
        battle.log()
        try {
            val lines = rawMessage.split("\n").toMutableList()
            if (lines[0] == "update") {
                lines.removeAt(0)
                while (lines.isNotEmpty()) {
                    val line = lines.removeAt(0)

                    // Split blocks have a public and private message below
                    if (line.startsWith("|split|")) {
                        val showdownId = line.split("|split|")[1]
                        val targetActor = battle.getActor(showdownId)

                        if (targetActor == null) {
                            battle.log("No actor could be found with the showdown id: $showdownId")
                            return
                        }

                        val privateMessage = lines[0]
                        val publicMessage = lines[1]

                        for (instruction in splitUpdateInstructions.entries) {
                            if (lines[0].startsWith(instruction.key)) {
                                instruction.value(battle, targetActor, BattleMessage(publicMessage), BattleMessage(privateMessage))
                                break
                            }
                        }

                        lines.removeFirst()
                        lines.removeFirst()
                    } else {
                        if (line != "|") {
                            val instruction = updateInstructions.entries.find { line.startsWith(it.key) }?.value
                            if (instruction != null) {
                                instruction(battle, BattleMessage(line), lines)
                            } else {
                                battle.dispatch {
                                    battle.broadcastChatMessage(line.text())
                                    GO
                                }
                            }
                        }
                    }
                }
            } else if (lines[0] == "sideupdate") {
                val showdownId = lines[1]
                val targetActor = battle.getActor(showdownId)
                val line = lines[2]

                if (targetActor == null) {
                    battle.log("No actor could be found with the showdown id: $showdownId")
                    return
                }

                for (instruction in sideUpdateInstructions.entries) {
                    if (line.startsWith(instruction.key)) {
                        instruction.value(battle, targetActor, BattleMessage(line))
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Caught exception interpreting {}", e)
        }
    }

    /**
     * Format:
     * |player|PLAYER|USERNAME|AVATAR|RATING
     *
     * Definitions:
     * PLAYER is p1 or p2 unless 4 player battle which adds p3 and p4
     * USERNAME is the Cobblemon battle actors uuid
     * AVATAR is unused currently
     * RATING is unused currently
     */
    private fun handlePlayerInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
//        battle.log("Player Instruction")
    }

    /**
     * Format:
     * |teamsize|PLAYER|NUMBER
     *
     * Definitions:
     * PLAYER is p1 or p2 unless 4 player battle which adds p3 and p4
     * NUMBER is number of Pokémon your opponent starts with for team preview.
     */
    private fun handleTeamSizeInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
//        battle.log("Team Size Instruction")
    }

    /**
     * Format:
     * |gametype|GAMETYPE
     *
     * Definitions:
     * GAMETYPE is singles, doubles, triples, multi, and or freeforall
     */
    private fun handleGameTypeInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Game Type Instruction: $message")
//
//        battle.broadcastChatMessage(LiteralText("${Formatting.GOLD}${Formatting.BOLD}Battle Type:"))
//
//        val tierName = message.split("|gametype|")[1]
//        val textComponent = LiteralText(" ${Formatting.GRAY}$tierName")
//        battle.broadcastChatMessage(textComponent)
//        battle.broadcastChatMessage("".text())
    }

    /**
     * Format:
     * |gen|GENNUM
     *
     * Definitions:
     * GENNUM is Generation number, from 1 to 7. Stadium counts as its respective gens;
     * Let's Go counts as 7, and modded formats count as whatever gen they were based on.
     */
    private fun handleGenInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Gen Instruction: $message")
    }

    /**
     * Format:
     * |tier|FORMATNAME
     *
     * Definitions:
     * FORMATNAME is the name of the format being played.
     */
    private fun handleTierInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Tier Instruction: $message")
//
//        battle.broadcastChatMessage(LiteralText("${Formatting.GOLD}${Formatting.BOLD}Battle Tier:"))
//
//        val tierName = message.split("|tier|")[1]
//        val textComponent = LiteralText(" ${Formatting.GRAY}$tierName")
//        battle.broadcastChatMessage(textComponent)
//        battle.broadcastChatMessage("".text())
    }

    /**
     * Format:
     * |rated or |rated|MESSAGE
     *
     * Definitions:
     * No message: Will be sent if the game will affect the player's ladder rating (Elo score).
     * Message: Will be sent if the game is official in some other way, such as being a tournament game.
     * Does not actually mean the game is rated.
     */
    private fun handleRatedInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
//        battle.log("Rated Instruction")
    }

    /**
     * Format:
     * |rule|RULE: DESCRIPTION
     *
     * Definitions:
     * RULE is a rule and its description
     */
    private fun handleRuleInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Rule Instruction: ${message.rawMessage}")
        if (!battle.announcingRules) {
            battle.announcingRules = true
//            val textComponent = LiteralText("${Formatting.GOLD}${Formatting.BOLD}Battle Rules:")
//            battle.broadcastChatMessage(textComponent)
        }
//        val rule = message.substringAfter("|rule|")
//        val textComponent = LiteralText("${Formatting.GRAY} - $rule")
//        battle.broadcastChatMessage(textComponent)
    }

    /**
     * Format:
     * |clearpoke
     *
     * Marks the start of Team Preview
     */
    private fun handleClearPokeInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Clear Poke Instruction")
    }

    /**
     * Format:
     * |poke|PLAYER|DETAILS|ITEM
     *
     * Declares a Pokémon for Team Preview.
     *
     * PLAYER is the player ID
     * DETAILS describes the pokemon
     * ITEM will be an item if the pokemon is holding an item or blank if it isn't
     */
    private fun handlePokeInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Poke Instruction: $message")
//
//        val args = message.split("|")
//        val showdownId = args[2]
//        val pokemon = args[3]
//
//        val targetActor = battle.getActor(showdownId)
//
//        if (targetActor == null) {
//            battle.log("No actor could be found with the showdown id: $showdownId")
//            return
//        }
//
//        if (targetActor is PlayerBattleActor) {
//            if (!targetActor.announcedPokemon) {
//                targetActor.announcedPokemon = true
//                val textComponent = battleLang("your_team").gold().bold()
//                targetActor.sendMessage(textComponent)
//            }
//
//            battle.broadcastChatMessage("- $pokemon".aqua()) // change to our own description using UUID to get the Pokemon
//        }
    }

    /**
     * Format:
     * |teampreview indicates team preview is over
     */
    private fun handleTeamPreviewInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Start Team Preview Instruction: ${message.rawMessage}")
    }

    /**
     * Format:
     * |start
     *
     * Indicates that the game has started.
     */
    private fun handleStartInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.log("Start Instruction: ${message.rawMessage}")
    }

    /**
     * Format:
     * |turn|NUMBER
     *
     * It is now turn NUMBER.
     */
    private fun handleTurnInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        if (!battle.started) {
            battle.started = true
            battle.actors.forEach { actor ->
                if (actor.uuid.getPlayer() != null) {
                    val initializePacket = BattleInitializePacket(battle, actor.getSide())
                    actor.sendUpdate(initializePacket)
                    actor.sendUpdate(BattleMusicPacket(battle))
                }
            }
            battle.actors.forEach { actor ->
                actor.sendUpdate(BattleSetTeamPokemonPacket(actor.pokemonList.map { it.effectedPokemon }))
                val req = actor.request ?: return@forEach
                actor.sendUpdate(BattleQueueRequestPacket(req))
            }

            battle.dispatch {
                DispatchResult { !battle.side1.stillSendingOut() && !battle.side2.stillSendingOut() }
            }

            battle.dispatchGo {
                battle.side1.playCries()
                after(seconds = 1.0F) { battle.side2.playCries() }
            }
        }

        // TODO maybe tell the client that the turn number has changed
        val turnNumber = message.argumentAt(0)?.toInt() ?: return

        battle.dispatch {
            battle.sendToActors(BattleMakeChoicePacket())
            battle.broadcastChatMessage(battleLang("turn", turnNumber).aqua())
            battle.turn(turnNumber)
            GO
        }
    }

    /**
     * Format:
     * |upkeep
     *
     * Signals the upkeep phase of the turn where the number of turns left for field conditions are updated.
     */
    private fun handleUpkeepInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatch {
            battle.actors.forEach { it.upkeep() }
            GO
        }
    }

    /**
     * Format:
     * |faint|POKEMON
     *
     * The Pokémon POKEMON has fainted.
     */
    private fun handleFaintInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(2.5F) {
            val (pnx, _) = message.pnxAndUuid(0) ?: return@dispatchWaiting
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.sendUpdate(BattleFaintPacket(pnx, battleLang("fainted", pokemon.getName())))
            pokemon.effectedPokemon.currentHealth = 0
            pokemon.sendUpdate()
            battle.broadcastChatMessage(battleLang("fainted", pokemon.getName()).red())
            val context = getContextFromFaint(pokemon, battle)
            CobblemonEvents.BATTLE_FAINTED.post(BattleFaintedEvent(battle, pokemon, context))

            battle.getActorAndActiveSlotFromPNX(pnx).second.battlePokemon = null
            pokemon.contextManager.add(context)
            pokemon.contextManager.clear(BattleContext.Type.STATUS, BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
            battle.majorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |bagitem|POKEMON|ITEMNAME
     *
     * POKEMON had ITEMNAME used on it from the 'bag'.
     */
    private fun handleBagItemInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val pokemon = message.pokemonByUuid(0, battle)!!
            val item = message.argumentAt(1)!!

            val ownerName = pokemon.actor.getName()
            val itemName = item.asTranslated()

            battle.broadcastChatMessage(battleLang("bagitem.use", ownerName, itemName, pokemon.getName()))
        }
    }

    /**
     * Format:
     * |win|USER
     *
     * USER has won the battle.
     */
    private fun handleWinInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val user = message.argumentAt(0) ?: return@dispatchGo
            val ids = user.split("&").map { it.trim() }
            val winners = ids.map { battle.getActor(UUID.fromString(it))!! }
            val losers = battle.actors.filter { !winners.contains(it) }
            val winnersText = winners.map { it.getName() }.reduce { acc, next -> acc + " & " + next }

            battle.broadcastChatMessage(battleLang("win", winnersText).gold())

            battle.end()

            val wasCaught = battle.showdownMessages.any { "capture" in it }
            CobblemonEvents.BATTLE_VICTORY.post(BattleVictoryEvent(battle, winners, losers, wasCaught))

            this.lastCauser.remove(battle.battleId)
        }
    }

    /**
     * Format:
     * |-status|POKEMON|STATUS
     *
     * The Pokémon POKEMON has been inflicted with STATUS.
     */
    fun handleStatusInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val (pnx, _) = message.pnxAndUuid(0) ?: return
        val pokemon = message.getBattlePokemon(0, battle) ?: return
        val statusLabel = message.argumentAt(1) ?: return
        val status = Statuses.getStatus(statusLabel) ?: return LOGGER.error("Unrecognized status: $statusLabel")
        broadcastOptionalAbility(battle, message.effect(), pokemon.getName())

        battle.dispatchWaiting {
            if (status is PersistentStatus) {
                pokemon.effectedPokemon.applyStatus(status)
                battle.sendUpdate(BattlePersistentStatusPacket(pnx, status))
            }

            battle.broadcastChatMessage(status.applyMessage.asTranslated(pokemon.getName()))
            pokemon.contextManager.add(getContextFromAction(message, BattleContext.Type.STATUS, battle))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-miss|SOURCE|TARGET
     *
     * The move used by the SOURCE Pokémon missed (maybe absent) the TARGET Pokémon.
     */
    private fun handleMissInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.broadcastChatMessage(battleLang("missed").red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-immune|POKEMON
     *
     * The POKEMON was immune to a move.
     */
    private fun handleImmuneInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val name = pokemon.getName()
            battle.broadcastChatMessage(battleLang("immune", name).red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |move|POKEMON|MOVE|TARGET
     *
     * The specified Pokémon has used move MOVE at TARGET.
     */
    private fun handleMoveInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val userPokemon = message.getBattlePokemon(0, battle) ?: return
        val targetPokemon = message.getBattlePokemon(2, battle)
        val effect = message.effectAt(1) ?: return
        val optionalEffect = message.effect()
        val move = Moves.getByNameOrDummy(effect.id)
        val pokemonName = userPokemon.getName()
        broadcastOptionalAbility(battle, optionalEffect, pokemonName)

        battle.dispatchGo {
            this.lastCauser[battle.battleId] = message

            userPokemon.effectedPokemon.let { pokemon ->
                if (UseMoveEvolutionProgress.supports(pokemon, move)) {
                    val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is UseMoveEvolutionProgress && it.currentProgress().move == move }) { UseMoveEvolutionProgress() }
                    progress.updateProgress(UseMoveEvolutionProgress.Progress(move, progress.currentProgress().amount + 1))
                }
            }

            val lang = when {
                optionalEffect?.id == "magicbounce" ->
                    battleLang("ability.magicbounce", pokemonName, move.displayName)
                move.name != "struggle" && targetPokemon != null && targetPokemon != userPokemon ->
                    battleLang("used_move_on", pokemonName, move.displayName, targetPokemon.getName())
                else ->
                    battleLang("used_move", pokemonName, move.displayName)
            }
            battle.broadcastChatMessage(lang)

            battle.majorBattleActions[userPokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |cant|POKEMON|REASON && |cant|POKEMON|REASON|MOVE
     *
     * The Pokémon POKEMON could not perform a move because of the indicated REASON.
     */
    private fun handleCantInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            val name = pokemon.getName()
            // Move may be null as it's not always given
            val moveName = message.moveAt(2)?.displayName ?: run { println(message.argumentAt(2)); "(Unrecognized: ${message.argumentAt(2)})".text() }

            val lang = when (effectID) {
                // TODO: in the games they use a generic image because there is a popup of the ability and the sprite of the mon, it may be good to have a similar system here
                "armortail", "damp", "dazzling", "queenlymajesty" -> battleLang("cant.generic", name, moveName)
                "par", "slp", "frz" -> {
                    val status = Statuses.getStatus(effectID)?.name?.path ?: return@dispatchWaiting
                    lang("status.$status.is", name)
                }
                else -> battleLang("cant.$effectID", name, moveName)
            }

            battle.broadcastChatMessage(lang.red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-resisted|p%a
     *
     * player % resisted the attack.
     */
    private fun handleResistInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("resisted"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |pp_update|<side_id>: <pokemon_uuid>|...<move_id>: <move_pp>
     */
    private fun handlePpUpdateInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatch {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatch GO
            val moveDatum = message.argumentAt(1)?.split(", ") ?: return@dispatch GO
            moveDatum.forEach { moveData ->
                val moveIdAndPp = moveData.split(": ")
                val moveId = moveIdAndPp[0]
                val movePp = moveIdAndPp[1]
                val move = pokemon.effectedPokemon.moveSet.firstOrNull { move -> move.name.equals(moveId, true) } ?: return@dispatch GO
                move.currentPp = movePp.toInt()
            }
            GO
        }
    }

    /**
     * Format:
     * |-supereffective|p%a
     *
     * player % was weak against the attack.
     */
    private fun handleSuperEffectiveInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("superEffective"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-crit|p%a
     *
     * player % received a critical hit.
     */
    private fun handleCritInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("crit").yellow())
            this.lastCauser[battle.battleId]?.let { message ->
                val battlePokemon = message.getBattlePokemon(0, battle) ?: return@let
                battlePokemon.criticalHits++
            }
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-weather|WEATHER
     *
     * Indicates the weather that is currently in effect.
     *
     * If upkeep is present, it means that WEATHER was active previously and is still in effect that turn.
     * Otherwise, it means that the weather has changed due to a move or ability, or has expired, in which case WEATHER will be none.
     */
    private fun handleWeatherInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        val weather = message.effectAt(0)?.id ?: return
        val user = message.getSourceBattlePokemon(battle)?.getName() ?: Text.literal("UNKNOWN")
        broadcastOptionalAbility(battle, message.effect(), user)

        battle.dispatchWaiting(1.5F) {
            val lang = when {
                message.hasOptionalArgument("upkeep") -> battleLang("weather.$weather.upkeep")
                weather != "none" -> {
                    battle.contextManager.add(getContextFromAction(message, BattleContext.Type.WEATHER, battle))
                    battleLang("weather.$weather.start")
                }
                else -> {
                    val oldWeather = battle.contextManager.get(BattleContext.Type.WEATHER)?.iterator()?.next()?.id ?: return@dispatchWaiting
                    battle.contextManager.clear(BattleContext.Type.WEATHER)
                    battleLang("weather.$oldWeather.end")
                }
            }
            battle.broadcastChatMessage(lang)
        }
    }

    /**
     * Format:
     * |-fail|POKEMON|ACTION
     *
     * The specified ACTION has failed against the POKEMON targetted.
     */
    private fun handleFailInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        battle.dispatchWaiting(1.5F){
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.broadcastChatMessage(battleLang("fail").red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-mustrecharge|POKEMON
     *
     * The Pokémon POKEMON must spend the turn recharging from a previous move.
     */
    private fun handleRechargeInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        battle.dispatchWaiting(2F){
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.broadcastChatMessage(battleLang("recharge", pokemon.getName()))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-curestatus|POKEMON|STATUS
     *
     * The Pokémon POKEMON has recovered from STATUS.
     */
    private fun handleCureStatusInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val maybeActivePokemon = message.actorAndActivePokemon(0, battle)?.second?.battlePokemon
        val maybePartyPokemon = message.getBattlePokemon(0, battle)
        val pokemon = maybeActivePokemon ?: maybePartyPokemon ?: return
        val pokemonName = pokemon.getName()
        val status = message.argumentAt(1)?.let(Statuses::getStatus) ?: return
        val effect = message.effect()
        broadcastOptionalAbility(battle, effect, pokemonName)

        battle.dispatchWaiting {
            pokemon.effectedPokemon.status = null
            pokemon.sendUpdate()

            if (maybeActivePokemon != null) {
                message.pnxAndUuid(0)?.let {
                    battle.sendUpdate(BattlePersistentStatusPacket(it.first, null))
                }
            }
            val lang = when (effect?.type) {
                Effect.Type.ABILITY -> battleLang("curestatus.${effect.id}", pokemonName)
                else -> status.removeMessage.asTranslated(pokemonName)
            }
            battle.broadcastChatMessage(lang)
            pokemon.contextManager.remove(status.showdownName, BattleContext.Type.STATUS)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-start|POKEMON|EFFECT
     *
     * A volatile status has been inflicted on the POKEMON Pokémon by EFFECT.
     */
    private fun handleStartInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        battle.dispatch {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatch GO
            val effectID = message.effectAt(1)?.id ?: return@dispatch GO

            // skip adding contexts for every time the perish counter decrements
            if (!effectID.contains("perish")) {
                // don't need to add unique: showdown won't send -start instruction if volatile status is already present
                pokemon.contextManager.add(getContextFromAction(message, BattleContext.Type.VOLATILE, battle))
            }
            battle.minorBattleActions[pokemon.uuid] = message

            if (!message.hasOptionalArgument("silent")) {
                val lang = when (effectID) {
                    "confusion", "perish3" -> return@dispatch GO // Skip
                    "perish2", "perish1", "perish0" -> battleLang("start.perish", pokemon.getName(), effectID.last().digitToInt())
                    "dynamax" -> battleLang("start.${message.effectAt(2)?.id ?: effectID}", pokemon.getName()).yellow()
                    else -> battleLang("start.$effectID", pokemon.getName())
                }
                battle.broadcastChatMessage(lang)
            }
            WaitDispatch(1F)
        }
    }

    /**
     * Format:
     * |-singleturn|POKEMON|MOVE
     *
     * The Pokémon POKEMON used move MOVE which causes a temporary effect lasting the duration of the turn.
     */
    private fun handleSingleTurnInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val sourceName = message.getSourceBattlePokemon(battle)?.getName() ?: return@dispatchWaiting
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            val lang = battleLang("singleturn.$effectID", pokemonName, sourceName)
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-singlemove|POKEMON|MOVE
     *
     * The Pokémon POKEMON used move MOVE which causes a temporary effect lasting the duration of the move.
     */
    private fun handleSingleMoveInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            val lang = battleLang("singlemove.$effectID", pokemonName)
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-activate|POKEMON|EFFECT
     *
     * A miscellaneous effect has activated.This is triggered whenever an effect could not be better described by one of the other minor messages.
     */
    private fun handleActivateInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        val pokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = pokemon.getName()
        val sourceName = message.getSourceBattlePokemon(battle)?.getName() ?: Text.literal("UNKOWN")
        val effect = message.effectAt(1) ?: return
        broadcastOptionalAbility(battle, effect, pokemonName)

        battle.dispatch{
            this.lastCauser[battle.battleId] = message
            battle.minorBattleActions[pokemon.uuid] = message

            val lang = when (effect.id) {
                // Includes a 3rd argument being the magnitude level as a number
                "magnitude" -> battleLang("activate.magnitude", message.argumentAt(2)?.toIntOrNull() ?: 1)
                // Includes spited move and the PP it was reduced by
                "spite" -> battleLang("activate.spite", pokemonName, message.argumentAt(2)!!, message.argumentAt(3)!!)
                // Don't need additional lang, announced elsewhere
                "toxicdebris", "shedskin" -> return@dispatch GO
                // Add activation to each Pokemon's history
                "destinybond" -> {
                    battle.activePokemon.mapNotNull { it.battlePokemon?.uuid }.forEach { battle.minorBattleActions[it] = message }
                    battleLang("activate.destinybond", pokemonName)
                }
                // Includes revealed move
                "forewarn" -> {
                    val moveName = message.moveAt(2)?.displayName ?: run { println(message.argumentAt(2)); "(Unrecognized: ${message.argumentAt(2)})".text() }
                    battleLang("activate.forewarn", sourceName, moveName)
                }
                "focussash", "focusband" -> battleLang("activate.focusband", pokemonName, message.effectAt(1)!!.typelessData)
                "maxguard", "protect" -> battleLang("activate.protect", pokemonName)
                else -> battleLang("activate.${effect.id}", pokemonName, sourceName)
            }
            battle.broadcastChatMessage(lang)
            WaitDispatch(1F)
        }
    }

    /**
     * Format:
     * |-fieldstart|CONDITION
     *
     * The field condition CONDITION has started.
     */
    private fun handleFieldStartInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        val effect = message.effectAt(0) ?: return
        val user = message.getSourceBattlePokemon(battle)?.getName() ?: Text.literal("UNKNOWN")
        broadcastOptionalAbility(battle, effect, user)

        battle.dispatchWaiting(1.5F) {
            // Note persistent is a CAP ability only we can ignore the flag
            val lang = battleLang("fieldstart.${effect.id}", user)
            battle.broadcastChatMessage(lang)

            val type = BattleContext.Type.valueOf(effect.rawData.substringAfterLast(" ").uppercase())
            battle.contextManager.add(getContextFromAction(message, type, battle))
        }
    }

    /**
     * Format:
     * |-fieldend|CONDITION
     *
     * The field condition CONDITION has ended.
     */
    private fun handleFieldEndInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        battle.dispatchWaiting(1.5F) {
            val effect = message.effectAt(0) ?: return@dispatchWaiting
            val lang = battleLang("fieldend.${effect.id}")
            battle.broadcastChatMessage(lang)

            val type = BattleContext.Type.valueOf(effect.rawData.substringAfterLast(" ").uppercase())
            battle.contextManager.remove(effect.id, type)
        }
    }

    /**
     * Format:
     * |-fieldactivate|EFFECT
     *
     * A miscellaneous effect has activated for the entire field.
     */
    private fun handleFieldActivateInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        battle.dispatchWaiting(2.5F) {
            val effectID = message.effectAt(0)?.id ?: return@dispatchWaiting
            val lang = battleLang("fieldactivate.$effectID")
            battle.broadcastChatMessage(lang.red())

            // share this action with all active Pokemon
            battle.activePokemon.forEach {
                it.battlePokemon?.contextManager?.addUnique(getContextFromAction(message, BattleContext.Type.VOLATILE, battle))
            }
        }
    }

    /**
     * Format:
     * |-ability|POKEMON|ABILITY|[from]EFFECT
     *
     * The ABILITY of the POKEMON has been changed due to a move/ability EFFECT.
     *
     * Format:
     * |-ability|POKEMON|ABILITY
     *
     * POKEMON has just switched-in, and its ability ABILITY is being announced to have a long-term effect.
     */
    private fun handleAbilityInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val pokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = pokemon.getName()
        val effect = message.effectAt(1) ?: return
        broadcastAbility(battle, effect, pokemonName)

        battle.dispatch {
            this.lastCauser[battle.battleId] = message

            val lang = when (effect.id) {
                "sturdy", "unnerve", "anticipation" -> battleLang("ability.${effect.id}", pokemonName) // Unique message
                "airlock", "cloudnine" -> battleLang("ability.airlock") // Cloud Nine shares the same text as Air Lock
                else -> null // Effect broadcasted by a succeeding instruction
            }

            battle.minorBattleActions[pokemon.uuid] = message
            if (lang != null) {
                battle.broadcastChatMessage(lang)
                return@dispatch WaitDispatch(1F)
            }
            else return@dispatch GO
        }
    }

    fun broadcastOptionalAbility(battle: PokemonBattle, effect: Effect?, pokemonName: MutableText) {
        if (effect != null && effect.type == Effect.Type.ABILITY)
            broadcastAbility(battle, effect, pokemonName)
    }

    // Broadcasts a generic lang to notify players of ability activations (effects are broadcasted separately)
    fun broadcastAbility(battle: PokemonBattle, effect: Effect, pokemonName: MutableText) {
        battle.dispatchGo {
            val lang = battleLang("ability.generic", pokemonName, effect.typelessData).yellow()
            battle.broadcastChatMessage(lang)
        }
    }

    /**
     * Format:
     * |-prepare|ATTACKER|MOVE && |-prepare|ATTACKER|MOVE|DEFENDER
     *
     * The ATTACKER Pokémon is preparing to use a charge MOVE on the DEFENDER or an unknown target.
     */
    private fun handlePrepareInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(1.5F) {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            //Prevents spam when the move Role Play is used
            val lang = when (effectID) {
                "shadowforce" -> battleLang("prepare.phantomforce", pokemonName) //Phantom Force and Shadow Force share the same text
                "solarblade" -> battleLang("prepare.solarbeam", pokemonName) //Solar Beam and Solar Blade share the same text
                else -> battleLang("prepare.$effectID", pokemonName)
            }
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-swapboost|SOURCE|TARGET|STATS
     *
     * Swaps the boosts from STATS between the SOURCE Pokémon and TARGET Pokémon.
     */
    private fun handleSwapBoostInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(2F) {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val targetPokemon = message.getBattlePokemon(1, battle) ?: return@dispatchWaiting
            val targetPokemonName = targetPokemon.getName()
            val lang = battleLang("swapboost.generic", pokemonName, targetPokemonName)
            battle.broadcastChatMessage(lang)

            pokemon.contextManager.swap(targetPokemon.contextManager, BattleContext.Type.BOOST)
            pokemon.contextManager.swap(targetPokemon.contextManager, BattleContext.Type.UNBOOST)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-end|POKEMON|EFFECT
     *
     * The volatile status from EFFECT inflicted on the POKEMON Pokémon has ended.
     */
    private fun handleEndInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting {
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val effectID = message.effectAt(1)?.id ?: return@dispatchWaiting
            if (!message.hasOptionalArgument("silent")) {
                val lang = when (effectID) {
                    "yawn" -> lang("status.sleep.apply", pokemonName)
                    else -> battleLang("end.$effectID", pokemonName)
                }
                battle.broadcastChatMessage(lang)
            }
            pokemon.contextManager.remove(effectID, BattleContext.Type.VOLATILE)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-sidestart|SIDE|CONDITION
     *
     * A side condition CONDITION has started on SIDE.
     */
    private fun handleSideStartInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        battle.dispatchWaiting(2F) {
            val side = if (message.argumentAt(0)?.get(1) == '1') battle.side1 else battle.side2
            val effect = message.effectAt(1) ?: return@dispatchWaiting
            battle.sides.forEach {
                val subject = if (it == side) battleLang("side_subject.ally") else battleLang("side_subject.opponent")
                val lang = battleLang("sidestart.${effect.id}", subject)
                it.broadcastChatMessage(lang)
            }

            val bucket = when(effect.rawData.substringAfterLast(" ").lowercase()) {
                "reflect", "screen", "veil" -> BattleContext.Type.SCREEN
                "spikes", "rock", "web" -> BattleContext.Type.HAZARD
                "tailwind" -> BattleContext.Type.TAILWIND
                else -> BattleContext.Type.MISC
            }
            side.contextManager.add(getContextFromAction(message, bucket, battle))
        }
    }

    /**
     * Format:
     * |-sideend|SIDE|CONDITION
     *
     * Indicates that the side condition CONDITION ended for the given SIDE.
     */
    private fun handleSideEndInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>){
        battle.dispatchWaiting(2F) {
            val side = if (message.argumentAt(0)?.get(1) == '1') battle.side1 else battle.side2
            val effect = message.effectAt(1) ?: return@dispatchWaiting
            battle.sides.forEach {
                val subject = if (it == side) battleLang("side_subject.ally") else battleLang("side_subject.opponent")
                val lang = battleLang("sideend.${effect.id}", subject)
                it.broadcastChatMessage(lang)
            }

            val bucket = when(effect.rawData.substringAfterLast(" ").lowercase()) {
                "reflect", "screen", "veil" -> BattleContext.Type.SCREEN
                "spikes", "rock", "web" -> BattleContext.Type.HAZARD
                "tailwind" -> BattleContext.Type.TAILWIND
                else -> BattleContext.Type.MISC
            }
            side.contextManager.remove(effect.id, bucket)
        }
    }

    /**
     * Format:
     * |error|ERROR
     *
     * Some examples
     * |error|[Invalid choice] Can't choose for Team Preview: You're not in a Team Preview phase
     * |error|[Unavailable choice] Can't switch: The active Pokémon is trapped
     * The protocol message to tell you to send a different decision:
     */
    private fun handleErrorInstructions(battle: PokemonBattle, battleActor: BattleActor, message: BattleMessage) {
        battle.log("Error Instruction")
        battle.dispatchGo {
            //TODO: some lang stuff for the error messages (Whats the protocol for adding to other langs )
            //Also is it okay to ignore the team preview error for now? - You bet!
            val lang = when(message.rawMessage) {
                "|error|[Unavailable choice] Can't switch: The active Pokémon is trapped" -> battleLang("error.pokemon_is_trapped").red()
                "|error|[Invalid choice] Can't choose for Team Preview: You're not in a Team Preview phase" -> return@dispatchGo
                else -> battle.createUnimplemented(message)
            }
            battleActor.sendMessage(lang)
            battleActor.mustChoose = true
            battleActor.sendUpdate(BattleMadeInvalidChoicePacket())
        }
    }

    /**
     * Format:
     * |request|REQUEST
     *
     * The protocol message to tell you that it's time for you to make a decision is:
     */
    private fun handleRequestInstruction(battle: PokemonBattle, battleActor: BattleActor, message: BattleMessage) {
        battle.log("Request Instruction")

        if (message.rawMessage.contains("teamPreview")) // TODO probably change when we're allowing team preview
            return

        // Parse Json message and update state info for actor
        val request = BattleRegistry.gson.fromJson(message.rawMessage.split("|request|")[1], ShowdownActionRequest::class.java)
        request.sanitize(battle, battleActor)
        if (battle.started) {
            battle.dispatchGo {
                // This request won't be acted on until the start of next turn
                battleActor.sendUpdate(BattleQueueRequestPacket(request))
                battleActor.request = request
                battleActor.responses.clear()
                // We need to send this out because 'upkeep' isn't received until the request is handled since the turn won't swap
                if (request.forceSwitch.contains(true)) {
                    battle.doWhenClear {
                        battleActor.mustChoose = true
                        battleActor.sendUpdate(BattleMakeChoicePacket())
                    }
                }
            }
        } else {
            battleActor.request = request
            battleActor.responses.clear()
        }
    }

    /**
     * Format:
     * |switch|POKEMON|DETAILS|HP STATUS
     *
     * A Pokémon identified by POKEMON has switched in (if there was an old Pokémon in that position, it is switched out).
     * POKEMON|DETAILS represents all the information that can be used to tell Pokémon apart.
     * The switched Pokémon has HP HP, and status STATUS.
     */
    private fun handleSwitchInstruction(battle: PokemonBattle, battleActor: BattleActor, publicMessage: BattleMessage, privateMessage: BattleMessage) {
        val (pnx, pokemonID) = publicMessage.pnxAndUuid(0) ?: return
        if (!battle.started) {
            val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
            val pokemon = battle.getBattlePokemon(pnx, pokemonID)
            val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null

            activePokemon.battlePokemon = pokemon
            val pokemonEntity = pokemon.entity
            if (pokemonEntity == null && entity != null) {
                val targetPos = battleActor.getSide().getOppositeSide().actors.filterIsInstance<EntityBackedBattleActor<*>>().firstOrNull()?.entity?.pos?.let { pos ->
                    val offset = pos.subtract(entity.pos)
                    val idealPos = entity.pos.add(offset.multiply(0.33))
                    idealPos
                } ?: entity.pos

                actor.stillSendingOutCount++
                pokemon.effectedPokemon.sendOutWithAnimation(
                    source = entity,
                    battleId = battle.battleId,
                    level = entity.world as ServerWorld,
                    doCry = false,
                    position = targetPos
                ).thenApply {
                    actor.stillSendingOutCount--
                }
            }
        } else {
            battle.dispatchInsert {
                val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
                val pokemon = battle.getBattlePokemon(pnx, pokemonID)
                val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null
                pokemon.sendUpdate()

                if (activePokemon.battlePokemon == pokemon) {
                    return@dispatchInsert emptySet() // Already switched in, Showdown does this if the pokemon is going to die before it can switch
                }

                activePokemon.battlePokemon?.let { oldPokemon ->
                    if (publicMessage.effect()?.id == "batonpass") oldPokemon.contextManager.swap(pokemon.contextManager, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    oldPokemon.contextManager.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    battle.majorBattleActions[oldPokemon.uuid] = publicMessage
                }
                battle.majorBattleActions[pokemon.uuid] = publicMessage

                setOf(
                    BattleDispatch {
                        if (entity != null) {
                            this.createEntitySwitch(battle, actor, entity, pnx, activePokemon, pokemon)
                        } else {
                            this.createNonEntitySwitch(battle, actor, pnx, activePokemon, pokemon)
                        }
                    }
                )
            }
        }
    }

    private fun createEntitySwitch(battle: PokemonBattle, actor: BattleActor, entity: LivingEntity, pnx: String, activePokemon: ActiveBattlePokemon, newPokemon: BattlePokemon): DispatchResult {
        val pokemonEntity = activePokemon.battlePokemon?.entity
        // If we can't find the entity for some reason then we're going to skip the recall animation
        val sendOutFuture = CompletableFuture<Unit>()
        (pokemonEntity?.recallWithAnimation() ?: CompletableFuture.completedFuture(Unit)).thenApply {
            // Queue actual swap and send-in after the animation has ended
            actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
            activePokemon.battlePokemon = newPokemon
            battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true), BattleSwitchPokemonPacket(pnx, newPokemon, false))
            if (newPokemon.entity != null) {
                newPokemon.entity?.cry()
                sendOutFuture.complete(Unit)
            } else {
                val lastPosition = activePokemon.position
                // Send out at previous Pokémon's location if it is known, otherwise actor location
                val world = lastPosition?.first ?: entity.world as ServerWorld
                val pos = lastPosition?.second ?: entity.pos
                newPokemon.effectedPokemon.sendOutWithAnimation(
                    source = entity,
                    battleId = battle.battleId,
                    level = world,
                    position = pos
                ).thenAccept { sendOutFuture.complete(Unit) }
            }
        }

        return UntilDispatch { sendOutFuture.isDone }
    }

    private fun createNonEntitySwitch(battle: PokemonBattle, actor: BattleActor, pnx: String, activePokemon: ActiveBattlePokemon, newPokemon: BattlePokemon): DispatchResult {
        actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
        activePokemon.battlePokemon = newPokemon
        battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true), BattleSwitchPokemonPacket(pnx, newPokemon, false))
        return WaitDispatch(1.5F)
    }

    /**
     * Format:
     * |-damage|POKEMON|HP STATUS
     *
     * The specified Pokémon POKEMON has taken damage, and is now at HP STATUS
     */
    fun handleDamageInstruction(battle: PokemonBattle, actor: BattleActor, publicMessage: BattleMessage, privateMessage: BattleMessage) {
        val battlePokemon = publicMessage.getBattlePokemon(0, battle) ?: return
        if (privateMessage.optionalArgument("from")?.equals("recoil", true) == true) {
            battlePokemon.effectedPokemon.let { pokemon ->
                if (RecoilEvolutionProgress.supports(pokemon)) {
                    val newPercentage = privateMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toIntOrNull() ?: 0
                    val newHealth = (pokemon.hp * (newPercentage / 100.0)).roundToInt()
                    val difference = pokemon.currentHealth - newHealth
                    if (difference > 0) {
                        val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is RecoilEvolutionProgress }) { RecoilEvolutionProgress() }
                        progress.updateProgress(RecoilEvolutionProgress.Progress(progress.currentProgress().recoil + difference))
                    }
                }
            }
        }
        val newHealth = privateMessage.argumentAt(1)?.split(" ")?.get(0) ?: return
        val effect = privateMessage.effect()
        val pokemonName = battlePokemon.getName()
        val sourceName = privateMessage.getSourceBattlePokemon(battle)?.getName() ?: Text.literal("UNKOWN")
        broadcastOptionalAbility(battle, effect, sourceName)

        battle.dispatch {
            val newHealthRatio: Float
            val remainingHealth = newHealth.split("/")[0].toInt()

            if (effect != null) {
                val lang = when (effect.id) {
                    "blacksludge", "stickybarb" -> battleLang("damage.item", pokemonName, effect.typelessData)
                    "brn", "psn", "tox" -> {
                        val status = Statuses.getStatus(effect.id)?.name?.path ?: return@dispatch GO
                        lang("status.$status.hurt", pokemonName)
                    }
                    "aftermath" -> battleLang("damage.generic", pokemonName)
                    else -> battleLang("damage.${effect.id}", pokemonName, sourceName)
                }
                battle.broadcastChatMessage(lang.red())
            }

            if (newHealth == "0") {
                newHealthRatio = 0F
                battle.dispatch {
                    battlePokemon.effectedPokemon.currentHealth = 0
                    battlePokemon.sendUpdate()
                    GO
                }
            } else {
                val maxHealth = newHealth.split("/")[1].toInt()
                val difference = maxHealth - remainingHealth
                newHealthRatio = remainingHealth.toFloat() / maxHealth
                battle.dispatch {
                    battlePokemon.effectedPokemon.currentHealth = remainingHealth
                    if (difference > 0) {
                        battlePokemon.effectedPokemon.let { pokemon ->
                            if (DamageTakenEvolutionProgress.supports(pokemon)) {
                                val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is DamageTakenEvolutionProgress }) { DamageTakenEvolutionProgress() }
                                progress.updateProgress(DamageTakenEvolutionProgress.Progress(progress.currentProgress().amount + difference))
                            }
                        }
                    }
                    battlePokemon.sendUpdate()
                    GO
                }
            }
            privateMessage.pnxAndUuid(0)?.let { (pnx, _) -> battle.sendSidedUpdate(actor, BattleHealthChangePacket(pnx, remainingHealth.toFloat()), BattleHealthChangePacket(pnx, newHealthRatio)) }

            battle.minorBattleActions[battlePokemon.uuid] = privateMessage
            WaitDispatch(1F)
        }
    }

    /**
     * Format:
     * |drag|POKEMON|DETAILS|HP STATUS
     *
     * A Pokémon identified by POKEMON has switched in (if there was an old Pokémon in that position, it is switched out).
     * POKEMON|DETAILS represents all the information that can be used to tell Pokémon apart.
     * The switched Pokémon has HP HP, and status STATUS.
     */
    fun handleDragInstruction(battle: PokemonBattle, actor: BattleActor, publicMessage: BattleMessage, privateMessage: BattleMessage) {
        battle.dispatchInsert {
            val (pnx, pokemonID) = publicMessage.pnxAndUuid(0)!!
            val (_, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
            val pokemon = battle.getBattlePokemon(pnx, pokemonID)

            battle.broadcastChatMessage(battleLang("dragged_out", pokemon.getName()))
            activePokemon.battlePokemon?.let { oldPokemon ->
                oldPokemon.contextManager.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                battle.majorBattleActions[oldPokemon.uuid] = publicMessage
            }
            battle.majorBattleActions[pokemon.uuid] = publicMessage

            val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null
            setOf(
                BattleDispatch {
                    if (entity != null) {
                        this.createEntitySwitch(battle, actor, entity, pnx, activePokemon, pokemon)
                    } else {
                        this.createNonEntitySwitch(battle, actor, pnx, activePokemon, pokemon)
                    }
                }
            )
        }

    }

    /**
     * Format:
     * |-hitcount|POKEMON|NUM
     *
     * A multi-hit move hit the POKEMON NUM times.
     */
    fun handleHitCountInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val battlePokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val hitCount = message.argumentAt(1)?.toIntOrNull() ?: return@dispatchGo
            val lang = if (hitCount == 1) battleLang("hit_count_singular") else battleLang("hit_count", hitCount)
            battle.minorBattleActions[battlePokemon.uuid] = message
            battle.broadcastChatMessage(lang)
        }
    }

    /**
     * Format:
     * |-item|POKEMON|ITEM|[from]EFFECT
     *
     * The ITEM held by the POKEMON has been changed or revealed due to a move or ability EFFECT.
     *
     * Format:
     * |-item|POKEMON|ITEM
     *
     * POKEMON has just switched in, and its item ITEM is being announced to have a long-term effect.
     */
    fun handleItemInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val sourceName = message.getSourceBattlePokemon(battle)?.getName() ?: Text.literal("UNKOWN")
        broadcastOptionalAbility(battle, message.effect(), sourceName)

        battle.dispatchGo {
            val battlePokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battlePokemon.heldItemManager.handleStartInstruction(battlePokemon, battle, message)
            battle.minorBattleActions[battlePokemon.uuid] = message
            battlePokemon.contextManager.add(getContextFromAction(message, BattleContext.Type.ITEM, battle))
        }
    }

    /**
     * Format:
     * |-enditem|POKEMON|ITEM|[from]EFFECT
     *
     * The ITEM held by POKEMON has been destroyed by a move or ability, and it now holds no item.
     *
     * Format:
     * |-enditem|POKEMON|ITEM
     *
     * POKEMON's ITEM has destroyed itself (consumed or used).
     */
    fun handleEndItemInstruction(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val battlePokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val item = message.effectAt(1) ?: return@dispatchGo
            battlePokemon.heldItemManager.handleEndInstruction(battlePokemon, battle, message)
            battle.minorBattleActions[battlePokemon.uuid] = message
            battlePokemon.contextManager.remove(item.id, BattleContext.Type.ITEM)
            if (message.hasOptionalArgument("eat")) {
                battlePokemon.entity?.playSound(CobblemonSounds.BERRY_EAT, 1F, 1F)
            }
        }
    }

    /**
     * Format:
     * |-heal|POKEMON|HP STATUS
     *
     * The specified Pokémon POKEMON has healed damage, and is now at HP STATUS.
     */
    private fun handleHealInstruction(battle: PokemonBattle, actor: BattleActor, publicMessage: BattleMessage, privateMessage: BattleMessage) {
        val pnx = privateMessage.pnxAndUuid(0)?.first
        val battlePokemon = privateMessage.getBattlePokemon(0, battle) ?: return
        val rawHpAndStatus = privateMessage.argumentAt(1)?.split(" ") ?: return
        val rawHpRatio = rawHpAndStatus.getOrNull(0) ?: return
        val newHealth = rawHpRatio.split("/").map { it.toFloatOrNull() ?: return }
        val newHealthRatio = rawHpRatio.split("/").map { it.toFloatOrNull()?.div(100) ?: return }
        val effect = privateMessage.effect()
        val pokemonName = battlePokemon.getName()
        broadcastOptionalAbility(battle, effect, pokemonName)

        battle.dispatchWaiting {
            if (pnx != null) {
                // dynamax changes max health
                battle.sendSidedUpdate(actor, BattleHealthChangePacket(pnx, newHealth[0], newHealth[1]), BattleHealthChangePacket(pnx, newHealthRatio[0]))
            }
            val silent = privateMessage.hasOptionalArgument("silent")
            if (!silent) {
                val lang = when {
                    privateMessage.hasOptionalArgument("zeffect") -> battleLang("heal.zeffect", battlePokemon.getName())
                    privateMessage.hasOptionalArgument("wisher") -> {
                        val name = privateMessage.optionalArgument("wisher")!!
                        val showdownId = name.lowercase().replace(ShowdownIdentifiable.REGEX, "")
                        val wisher = actor.pokemonList.firstOrNull { it.effectedPokemon.showdownId() == showdownId }
                        // If no Pokémon is found this is a nickname
                        battleLang("heal.wish", wisher?.getName() ?: actor.nameOwned(name))
                    }
                    privateMessage.hasOptionalArgument("from") -> {
                        when (effect!!.type) {
                            Effect.Type.ITEM -> when (effect.id) {
                                "leftovers", "shellbell", "blacksludge" -> battleLang("heal.leftovers", battlePokemon.getName(), effect.typelessData)
                                else -> battleLang("heal.item", battlePokemon.getName(), effect.typelessData)
                            }
                            else -> when (effect.id) {
                                "drain" -> {
                                    val drained = privateMessage.getSourceBattlePokemon(battle) ?: return@dispatchWaiting
                                    battleLang("heal.drain", drained.getName())
                                }
                                else -> battleLang("heal.${effect.id}", battlePokemon.getName())
                            }
                        }
                    }
                    else -> {
                        battleLang("heal.generic", battlePokemon.getName())
                    }
                }
                battle.broadcastChatMessage(lang)
            }
            battle.minorBattleActions[battlePokemon.uuid] = privateMessage
            battlePokemon.effectedPokemon.currentHealth = newHealth[0].toInt()

            // This part is not always present
            val rawStatus = rawHpAndStatus.getOrNull(1) ?: return@dispatchWaiting
            val status = Statuses.getStatus(rawStatus) ?: return@dispatchWaiting
            if (status is PersistentStatus) {
                battlePokemon.effectedPokemon.applyStatus(status)
                if (pnx != null) {
                    battle.sendUpdate(BattlePersistentStatusPacket(pnx, status))
                }
                if (!silent) {
                    status.applyMessage.let { battle.broadcastChatMessage(it.asTranslated(battlePokemon.getName())) }
                }
            }
        }
    }

    /**
     * Format:
     * |-sethp|POKEMON|HP
     *
     * The specified Pokémon POKEMON now has HP hit points.
     */
    private fun handleSetHpInstructions(battle: PokemonBattle, actor: BattleActor, publicMessage: BattleMessage, privateMessage: BattleMessage){
        battle.dispatchWaiting {
            val (pnx, _) = privateMessage.pnxAndUuid(0) ?: return@dispatchWaiting
            val flatHp = privateMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toFloatOrNull() ?: return@dispatchWaiting
            val ratioHp = publicMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toFloatOrNull()?.times(0.01F) ?: return@dispatchWaiting
            val battlePokemon = privateMessage.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battlePokemon.effectedPokemon.currentHealth = flatHp.roundToInt()
            battle.sendSidedUpdate(actor, BattleHealthChangePacket(pnx, flatHp), BattleHealthChangePacket(pnx, ratioHp))
            // It doesn't matter which we check when silent both have it
            if (!publicMessage.hasOptionalArgument("silent")) {
                val effectID = publicMessage.effect()?.id ?: return@dispatchWaiting
                val lang = battleLang("sethp.$effectID")
                battle.broadcastChatMessage(lang)
            }
            battle.minorBattleActions[battlePokemon.uuid] = publicMessage
        }
    }

    /**
     * Format:
     * |-clearallboost|
     *
     * Clears all boosts from all Pokémon on both sides.
     */
    private fun handleClearAllBoostInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(1.5F) {
            battle.activePokemon.forEach {
                it.battlePokemon?.contextManager?.clear(BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
            }
            battle.broadcastChatMessage(battleLang("clearallboost"))
        }
    }

    /**
     * Format:
     * |-clearnegativeboost|POKEMON
     *
     * Clear the negative boosts from the target Pokémon POKEMON (usually as the result of a zeffect).
     */
    private fun handleClearNegativeBoostInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        battle.dispatchWaiting(1.5F) {
            val lang = when {
                message.hasOptionalArgument("zeffect") -> battleLang("clearallnegativeboost.zeffect", pokemonName)
                else -> battleLang("clearallnegativeboost", pokemonName)
            }
            battle.broadcastChatMessage(lang)

            battlePokemon.contextManager.clear(BattleContext.Type.UNBOOST)
            battle.minorBattleActions[battlePokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-zpower|POKEMON
     *
     * The Pokémon POKEMON has used the z-move version of its move.
     */
    private fun handleZPowerInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        battle.dispatchWaiting {
            battle.broadcastChatMessage(battleLang("zpower", pokemonName).yellow())
            battle.minorBattleActions[battlePokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-zbroken|POKEMON
     *
     * A z-move has broken through protect and hit the Pokémon POKEMON.
     */
    private fun handleZBrokenInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        battle.dispatchWaiting {
            battle.broadcastChatMessage(battleLang("zbroken", pokemonName).red())
            battle.minorBattleActions[battlePokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-terastallize|POKEMON|TYPE
     *
     * The Pokémon POKEMON terastallized into type TYPE.
     */
    private fun handleTerastallizeInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        val type = message.effectAt(1)?.let { ElementalTypes.get(it.id) } ?: return
        battle.dispatchWaiting {
            battle.broadcastChatMessage(battleLang("terastallize", pokemonName, type.displayName).yellow())
            battle.minorBattleActions[battlePokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |detailschange|POKEMON|DETAILS|HP STATUS
     *
     * The specified Pokémon has changed formes (via Mega Evolution, ability, etc.). If the forme change is permanent,
     * then detailschange will appear; otherwise, the client will send -formechange.
     */
    private fun handleDetailsChangeInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        val formName = message.argumentAt(1)?.split(',')?.get(0)?.substringAfter('-')?.lowercase() ?: return
        battle.dispatchWaiting {
            battle.broadcastChatMessage(battleLang("detailschange.$formName", pokemonName))
            battle.majorBattleActions[battlePokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-mega|POKEMON|MEGASTONE
     *
     * The Pokémon POKEMON used MEGASTONE to Mega Evolve.
     */
    private fun handleMegaInstructions(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        val battlePokemon = message.getBattlePokemon(0, battle) ?: return
        val pokemonName = battlePokemon.getName()
        battle.dispatchWaiting {
            battle.broadcastChatMessage(battleLang("mega", pokemonName).yellow())
            battle.minorBattleActions[battlePokemon.uuid] = message
        }
    }

    // Used for things that are only meant for visual information we don't have
    private fun handleSilently(battle: PokemonBattle, message: BattleMessage, remainingLines: MutableList<String>) {
        battle.dispatchGo {  }
    }

    private fun getContextFromFaint(pokemon: BattlePokemon, battle: PokemonBattle): BattleContext {
        val cause = battle.minorBattleActions[pokemon.uuid] ?: lastCauser[battle.battleId] ?: return MissingContext()
        val side = pokemon.actor.getSide()

        return when (cause.id) {
            "-damage", "move" -> {
                // damage from abilities
                cause.effect("of")?.let {
                    val effectID = cause.effect()?.id ?: it.id
                    val originPnx = cause.optionalArgument("of")!!.substringBefore(':')
                    val uuid = cause.optionalArgument("of")!!.substringAfter(':').trim()
                    val origin = battle.getBattlePokemon(originPnx, uuid)
                    BasicContext(effectID, battle.turn, BattleContext.Type.FAINT, origin)
                } ?:
                // damage from weather, statuses, entry hazards
                cause.effect()?.let { effect ->
                    val damagingContexts = BattleContext.Type.values().filter { it.damaging }
                    val contextBuckets = damagingContexts.map { pokemon.contextManager.get(it) ?: side.contextManager.get(it)
                        ?: battle.contextManager.get(it) }
                    ContextManager.scoop(effect.id, *contextBuckets.toTypedArray())
                } ?:
                // damage from moves and suicide
                lastCauser[battle.battleId]?.let {
                    val move = it.effectAt(1)!!.id
                    val origin = it.getBattlePokemon(0, battle)
                    BasicContext(move, battle.turn, BattleContext.Type.FAINT, origin)
                } ?:
                MissingContext()
            }
            // perish song
            "-start" -> {
                cause.effectAt(1)?.let {
                    val effectID = if (it.id.contains("perish")) "perishsong" else it.id
                    ContextManager.scoop(effectID, pokemon.contextManager.get(BattleContext.Type.VOLATILE))
                } ?:
                MissingContext()
            }
            // destiny bond
            "-activate" -> {
                cause.effectAt(1)?.let {
                    val origin = cause.getBattlePokemon(0, battle)
                    BasicContext(it.id, battle.turn, BattleContext.Type.FAINT, origin)
                } ?:
                MissingContext()
            }
            else -> MissingContext()
        }
    }

    private fun getContextFromAction(message: BattleMessage, type: BattleContext.Type, battle: PokemonBattle): BattleContext {
        // |-action|POKEMON|EFFECT|[from]EFFECT|[of]POKEMON or |-action|EFFECT|[from]EFFECT|[of]POKEMON
        return message.actorAndActivePokemonFromOptional(battle)?.let {
            // ex: |-item|p2a: ###|Black Sludge|[from] ability: Pickpocket|[of] p1a: ###
            val effectID = message.effectAt(1)?.id ?: message.effectAt(0)?.id ?: return@let MissingContext()
            BasicContext(effectID, battle.turn, type, it.second.battlePokemon)
        } ?:
        // |-action|POKEMON|EFFECT| (caused by a move or another action)
        message.actorAndActivePokemon(0, battle)?.let {
            // ex: |-status|p2a: ###|par -> |move|p1a: ###|Glare|p2a: ###
            // ex: |-unboost|p1a: ###|atk|1 -> |-ability|p2a: ###|Intimidate|boost
            val effectID = message.effectAt(1)?.id ?: return@let MissingContext()
            val origin = lastCauser[battle.battleId]?.getBattlePokemon(0, battle) ?: return@let MissingContext()
            BasicContext(effectID, battle.turn, type, origin)
        } ?:
        // |-action|EFFECT
        lastCauser[battle.battleId]?.let {
            // ex: |-sidestart|p2: ###|move: Toxic Spikes -> |-activate|p1a: ###|ability: Toxic Debris
            // ex: |-weather|Sandstorm -> |move|p1a: ###|Sandstorm|p1a: ###
            val effectID = message.effectAt(1)?.id ?: message.effectAt(0)?.id ?: return@let MissingContext()
            BasicContext(effectID, battle.turn, type, it.getBattlePokemon(0, battle))
        } ?:
        MissingContext()
    }
}
