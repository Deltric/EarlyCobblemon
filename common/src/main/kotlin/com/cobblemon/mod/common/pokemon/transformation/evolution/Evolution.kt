/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.transformation.evolution

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cobblemon.mod.common.api.moves.BenchedMove
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.transformation.Transformation
import com.cobblemon.mod.common.api.pokemon.transformation.evolution.EvolutionDisplay
import com.cobblemon.mod.common.api.pokemon.transformation.evolution.EvolutionLike
import com.cobblemon.mod.common.api.pokemon.transformation.requirement.TransformationRequirement
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.PassiveTrigger
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.TransformationTrigger
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.TriggerContext
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState
import com.cobblemon.mod.common.pokemon.transformation.triggers.ItemInteractionTrigger
import com.cobblemon.mod.common.util.lang
import net.minecraft.sound.SoundCategory

/**
 * Represents an evolution of a [Pokemon], this is the server side counterpart of [EvolutionDisplay].
 *
 * @author Licious
 * @since March 19th, 2022
 */
class Evolution(
    override val id: String = "",
    override val requirements: Set<TransformationRequirement> = setOf(),
    override val trigger: TransformationTrigger = ItemInteractionTrigger(),
    /** The result of this evolution. */
    val result: PokemonProperties = PokemonProperties(),
    /** If this evolution allows the user to choose when to start it or not. */
    var optional: Boolean = true,
    /** The [MoveTemplate]s that will be offered to be learnt upon evolving. */
    val learnableMoves: Set<MoveTemplate> = setOf()
) : EvolutionLike, Transformation {

    /**
     * Starts this evolution or queues it if [optional] is true.
     *
     * @param pokemon The [Pokemon] being evolved.
     * @param context The optional [TriggerContext] needed to evolve.
     * @return Whether the [Evolution] was successful.
     */
    override fun start(pokemon: Pokemon, context: TriggerContext?): Boolean {
        if (this.test(pokemon, context)) {
            return if (this.optional) pokemon.evolutionProxy.server().add(this)
            else {
                this.forceStart(pokemon)
                true
            }
        }
        return false
    }

    /**
     * Starts this evolution as soon as possible.
     * This will not present a choice to the client regardless of [optional].
     *
     * @param pokemon The [Pokemon] being evolved.
     */
    override fun forceStart(pokemon: Pokemon) {
        if (pokemon.state is ShoulderedState) {
            pokemon.tryRecallWithAnimation()
        }
        // TODO Once implemented queue evolution for a pokemon state that is not in battle, start animation instead of instantly doing all of this
        this.result.apply(pokemon)
        this.learnableMoves.forEach { move ->
            if (pokemon.moveSet.hasSpace()) {
                pokemon.moveSet.add(move.create())
            }
            else {
                pokemon.benchedMoves.add(BenchedMove(move, 0))
            }
            pokemon.getOwnerPlayer()?.sendMessage(lang("experience.learned_move", pokemon.getDisplayName(), move.displayName))
        }
        // we want to instantly tick for example you might only evolve your Bulbasaur at level 34 so Venusaur should be immediately available
        pokemon.triggerTransformations(PassiveTrigger::class.java)
        pokemon.getOwnerPlayer()?.playSound(CobblemonSounds.EVOLVING, SoundCategory.NEUTRAL, 1F, 1F)
        CobblemonEvents.EVOLUTION_COMPLETE.post(EvolutionCompleteEvent(pokemon, this))
        super.forceStart(pokemon)
    }

}