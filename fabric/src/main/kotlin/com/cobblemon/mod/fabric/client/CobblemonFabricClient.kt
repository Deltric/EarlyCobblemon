/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.client

import com.cobblemon.mod.common.CobblemonClientImplementation
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.client.render.CobblemonAtlases
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.CobblemonClient.reloadCodedAssets
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds
import com.cobblemon.mod.common.particle.CobblemonParticles
import com.cobblemon.mod.common.particle.SnowstormParticleType
import com.cobblemon.mod.common.platform.events.ClientPlayerEvent
import com.cobblemon.mod.common.platform.events.ClientTickEvent
import com.cobblemon.mod.common.platform.events.ItemTooltipEvent
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.fabric.CobblemonFabric
import java.util.function.Supplier
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.item.Item
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.util.profiler.Profiler
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent
import net.minecraft.world.WorldEvents
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class CobblemonFabricClient: ClientModInitializer, CobblemonClientImplementation {
    override fun onInitializeClient() {
        registerParticleFactory(CobblemonParticles.SNOWSTORM_PARTICLE_TYPE, SnowstormParticleType::Factory)
        CobblemonClient.initialize(this)

        CobblemonFabric.networkManager.registerClientBound()

        EntityRendererRegistry.register(CobblemonEntities.POKEMON) { CobblemonClient.registerPokemonRenderer(it) }
        EntityRendererRegistry.register(CobblemonEntities.EMPTY_POKEBALL) { CobblemonClient.registerPokeBallRenderer(it) }

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(object : IdentifiableResourceReloadListener {
            override fun reload(
                synchronizer: ResourceReloader.Synchronizer?,
                manager: ResourceManager?,
                prepareProfiler: Profiler?,
                applyProfiler: Profiler?,
                prepareExecutor: Executor?,
                applyExecutor: Executor?
            ): CompletableFuture<Void> {
                //Atlases must be loaded before we reloadCodedAssets, BerryModelRepository needs the berry atlas
                val atlasFutures = mutableListOf<CompletableFuture<Void>>()
                CobblemonAtlases.atlases.forEach {
                    atlasFutures.add(it.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor))
                }
                val result = CompletableFuture.allOf(*atlasFutures.toTypedArray()).thenRun {
                    reloadCodedAssets(manager!!)
                }
                return result
            }

            override fun getFabricId() = cobblemonResource("atlases")

        })

        CobblemonKeyBinds.register(KeyBindingHelper::registerKeyBinding)

        ClientTickEvents.START_CLIENT_TICK.register { client -> PlatformEvents.CLIENT_TICK_PRE.post(ClientTickEvent.Pre(client)) }
        ClientTickEvents.END_CLIENT_TICK.register { client -> PlatformEvents.CLIENT_TICK_POST.post(ClientTickEvent.Post(client)) }
        ClientPlayConnectionEvents.JOIN.register { _, _, client -> client.player?.let { PlatformEvents.CLIENT_PLAYER_LOGIN.post(ClientPlayerEvent.Login(it)) } }
        ClientPlayConnectionEvents.DISCONNECT.register { _, client -> client.player?.let { PlatformEvents.CLIENT_PLAYER_LOGOUT.post(ClientPlayerEvent.Logout(it)) } }
        ItemTooltipCallback.EVENT.register { stack, context, lines -> PlatformEvents.CLIENT_ITEM_TOOLTIP.post(ItemTooltipEvent(stack, context, lines)) }
    }

    override fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>) {
        EntityModelLayerRegistry.registerModelLayer(modelLayer) { supplier.get() }
    }

    override fun <T : ParticleEffect> registerParticleFactory(type: ParticleType<T>, factory: (SpriteProvider) -> ParticleFactory<T>) {
        ParticleFactoryRegistry.getInstance().register(type, ParticleFactoryRegistry.PendingParticleFactory { factory(it) })
    }

    override fun registerBlockRenderType(layer: RenderLayer, vararg blocks: Block) {
        BlockRenderLayerMap.INSTANCE.putBlocks(layer, *blocks)
    }

    override fun registerItemColors(provider: ItemColorProvider, vararg items: Item) {
        ColorProviderRegistry.ITEM.register(provider, *items)
    }

    override fun registerBlockColors(provider: BlockColorProvider, vararg blocks: Block) {
        ColorProviderRegistry.BLOCK.register(provider, *blocks)
    }

    override fun <T : BlockEntity> registerBlockEntityRenderer(type: BlockEntityType<T>, factory: BlockEntityRendererFactory<T>) {
        BlockEntityRendererFactories.register(type, factory)
    }
}