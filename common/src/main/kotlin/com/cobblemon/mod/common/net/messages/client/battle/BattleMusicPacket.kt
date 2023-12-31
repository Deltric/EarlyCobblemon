/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.battle

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvent

/**
 * Instructs a client what SoundEvent to play during a battle. If the SoundEvent specified is null, the SoundEvent
 * currently playing will terminate gracefully.
 *
 * Handled by [com.cobblemon.mod.common.client.net.battle.BattleMusicHandler].
 *
 * @author Segfault Guy
 * @since April 20th, 2023
 */
class BattleMusicPacket : NetworkPacket<BattleMusicPacket> {
    companion object {
        val ID = cobblemonResource("battle_music")
        fun decode(buffer: PacketByteBuf) =  BattleMusicPacket(
            music = Registries.SOUND_EVENT.get(buffer.readIdentifier()),
            volume = buffer.readFloat(),
            pitch = buffer.readFloat()
        )
    }

    override val id = ID

    var music : SoundEvent? = null
    var volume = 1.0f
    var pitch = 1.0f

    constructor(music: SoundEvent?, volume: Float = 1.0f, pitch: Float = 1.0f) {
        this.music = music
        this.volume = volume
        this.pitch = pitch
    }

    constructor(battle: PokemonBattle, volume: Float = 1.0f, pitch: Float = 1.0f) {
        this.music = if (battle.isPvP) {
            CobblemonSounds.PVP_BATTLE
        }
        else if (battle.isPvN) {
            CobblemonSounds.PVN_BATTLE
        }
        else {
            CobblemonSounds.PVW_BATTLE
        }
        this.volume = volume
        this.pitch = pitch
    }

    override fun encode(buffer: PacketByteBuf) {
        music?.let { buffer.writeIdentifier(it.id) } ?: buffer.writeIdentifier("".asIdentifierDefaultingNamespace())
        buffer.writeFloat(volume)
        buffer.writeFloat(pitch)
    }
}