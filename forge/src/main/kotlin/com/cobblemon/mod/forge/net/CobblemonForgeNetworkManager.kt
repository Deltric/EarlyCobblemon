/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.net

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.NetworkManager
import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.util.cobblemonResource
import kotlin.reflect.KClass
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraftforge.network.ChannelBuilder
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.PacketDistributor
import java.util.function.BiConsumer


object CobblemonForgeNetworkManager : NetworkManager {

    private const val PROTOCOL_VERSION = 1
    private var id = 0

    private val channel = ChannelBuilder
        .named(cobblemonResource("main"))
        .networkProtocolVersion(PROTOCOL_VERSION)
        .simpleChannel()

    override fun registerClientBound() {
        CobblemonNetwork.registerClientBound()
    }

    override fun registerServerBound() {
        CobblemonNetwork.registerServerBound()
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : NetworkPacket<T>> createClientBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ClientNetworkPacketHandler<T>
    ) {
        this.channel.messageBuilder(kClass.java, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(encoder::invoke)
            .decoder(decoder::invoke)
            .consumerNetworkThread(BiConsumer { message, context ->
                handler.handleOnNettyThread(message)
                context.packetHandled = true
            })
            .add()
    }

    @Suppress("INACCESSIBLE_TYPE")
    override fun <T : NetworkPacket<T>> createServerBound(
        identifier: Identifier,
        kClass: KClass<T>,
        encoder: (T, PacketByteBuf) -> Unit,
        decoder: (PacketByteBuf) -> T,
        handler: ServerNetworkPacketHandler<T>
    ) {
        this.channel.messageBuilder(kClass.java, NetworkDirection.PLAY_TO_SERVER)
            .encoder(encoder::invoke)
            .decoder(decoder::invoke)
            .consumerNetworkThread(BiConsumer { message, context ->
                handler.handleOnNettyThread(message, context.sender!!.server, context.sender!!)
                context.packetHandled = true
            })
            .add()
    }

    override fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket<*>) {
        this.channel.send(packet, PacketDistributor.PLAYER.with(player))
    }

    override fun sendPacketToServer(packet: NetworkPacket<*>) {
        this.channel.send(packet, PacketDistributor.SERVER.noArg())
    }

    override fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientPlayPacketListener> {
        return (this.channel as ExtendedChannel).createVanillaPacket(NetworkDirection.PLAY_TO_CLIENT, packet) as Packet<ClientPlayPacketListener>
    }
}