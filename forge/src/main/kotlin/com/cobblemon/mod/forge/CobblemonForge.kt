/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge

import com.cobblemon.mod.common.*
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.brewing.BrewingRecipes
import com.cobblemon.mod.common.integration.adorn.AdornCompatibility
import com.cobblemon.mod.common.item.MedicinalLeekItem
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.common.particle.CobblemonParticles
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.util.didSleep
import com.cobblemon.mod.common.util.endsWith
import com.cobblemon.mod.common.world.CobblemonStructures
import com.cobblemon.mod.common.world.feature.CobblemonFeatures
import com.cobblemon.mod.common.world.placementmodifier.CobblemonPlacementModifierTypes
import com.cobblemon.mod.common.world.predicate.CobblemonBlockPredicates
import com.cobblemon.mod.common.world.structureprocessors.CobblemonProcessorTypes
import com.cobblemon.mod.common.world.structureprocessors.CobblemonStructureProcessorListOverrides
import com.cobblemon.mod.forge.client.CobblemonForgeClient
import com.cobblemon.mod.forge.event.ForgePlatformEventHandler
import com.cobblemon.mod.forge.net.CobblemonForgeNetworkManager
import com.cobblemon.mod.forge.permission.ForgePermissionValidator
import com.cobblemon.mod.forge.worldgen.CobblemonBiomeModifiers
import com.mojang.brigadier.arguments.ArgumentType
import java.util.UUID
import kotlin.reflect.KClass
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.block.ComposterBlock
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.item.*
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.*
import net.minecraft.resource.ResourcePackProfile.PackFactory
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.GameRules
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.ToolActions
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.common.brewing.IBrewingRecipe
import net.minecraftforge.event.AddPackFindersEvent
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.OnDatapackSyncEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.event.village.VillagerTradesEvent
import net.minecraftforge.event.village.WandererTradesEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.resource.PathPackResources
import net.minecraftforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.io.File
import java.util.concurrent.ExecutionException

@Mod(Cobblemon.MODID)
class CobblemonForge : CobblemonImplementation {
    override val modAPI = ModAPI.FORGE
    private val hasBeenSynced = hashSetOf<UUID>()

    private val commandArgumentTypes = DeferredRegister.create(RegistryKeys.COMMAND_ARGUMENT_TYPE, Cobblemon.MODID)
    private val reloadableResources = arrayListOf<ResourceReloader>()
    private val queuedWork = arrayListOf<() -> Unit>()
    private val queuedBuiltinResourcePacks = arrayListOf<Triple<Identifier, Text, ResourcePackActivationBehaviour>>()

    override val networkManager: NetworkManager = CobblemonForgeNetworkManager

    init {
        with(MOD_BUS) {
            this@CobblemonForge.commandArgumentTypes.register(this)
            addListener(this@CobblemonForge::initialize)
            addListener(this@CobblemonForge::serverInit)
            Cobblemon.preInitialize(this@CobblemonForge)
            addListener(CobblemonBiomeModifiers::register)
            addListener(this@CobblemonForge::on)
            addListener(this@CobblemonForge::onAddPackFindersEvent)
        }
        with(MinecraftForge.EVENT_BUS) {
            addListener(this@CobblemonForge::onDataPackSync)
            addListener(this@CobblemonForge::onLogin)
            addListener(this@CobblemonForge::onLogout)
            addListener(this@CobblemonForge::wakeUp)
            addListener(this@CobblemonForge::handleBlockStripping)
            addListener(this@CobblemonForge::registerCommands)
            addListener(this@CobblemonForge::onReload)
            addListener(this@CobblemonForge::addCobblemonStructures)
            addListener(::onVillagerTradesRegistry)
            addListener(::onWanderingTraderRegistry)
        }
        ForgePlatformEventHandler.register()
        DistExecutor.safeRunWhenOn(Dist.CLIENT) { DistExecutor.SafeRunnable(CobblemonForgeClient::init) }
        this.attemptModCompat()
    }

    fun addCobblemonStructures(event: ServerAboutToStartEvent) {
        CobblemonStructures.registerJigsaws(event.server)
        CobblemonStructureProcessorListOverrides.register(event.server)
    }

    fun wakeUp(event: PlayerWakeUpEvent) {
        val playerEntity = event.entity as? ServerPlayerEntity ?: return
        playerEntity.didSleep()
    }

