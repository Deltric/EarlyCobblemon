/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.storage.pc

import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.api.storage.pc.PCStore
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import java.util.UUID
import net.minecraft.network.PacketByteBuf

/**
 * Initializes a client side representation of a PC. It is given the ID, the number of boxes,
 * and whether overflow has occurred.
 *
 * Handled by [com.cablemc.pokemod.common.client.net.storage.pc.InitializePCHandler].
 *
 * @author Hiroku
 * @since June 18th, 2022
 */
class InitializePCPacket() : NetworkPacket {
    lateinit var storeID: UUID
    var boxCount = 0
    /* Might be useful to know this in case we want the option to restore the overflow as a button in PC GUI. */
    var hasOverflowed = false

    constructor(pc: PCStore): this() {
        this.storeID = pc.uuid
        this.boxCount = pc.boxes.size
        this.hasOverflowed = pc.backupStore.any()
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeSizedInt(IntSize.U_BYTE, boxCount)
        buffer.writeBoolean(hasOverflowed)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        boxCount = buffer.readSizedInt(IntSize.U_BYTE)
        hasOverflowed = buffer.readBoolean()
    }
}