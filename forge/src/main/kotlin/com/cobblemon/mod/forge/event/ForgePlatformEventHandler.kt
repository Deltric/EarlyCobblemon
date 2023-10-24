/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.event

import com.cobblemon.mod.common.platform.events.ChangeDimensionEvent
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.platform.events.ServerEvent
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import com.cobblemon.mod.common.platform.events.ServerTickEvent
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.server.ServerLifecycleHooks

object ForgePlatformEventHandler {

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun serverStarting(e: ServerAboutToStartEvent) {
        PlatformEvents.SERVER_STARTING.post(ServerEvent.Starting(e.server))
    }

    @SubscribeEvent
    fun serverStarted(e: ServerStartedEvent) {
        PlatformEvents.SERVER_STARTED.post(ServerEvent.Started(e.server))
    }


    @SubscribeEvent
    fun serverStopping(e: ServerStoppingEvent) {
        PlatformEvents.SERVER_STOPPING.post(ServerEvent.Stopping(e.server))
    }

    @SubscribeEvent
    fun serverStopped(e: ServerStoppedEvent) {
        PlatformEvents.SERVER_STOPPED.post(ServerEvent.Stopped(e.server))
    }

    @SubscribeEvent
    fun onTick(e: TickEvent.ServerTickEvent) {
        if (e.phase == TickEvent.Phase.START) {
            PlatformEvents.SERVER_TICK_PRE.post(ServerTickEvent.Pre(ServerLifecycleHooks.getCurrentServer()))
        }
        else {
            PlatformEvents.SERVER_TICK_POST.post(ServerTickEvent.Post(ServerLifecycleHooks.getCurrentServer()))
        }
    }

    @SubscribeEvent
    fun onLogin(e: PlayerEvent.PlayerLoggedInEvent) {
        val player = e.entity as? ServerPlayerEntity ?: return
        PlatformEvents.SERVER_PLAYER_LOGIN.post(com.cobblemon.mod.common.platform.events.ServerPlayerEvent.Login(player))
    }

    @SubscribeEvent
    fun onLogout(e: PlayerEvent.PlayerLoggedOutEvent) {
        val player = e.entity as? ServerPlayerEntity ?: return
        PlatformEvents.SERVER_PLAYER_LOGOUT.post(com.cobblemon.mod.common.platform.events.ServerPlayerEvent.Logout(player))
    }

    @SubscribeEvent
    fun onDeath(e: LivingDeathEvent) {
        val player = e.entity as? ServerPlayerEntity ?: return
        PlatformEvents.PLAYER_DEATH.postThen(
            event = com.cobblemon.mod.common.platform.events.ServerPlayerEvent.Death(player),
            ifSucceeded = {},
            ifCanceled = { e.isCanceled = true }
        )
    }

    @SubscribeEvent
    fun onRightClickBlock(e: PlayerInteractEvent.RightClickBlock) {
        val player = e.entity as? ServerPlayerEntity ?: return
        val hand = e.hand
        val pos = e.pos
        val face = e.face
        PlatformEvents.RIGHT_CLICK_BLOCK.postThen(
            event = com.cobblemon.mod.common.platform.events.ServerPlayerEvent.RightClickBlock(player, pos, hand, face),
            ifSucceeded = {},
            ifCanceled = { e.isCanceled = true }
        )
    }

    @SubscribeEvent
    fun onRightClickEntity(e: PlayerInteractEvent.EntityInteract) {
        val player = e.entity as? ServerPlayerEntity ?: return
        val hand = e.hand
        val item = player.getStackInHand(hand)
        val entity = e.target
        PlatformEvents.RIGHT_CLICK_ENTITY.postThen(
            event = ServerPlayerEvent.RightClickEntity(player, item, hand, entity),
            ifSucceeded = {},
            ifCanceled = { e.isCanceled = true }
        )
    }

    @SubscribeEvent
    fun onChangeDimension(e: PlayerChangedDimensionEvent) {
        val player = e.entity
        if (player is ServerPlayerEntity) {
            PlatformEvents.CHANGE_DIMENSION.post(ChangeDimensionEvent(player))
        }
    }
}