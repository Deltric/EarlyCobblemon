/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.platform.PlatformRegistry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundEvent

object CobblemonSounds : PlatformRegistry<Registry<SoundEvent>, RegistryKey<Registry<SoundEvent>>, SoundEvent>() {

    override val registry: Registry<SoundEvent> = Registries.SOUND_EVENT
    override val registryKey: RegistryKey<Registry<SoundEvent>> = RegistryKeys.SOUND_EVENT

    @JvmField
    val GUI_CLICK = this.create("gui.click")

    @JvmField
    val PC_ON = this.create("pc.on")
    @JvmField
    val PC_OFF = this.create("pc.off")
    @JvmField
    val PC_GRAB = this.create("pc.grab")
    @JvmField
    val PC_DROP = this.create("pc.drop")
    @JvmField
    val PC_RELEASE = this.create("pc.release")
    @JvmField
    val PC_CLICK = this.create("pc.click")

    @JvmField
    val HEALING_MACHINE_ACTIVE = this.create("healing_machine.active")

    @JvmField
    val POKE_BALL_CAPTURE_STARTED = this.create("poke_ball.capture_started")
    @JvmField
    val POKE_BALL_CAPTURE_SUCCEEDED = this.create("poke_ball.capture_succeeded")
    @JvmField
    val POKE_BALL_SHAKE = this.create("poke_ball.shake")
    @JvmField
    val POKE_BALL_OPEN = this.create("poke_ball.open")
    @JvmField
    val POKE_BALL_HIT = this.create("poke_ball.hit")
    @JvmField
    val POKE_BALL_SEND_OUT = this.create("poke_ball.send_out")
    @JvmField
    val POKE_BALL_RECALL = this.create("poke_ball.recall")

    @JvmField
    val ITEM_USE = this.create("item.use")
    @JvmField
    val CAN_EVOLVE = this.create("pokemon.can_evolve")
    @JvmField
    val EVOLVING = this.create("pokemon.evolving")

    @JvmField
    val PVN_BATTLE = this.create("battle.pvn.default")
    @JvmField
    val PVP_BATTLE = this.create("battle.pvp.default")
    @JvmField
    val PVW_BATTLE = this.create("battle.pvw.default")

    @JvmField
    val MEDICINE_HERB_USE = this.create("medicine_herb.use")
    @JvmField
    val MEDICINE_LIQUID_USE = this.create("medicine_liquid.use")
    @JvmField
    val MEDICINE_PILLS_USE = this.create("medicine_pills.use")
    @JvmField
    val MEDICINE_SPRAY_USE = this.create("medicine_spray.use")

    @JvmField
    val BERRY_HARVEST = this.create("berry.harvest")
    @JvmField
    val BERRY_EAT = this.create("berry.eat")

    @JvmField
    val MULCH_PLACE = this.create("mulch.place")
    @JvmField
    val MULCH_REMOVE = this.create("mulch.remove")


    private fun create(name: String): SoundEvent = this.create(name, SoundEvent.of(cobblemonResource(name)))
}