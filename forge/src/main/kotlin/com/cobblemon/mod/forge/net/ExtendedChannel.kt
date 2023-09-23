/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.net

import net.minecraft.network.packet.Packet
import net.minecraftforge.network.NetworkDirection

/**
 * Extension of Forge's channel to add in needed methods.
 */
interface ExtendedChannel {

    /**
     * Creates a vanilla packet from a custom one
     * @param direction The direction of the packet
     * @param message The message to send
     */
    fun createVanillaPacket(direction: NetworkDirection, message: Any): Packet<*>

}