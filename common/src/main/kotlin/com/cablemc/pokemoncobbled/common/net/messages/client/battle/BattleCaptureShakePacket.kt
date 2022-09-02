package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Packet sent when a PokéBall capturing a battle Pokémon just shook. The direction is included.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleCaptureShakeHandler].
 *
 * @author Hiroku
 * @since July 3rd, 2022
 */
class BattleCaptureShakePacket() : NetworkPacket {
    lateinit var targetPNX: String
    var direction = true

    constructor(targetPNX: String, direction: Boolean): this() {
        this.targetPNX = targetPNX
        this.direction = direction
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeString(targetPNX)
        buffer.writeBoolean(direction)
    }

    override fun decode(buffer: PacketByteBuf) {
        targetPNX = buffer.readString()
        direction = buffer.readBoolean()
    }
}