    @Suppress("UNUSED_PARAMETER")
    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        Cobblemon.LOGGER.info("Initializing...")
        this.networkManager.registerClientBound()
        this.networkManager.registerServerBound()
        event.enqueueWork {
            this.queuedWork.forEach { it.invoke() }
        }
        Cobblemon.initialize()
    }

    fun on(event: RegisterEvent) {
        event.register(RegistryKeys.POTION) {
            BrewingRecipes.registerPotionTypes()
            BrewingRecipes.getPotionRecipes().forEach { (inputDef, ingredientDef, output) ->
                BrewingRecipeRegistry.addRecipe(object : IBrewingRecipe {
                    override fun isInput(arg: ItemStack) = arg.item is PotionItem && PotionUtil.getPotion(arg) == inputDef
                    override fun isIngredient(arg: ItemStack) = ingredientDef.test(arg)
                    override fun getOutput(input: ItemStack, ingredient: ItemStack): ItemStack {
                        return if (inputDef == Potions.WATER && ingredient.item is MedicinalLeekItem) {
                            ItemStack(CobblemonItems.MEDICINAL_BREW)
                        } else if (isInput(input) && isIngredient(ingredient)) {
                            PotionUtil.setPotion(ItemStack(Items.POTION), output)
                        } else {
                            ItemStack.EMPTY
                        }
                    }
                })
            }
            BrewingRecipes.getItemRecipes().forEach { (input, ingredient, output) ->
                BrewingRecipeRegistry.addRecipe(object : IBrewingRecipe {
                    override fun isInput(arg: ItemStack) = arg.item === input
                    override fun isIngredient(arg: ItemStack) = ingredient.test(arg)
                    override fun getOutput(input: ItemStack, ingredient: ItemStack) = if (isIngredient(ingredient) && isInput(input)) ItemStack(output) else ItemStack.EMPTY
                })
            }
        }

        event.register(RegistryKeys.BLOCK_PREDICATE_TYPE) {
            CobblemonBlockPredicates.touch()
        }
        event.register(RegistryKeys.PLACEMENT_MODIFIER_TYPE) {
            CobblemonPlacementModifierTypes.touch()
        }

        event.register(RegistryKeys.STRUCTURE_PROCESSOR) {
            CobblemonProcessorTypes.touch()
        }
    }

    fun onDataPackSync(event: OnDatapackSyncEvent) {
        Cobblemon.dataProvider.sync(event.player ?: return)
    }

    fun onLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        this.hasBeenSynced.add(event.entity.uuid)
    }

    fun onLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        this.hasBeenSynced.remove(event.entity.uuid)
    }

    override fun isModInstalled(id: String) = ModList.get().isLoaded(id)

    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }

    override fun registerPermissionValidator() {
        Cobblemon.permissionValidator = ForgePermissionValidator
    }

    override fun registerSoundEvents() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonSounds.registryKey) { helper ->
                CobblemonSounds.register { identifier, sounds -> helper.register(identifier, sounds) }
            }
        }
    }

    override fun registerBlocks() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonBlocks.registryKey) { helper ->
                CobblemonBlocks.register { identifier, block -> helper.register(identifier, block) }
            }
        }
    }

    override fun registerParticles() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonParticles.registryKey) { helper ->
                CobblemonParticles.register { identifier, particleType -> helper.register(identifier, particleType) }
            }
        }
    }

    private fun handleBlockStripping(e: BlockEvent.BlockToolModificationEvent) {
        if (e.toolAction == ToolActions.AXE_STRIP) {
            val start = e.state.block
            val result = CobblemonBlocks.strippedBlocks()[start] ?: return
            e.finalState = result.getStateWithProperties(e.state)
        }
    }

    override fun registerItems() {
        with(MOD_BUS) {
            addListener<RegisterEvent> { event ->
                event.register(CobblemonItems.registryKey) { helper ->
                    CobblemonItems.register { identifier, item -> helper.register(identifier, item) }
                }
            }
            addListener<RegisterEvent> { event ->
                event.register(RegistryKeys.ITEM_GROUP) { helper ->
                    CobblemonItemGroups.register { holder ->
                        val itemGroup = ItemGroup.builder()
                            .displayName(holder.displayName)
                            .icon(holder.displayIconProvider)
                            .entries(holder.entryCollector)
                            .build()
                        helper.register(holder.key, itemGroup)
                        itemGroup
                    }
                }
            }
        }
    }

    override fun registerEntityTypes() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonEntities.registryKey) { helper ->
                CobblemonEntities.register { identifier, type -> helper.register(identifier, type) }
            }
        }
    }

    override fun registerEntityAttributes() {
        MOD_BUS.addListener<EntityAttributeCreationEvent> { event ->
            CobblemonEntities.registerAttributes { entityType, builder ->
                builder.add(ForgeMod.ENTITY_GRAVITY.get())
                    .add(ForgeMod.NAMETAG_DISTANCE.get())
                    .add(ForgeMod.SWIM_SPEED.get())
                event.put(entityType, builder.build())
            }
        }
    }

    override fun registerBlockEntityTypes() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonBlockEntities.registryKey) { helper ->
                CobblemonBlockEntities.register { identifier, type -> helper.register(identifier, type) }
            }
        }
    }

    override fun registerWorldGenFeatures() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonFeatures.registryKey) { helper ->
                CobblemonFeatures.register { identifier, feature -> helper.register(identifier, feature) }
            }
        }
    }

    override fun addFeatureToWorldGen(feature: RegistryKey<PlacedFeature>, step: GenerationStep.Feature, validTag: TagKey<Biome>?) {
        CobblemonBiomeModifiers.add(feature, step, validTag)
    }

    override fun <A : ArgumentType<*>, T : ArgumentSerializer.ArgumentTypeProperties<A>> registerCommandArgument(identifier: Identifier, argumentClass: KClass<A>, serializer: ArgumentSerializer<A, T>) {
        this.commandArgumentTypes.register(identifier.path) { ArgumentTypes.registerByClass(argumentClass.java, serializer) }
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        CobblemonCommands.register(e.dispatcher, e.buildContext, e.commandSelection)
    }

    override fun <T : GameRules.Rule<T>> registerGameRule(name: String, category: GameRules.Category, type: GameRules.Type<T>): GameRules.Key<T> = GameRules.register(name, category, type)

    override fun <T : Criterion<*>> registerCriteria(criteria: T): T = Criteria.register(criteria)

    override fun registerResourceReloader(identifier: Identifier, reloader: ResourceReloader, type: ResourceType, dependencies: Collection<Identifier>) {
        if (type == ResourceType.SERVER_DATA) {
            this.reloadableResources += reloader
        }
        else {
            CobblemonForgeClient.registerResourceReloader(reloader)
        }
    }

    private fun onReload(e: AddReloadListenerEvent) {
        this.reloadableResources.forEach(e::addListener)
    }

    override fun server(): MinecraftServer? = ServerLifecycleHooks.getCurrentServer()

    override fun <T> reloadJsonRegistry(registry: JsonDataRegistry<T>, manager: ResourceManager): HashMap<Identifier, T> {
        val data = hashMapOf<Identifier, T>()

        manager.findResources(registry.resourcePath) { path -> path.endsWith(JsonDataRegistry.JSON_EXTENSION) }.forEach { (identifier, resource) ->
            if (identifier.namespace == "pixelmon") {
                return@forEach
            }

            resource.inputStream.use { stream ->
                stream.bufferedReader().use { reader ->
                    val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                    try {
                        data[resolvedIdentifier] = registry.gson.fromJson(reader, registry.typeToken.type)
                    } catch (exception: Exception) {
                        throw ExecutionException("Error loading JSON for data: $identifier", exception)
                    }
                }
            }
        }
        return data
    }

    override fun registerCompostable(item: ItemConvertible, chance: Float) {
        this.queuedWork += {
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, chance)
        }
    }

    override fun registerBuiltinResourcePack(id: Identifier, title: Text, activationBehaviour: ResourcePackActivationBehaviour) {
        this.queuedBuiltinResourcePacks += Triple(id, title, activationBehaviour)
    }

    fun onAddPackFindersEvent(event: AddPackFindersEvent) {
        if (event.packType != ResourceType.CLIENT_RESOURCES) {
            return
        }
        val modFile = ModList.get().getModFileById(Cobblemon.MODID).file
        this.queuedBuiltinResourcePacks.forEach { (id, title, activationBehaviour) ->
            // Fabric expects resourcepacks as the root so we do too here
            val path = modFile.findResource("resourcepacks/${id.path}")
            val factory = PackFactory { name -> PathPackResources(name, true, path) }
            val profile = ResourcePackProfile.create(
                id.toString(),
                title,
                activationBehaviour == ResourcePackActivationBehaviour.ALWAYS_ENABLED,
                factory,
                ResourceType.CLIENT_RESOURCES,
                ResourcePackProfile.InsertionPosition.TOP, // Go top to match Fabric behaviour
                ResourcePackSource.BUILTIN
            )
            event.addRepositorySource { consumer -> consumer.accept(profile) }
        }
    }

    private fun onVillagerTradesRegistry(e: VillagerTradesEvent) {
        CobblemonTradeOffers.tradeOffersFor(e.type).forEach { tradeOffer ->
            // Will never be null between 1 n 5
            e.trades[tradeOffer.requiredLevel]?.addAll(tradeOffer.tradeOffers)
        }
    }

    private fun onWanderingTraderRegistry(e: WandererTradesEvent) {
        CobblemonTradeOffers.resolveWanderingTradeOffers().forEach { tradeOffer ->
            if (tradeOffer.isRareTrade) e.rareTrades.addAll(tradeOffer.tradeOffers) else e.genericTrades.addAll(tradeOffer.tradeOffers)
        }
    }

    private fun attemptModCompat() {
        if (this.isModInstalled("adorn")) {
            AdornCompatibility.register()
        }
        // CarryOn has a tag key for this but for some reason Forge version just doesn't work instead we do this :)
        // See https://github.com/Tschipp/CarryOn/wiki/IMC-support-for-Modders
        if (this.isModInstalled("carryon")) {
            InterModComms.sendTo("carryon", "blacklistEntity") { CobblemonEntities.POKEMON_KEY.toString() }
            InterModComms.sendTo("carryon", "blacklistEntity") { CobblemonEntities.EMPTY_POKEBALL_KEY.toString() }
        }
    }

}
