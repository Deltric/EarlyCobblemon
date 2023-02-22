/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.client

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonClientImplementation
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds
import com.cobblemon.mod.common.particle.CobblemonParticles
import com.cobblemon.mod.common.particle.SnowstormParticleType
import java.util.function.Supplier
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.EntityRenderers
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.item.Item
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.resource.ReloadableResourceManagerImpl
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@EventBusSubscriber(
    modid = Cobblemon.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object CobblemonForgeClient : CobblemonClientImplementation {
    val particleFactories = mutableListOf<PreparedParticleFactory<*>>()
    class PreparedParticleFactory<T : ParticleEffect>(val type: ParticleType<T>, val factory: (SpriteProvider) -> ParticleFactory<T>) {
        fun register(event: RegisterParticleProvidersEvent) {
            // Use the lambda version of the function so that it is interpreted as a sprite-aware factory
            // otherwise the textures associated with it won't be added to the sprite atlas
            event.register(type, factory)
        }
    }
    init {
        FMLJavaModLoadingContext.get().modEventBus.addListener(this::register3dPokeballModels)
        registerParticleFactory(CobblemonParticles.SNOWSTORM_PARTICLE_TYPE, SnowstormParticleType::Factory)
    }

    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        (MinecraftClient.getInstance().resourceManager as ReloadableResourceManagerImpl)
            .registerReloader(object : SynchronousResourceReloader {
                override fun reload(resourceManager: ResourceManager) {
                    CobblemonClient.reloadCodedAssets(resourceManager)
                }
            })
        CobblemonClient.reloadCodedAssets(MinecraftClient.getInstance().resourceManager)
        MinecraftForge.EVENT_BUS.register(this)
        event.enqueueWork {
            CobblemonClient.initialize(this)
            EntityRenderers.register(CobblemonEntities.POKEMON) { CobblemonClient.registerPokemonRenderer(it) }
            EntityRenderers.register(CobblemonEntities.EMPTY_POKEBALL) { CobblemonClient.registerPokeBallRenderer(it) }
        }
        ForgeClientPlatformEventHandler.register()
    }

    override fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>) {
        ForgeHooksClient.registerLayerDefinition(modelLayer, supplier)
    }

    override fun <T : ParticleEffect> registerParticleFactory(type: ParticleType<T>, factory: (SpriteProvider) -> ParticleFactory<T>) {
        particleFactories.add(PreparedParticleFactory(type, factory))
    }

    @JvmStatic
    @SubscribeEvent
    fun onRegisterParticleProviders(event: RegisterParticleProvidersEvent) {
        for (factoryProviders in particleFactories) {
            factoryProviders.register(event)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onKeyMappingRegister(event: RegisterKeyMappingsEvent) {
        CobblemonKeyBinds.register(event::register)
    }

    internal fun registerResourceReloader(reloader: ResourceReloader) {
        (MinecraftClient.getInstance().resourceManager as ReloadableResourceManagerImpl).registerReloader(reloader)
    }

    private fun register3dPokeballModels(event: ModelEvent.RegisterAdditional) {
        PokeBalls.all().forEach { pokeball ->
            event.register(pokeball.model3d)
        }
    }

    override fun registerBlockRenderType(layer: RenderLayer, vararg blocks: Block) {
        blocks.forEach { block ->
            RenderLayers.setRenderLayer(block, layer)
        }
    }

    override fun registerItemColors(provider: ItemColorProvider, vararg items: Item) {
        MinecraftClient.getInstance().itemColors.register(provider, *items)
    }

    override fun registerBlockColors(provider: BlockColorProvider, vararg blocks: Block) {
        MinecraftClient.getInstance().blockColors.registerColorProvider(provider, *blocks)
    }

    override fun <T : BlockEntity> registerBlockEntityRenderer(type: BlockEntityType<T>, factory: BlockEntityRendererFactory<T>) {
        BlockEntityRendererFactories.register(type, factory)
    }
}