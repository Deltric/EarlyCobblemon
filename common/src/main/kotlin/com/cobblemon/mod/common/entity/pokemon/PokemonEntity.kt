/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.drop.DropTable
import com.cobblemon.mod.common.api.entity.Despawner
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.entity.PokemonEntityLoadEvent
import com.cobblemon.mod.common.api.events.entity.PokemonEntitySaveEvent
import com.cobblemon.mod.common.api.events.entity.PokemonEntitySaveToWorldEvent
import com.cobblemon.mod.common.api.events.pokemon.ShoulderMountEvent
import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.net.serializers.SeatDataSerializer
import com.cobblemon.mod.common.api.net.serializers.StringSetDataSerializer
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.reactive.ObservableSubscription
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.riding.Rideable
import com.cobblemon.mod.common.api.riding.RidingProperties
import com.cobblemon.mod.common.api.riding.seats.Seat
import com.cobblemon.mod.common.api.scheduling.afterOnMain
import com.cobblemon.mod.common.api.storage.InvalidSpeciesException
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.api.types.ElementalTypes.FIRE
import com.cobblemon.mod.common.battles.BagItems
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.command.RidingDevParameters
import com.cobblemon.mod.common.entity.EntityProperty
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.ai.PokemonMoveControl
import com.cobblemon.mod.common.entity.pokemon.ai.PokemonNavigation
import com.cobblemon.mod.common.entity.pokemon.ai.goals.*
import com.cobblemon.mod.common.net.messages.client.sound.PokemonCryPacket
import com.cobblemon.mod.common.net.messages.client.sound.UnvalidatedPlaySoundS2CPacket
import com.cobblemon.mod.common.net.messages.client.spawn.SpawnPokemonPacket
import com.cobblemon.mod.common.net.messages.client.ui.InteractPokemonUIPacket
import com.cobblemon.mod.common.net.serverhandling.storage.SendOutPokemonHandler.SEND_OUT_DURATION
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.activestate.ActivePokemonState
import com.cobblemon.mod.common.pokemon.activestate.InactivePokemonState
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState
import com.cobblemon.mod.common.pokemon.ai.FormPokemonBehaviour
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cobblemon.mod.common.pokemon.riding.SpeedCalculator
import com.cobblemon.mod.common.pokemon.riding.RidingState
import com.cobblemon.mod.common.util.*
import com.cobblemon.mod.common.world.gamerules.CobblemonGameRules
import net.minecraft.entity.*
import net.minecraft.entity.ai.control.MoveControl
import net.minecraft.entity.ai.goal.EatGrassGoal
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.ai.pathing.EntityNavigation
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.passive.TameableShoulderEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtString
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ChunkTicketType
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TextContent
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.EntityView
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import java.util.EnumSet
import java.util.Optional
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class PokemonEntity(
    world: World,
    pokemon: Pokemon = Pokemon(),
    type: EntityType<out PokemonEntity> = CobblemonEntities.POKEMON,
) : TameableShoulderEntity(type, world), Poseable, Shearable, Rideable {
    val removalObservable = SimpleObservable<RemovalReason?>()
    /** A list of observable subscriptions related to this entity that need to be cleaned up when the entity is removed. */
    val subscriptions = mutableListOf<ObservableSubscription<*>>()

    val form: FormData
        get() = pokemon.form
    val behaviour: FormPokemonBehaviour
        get() = form.behaviour

    var pokemon: Pokemon = pokemon
        set(value) {
            field = value
            delegate.changePokemon(value)
            // We need to update this value every time the Pokémon changes, other eye height related things will be dynamic.
            this.updateEyeHeight()
        }

    var despawner: Despawner<PokemonEntity> = Cobblemon.bestSpawner.defaultPokemonDespawner

    /** The player that caused this Pokémon to faint. */
    var killer: ServerPlayerEntity? = null

    var ticksLived = 0
    val busyLocks = mutableListOf<Any>()
    val isBusy: Boolean
        get() = busyLocks.isNotEmpty()
    val isBattling: Boolean
        get() = this.battleId.get().isPresent

    var drops: DropTable? = null

    var tethering: PokemonPastureBlockEntity.Tethering? = null

    /**
     * The amount of steps this entity has traveled.
     */
    var blocksTraveled: Double = 0.0

    val entityProperties = mutableListOf<EntityProperty<*>>()

    val species = addEntityProperty(SPECIES, pokemon.species.resourceIdentifier.toString())
    val nickname = addEntityProperty(NICKNAME, pokemon.nickname ?: Text.empty())
    val nicknameVisible = addEntityProperty(NICKNAME_VISIBLE, true)
    val shouldRenderName = addEntityProperty(SHOULD_RENDER_NAME, true)
    val isMoving = addEntityProperty(MOVING, false)
    val behaviourFlags = addEntityProperty(BEHAVIOUR_FLAGS, 0)
    val phasingTargetId = addEntityProperty(PHASING_TARGET_ID, -1)
    val battleId = addEntityProperty(BATTLE_ID, Optional.empty())
    val aspects = addEntityProperty(ASPECTS, pokemon.aspects)
    val deathEffectsStarted = addEntityProperty(DYING_EFFECTS_STARTED, false)
    val poseType = addEntityProperty(POSE_TYPE, PoseType.STAND)
    internal val labelLevel = addEntityProperty(LABEL_LEVEL, pokemon.level)
    internal val seatUpdater = addEntityProperty(SEAT_UPDATER, listOf())

    override val properties: RidingProperties
        get() = TODO("Not yet implemented")

    private val ridingState = RidingState(0, 0F)
    override var seats: List<Seat> = pokemon.riding.seats().map { it.create(this) }
    val hideLabel = addEntityProperty(HIDE_LABEL, false)
    val unbattleable = addEntityProperty(UNBATTLEABLE, false)

    /**
     * 0 is do nothing,
     * 1 is appearing from a pokeball so needs to be small then grows,
     * 2 is being captured/recalling so starts large and shrinks.
     */
    val beamModeEmitter = addEntityProperty(BEAM_MODE, 0.toByte())
    // properties like the above are synced and can be subscribed to for changes on either side

    val delegate = if (world.isClient) {
        // Don't import because scanning for imports is a CI job we'll do later to detect errant access to client from server
        PokemonClientDelegate()
    } else {
        PokemonServerDelegate()
    }

    init {
        delegate.initialize(this)
        delegate.changePokemon(pokemon)
        calculateDimensions()

        subscriptions.add(
            battleId
                .subscribeIncludingCurrent {
                    if (it.isPresent) {
                        busyLocks.add(BATTLE_LOCK)
                    } else {
                        busyLocks.remove(BATTLE_LOCK)
                    }
                }
        )

        subscriptions.add(
            poseType.subscribe {
                if (it == PoseType.FLY || it == PoseType.HOVER) {
                    setNoGravity(true)
                } else {
                    setNoGravity(false)
                }
            }
        )

        subscriptions.add(
            species.subscribe {
                calculateDimensions()
            }
        )
    }

    companion object {
        val SPECIES = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.STRING)
        val NICKNAME = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.TEXT_COMPONENT)
        val NICKNAME_VISIBLE = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        val SHOULD_RENDER_NAME = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        val MOVING = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        val BEHAVIOUR_FLAGS = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BYTE)
        val PHASING_TARGET_ID = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        val BEAM_MODE = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BYTE)
        val BATTLE_ID = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.OPTIONAL_UUID)
        val ASPECTS = DataTracker.registerData(PokemonEntity::class.java, StringSetDataSerializer)
        val DYING_EFFECTS_STARTED = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        val POSE_TYPE = DataTracker.registerData(PokemonEntity::class.java, PoseTypeDataSerializer)
        val LABEL_LEVEL = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        val SEAT_UPDATER = DataTracker.registerData(PokemonEntity::class.java, SeatDataSerializer)
        val HIDE_LABEL = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        val UNBATTLEABLE = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

        const val BATTLE_LOCK = "battle"

        fun createAttributes(): DefaultAttributeContainer.Builder = LivingEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)

    }

    override fun canWalkOnFluid(state: FluidState): Boolean {
//        val node = navigation.currentPath?.currentNode
//        val targetPos = node?.blockPos
//        if (targetPos == null || world.getBlockState(targetPos.up()).isAir) {
        return if (state.isIn(FluidTags.WATER)) {
            behaviour.moving.swim.canWalkOnWater
        } else if (state.isIn(FluidTags.LAVA)) {
            behaviour.moving.swim.canWalkOnLava
        } else {
            super.canWalkOnFluid(state)
        }
//        }
//
//        return super.canWalkOnFluid(state)
    }

    override fun handleStatus(status: Byte) {
        delegate.handleStatus(status)
        super.handleStatus(status)
    }

    override fun tick() {
        super.tick()
        // We will be handling idle logic ourselves thank you
        this.setDespawnCounter(0)
        entityProperties.forEach { it.checkForUpdate() }
        delegate.tick(this)
        ticksLived++
        if (this.ticksLived % 20 == 0) {
            this.updateEyeHeight()
        }

        if (this.tethering != null && !this.tethering!!.box.contains(this.x, this.y, this.z)) {
            this.tethering = null
            this.pokemon.recall()
        }
    }

    fun setMoveControl(moveControl: MoveControl) {
        this.moveControl = moveControl
    }

    /**
     * Prevents water type Pokémon from taking drowning damage.
     */
    override fun canBreatheInWater(): Boolean {
        return behaviour.moving.swim.canBreatheUnderwater
    }

    /**
     * Prevents fire type Pokémon from taking fire damage.
     */
    override fun isFireImmune(): Boolean {
        return FIRE in pokemon.types || !behaviour.moving.swim.hurtByLava
    }

    /**
     * Prevents flying type Pokémon from taking fall damage.
     */
    override fun handleFallDamage(fallDistance: Float, damageMultiplier: Float, damageSource: DamageSource?): Boolean {
        return if (ElementalTypes.FLYING in pokemon.types || pokemon.ability.name == "levitate" || pokemon.species.behaviour.moving.fly.canFly) {
            false
        } else {
            super.handleFallDamage(fallDistance, damageMultiplier, damageSource)
        }
    }

    override fun isInvulnerableTo(damageSource: DamageSource): Boolean {
        // If the entity is busy, it cannot be hurt.
        if (busyLocks.isNotEmpty()) {
            return true
        }

        // Owned Pokémon cannot be hurt by players or suffocation
        if (ownerUuid != null && (damageSource.attacker is PlayerEntity || damageSource.isOf(DamageTypes.IN_WALL))) {
            return true
        }

        if (!Cobblemon.config.playerDamagePokemon && damageSource.attacker is PlayerEntity) {
            return true
        }

        return super.isInvulnerableTo(damageSource)
    }

    /**
     * A utility method that checks if this Pokémon has the [UncatchableProperty.uncatchable] property.
     *
     * @return If the Pokémon is uncatchable.
     */
    fun isUncatchable() = pokemon.isUncatchable()

    fun recallWithAnimation(): CompletableFuture<Pokemon> {
        val owner = owner
        val future = CompletableFuture<Pokemon>()
        if (phasingTargetId.get() == -1 && owner != null) {
            owner.getWorld().playSoundServer(pos, CobblemonSounds.POKE_BALL_RECALL, volume = 0.6F)
            phasingTargetId.set(owner.id)
            beamModeEmitter.set(2)
            afterOnMain(seconds = SEND_OUT_DURATION) {
                pokemon.recall()
                future.complete(pokemon)
            }
        } else {
            pokemon.recall()
            future.complete(pokemon)
        }

        return future
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val tethering = this.tethering
        if (tethering != null) {
            val tetheringNbt = NbtCompound()
            tetheringNbt.putUuid(DataKeys.TETHERING_ID, tethering.tetheringId)
            tetheringNbt.putUuid(DataKeys.POKEMON_UUID, tethering.pokemonId)
            tetheringNbt.putUuid(DataKeys.POKEMON_OWNER_ID, tethering.playerId)
            tetheringNbt.putUuid(DataKeys.PC_ID, tethering.pcId)
            tetheringNbt.put(DataKeys.TETHER_MIN_ROAM_POS, NbtHelper.fromBlockPos(tethering.minRoamPos))
            tetheringNbt.put(DataKeys.TETHER_MAX_ROAM_POS, NbtHelper.fromBlockPos(tethering.maxRoamPos))
            nbt.put(DataKeys.TETHERING, tetheringNbt)
        } else {
            nbt.put(DataKeys.POKEMON, pokemon.saveToNBT(NbtCompound()))
        }
        val battleIdToSave = battleId.get().orElse(null)
        if (battleIdToSave != null) {
            nbt.putUuid(DataKeys.POKEMON_BATTLE_ID, battleIdToSave)
        }
        nbt.putString(DataKeys.POKEMON_POSE_TYPE, poseType.get().name)
        nbt.putByte(DataKeys.POKEMON_BEHAVIOUR_FLAGS, behaviourFlags.get())

        if (hideLabel.get()) {
            nbt.putBoolean(DataKeys.POKEMON_HIDE_LABEL, true)
        }
        if (unbattleable.get()) {
            nbt.putBoolean(DataKeys.POKEMON_UNBATTLEABLE, true)
        }

        CobblemonEvents.POKEMON_ENTITY_SAVE.post(PokemonEntitySaveEvent(this, nbt))

        return super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains(DataKeys.TETHERING)) {
            val tetheringNBT = nbt.getCompound(DataKeys.TETHERING)
            val tetheringId = tetheringNBT.getUuid(DataKeys.TETHERING_ID)
            val pcId = tetheringNBT.getUuid(DataKeys.PC_ID)
            val pokemonId = tetheringNBT.getUuid(DataKeys.POKEMON_UUID)
            val playerId = tetheringNBT.getUuid(DataKeys.POKEMON_OWNER_ID)
            val minRoamPos = NbtHelper.toBlockPos(tetheringNBT.getCompound(DataKeys.TETHER_MIN_ROAM_POS))
            val maxRoamPos = NbtHelper.toBlockPos(tetheringNBT.getCompound(DataKeys.TETHER_MAX_ROAM_POS))

            val loadedPokemon = Cobblemon.storage.getPC(pcId)[pokemonId]
            if (loadedPokemon != null && loadedPokemon.tetheringId == tetheringId) {
                pokemon = loadedPokemon
                tethering = PokemonPastureBlockEntity.Tethering(
                    minRoamPos = minRoamPos,
                    maxRoamPos = maxRoamPos,
                    playerId = playerId,
                    playerName = "",
                    tetheringId = tetheringId,
                    pokemonId = pokemonId,
                    pcId = pcId,
                    entityId = id // Doesn't really matter on the entity
                )
            } else {
                pokemon = Pokemon()
                health = 0F
            }
        } else {
            pokemon = try {
                Pokemon().loadFromNBT(nbt.getCompound(DataKeys.POKEMON))
            } catch (_: InvalidSpeciesException) {
                health = 0F
                Pokemon()
            }
        }
        species.set(pokemon.species.resourceIdentifier.toString())
        nickname.set(pokemon.nickname ?: Text.empty())
        labelLevel.set(pokemon.level)
        val savedBattleId = if (nbt.containsUuid(DataKeys.POKEMON_BATTLE_ID)) nbt.getUuid(DataKeys.POKEMON_BATTLE_ID) else null
        if (savedBattleId != null) {
            val battle = BattleRegistry.getBattle(savedBattleId)
            if (battle != null) {
                battleId.set(Optional.of(savedBattleId))
            }
        }
        poseType.set(PoseType.valueOf(nbt.getString(DataKeys.POKEMON_POSE_TYPE)))
        behaviourFlags.set(nbt.getByte(DataKeys.POKEMON_BEHAVIOUR_FLAGS))
        this.setBehaviourFlag(PokemonBehaviourFlag.EXCITED, true)

        if (nbt.contains(DataKeys.POKEMON_HIDE_LABEL)) {
            hideLabel.set(nbt.getBoolean(DataKeys.POKEMON_HIDE_LABEL))
        }
        if (nbt.contains(DataKeys.POKEMON_UNBATTLEABLE)) {
            unbattleable.set(nbt.getBoolean(DataKeys.POKEMON_UNBATTLEABLE))
        }

        CobblemonEvents.POKEMON_ENTITY_LOAD.postThen(
            event = PokemonEntityLoadEvent(this, nbt),
            ifSucceeded = {},
            ifCanceled = { this.discard() }
        )
    }

    override fun createSpawnPacket(): Packet<ClientPlayPacketListener> = CobblemonNetwork.asVanillaClientBound(SpawnPokemonPacket(this, super.createSpawnPacket() as EntitySpawnS2CPacket))

    override fun getPathfindingPenalty(nodeType: PathNodeType): Float {
        return if (nodeType == PathNodeType.OPEN) 2F else super.getPathfindingPenalty(nodeType)
    }

    override fun getNavigation(): PokemonNavigation {
        return navigation as PokemonNavigation
    }

    override fun createNavigation(world: World): EntityNavigation {
        return PokemonNavigation(world, this)
    }

    @Suppress("SENSELESS_COMPARISON")
    public override fun initGoals() {
        // DO NOT REMOVE
        // LivingEntity#getActiveEyeHeight is called in the constructor of Entity
        // Pokémon param is not available yet
        if (this.pokemon == null) {
            return
        }
        moveControl = PokemonMoveControl(this)
        goalSelector.clear { true }
        goalSelector.add(0, PokemonInBattleMovementGoal(this, 10))
        goalSelector.add(0, object : Goal() {
            override fun canStart() = this@PokemonEntity.phasingTargetId.get() != -1 || pokemon.status?.status == Statuses.SLEEP || deathEffectsStarted.get()
            override fun shouldContinue(): Boolean {
                if (pokemon.status?.status == Statuses.SLEEP && !canSleep() && !isBusy) {
                    return false
                } else if (pokemon.status?.status == Statuses.SLEEP || isBusy) {
                    return true
                }
                return false
            }
            override fun getControls() = EnumSet.allOf(Control::class.java)
        })

        goalSelector.add(1, PokemonBreatheAirGoal(this))
        goalSelector.add(2, PokemonFloatToSurfaceGoal(this))
        goalSelector.add(3, PokemonFollowOwnerGoal(this, 1.0, 8F, 2F, false))
        goalSelector.add(4, PokemonMoveIntoFluidGoal(this))
        goalSelector.add(5, SleepOnTrainerGoal(this))
        goalSelector.add(5, WildRestGoal(this))

        if (pokemon.getFeature<FlagSpeciesFeature>(DataKeys.HAS_BEEN_SHEARED) != null) {
            goalSelector.add(5, EatGrassGoal(this))
        }

        goalSelector.add(6, PokemonWanderAroundGoal(this))
        goalSelector.add(7, PokemonLookAtEntityGoal(this, ServerPlayerEntity::class.java, 5F))
        goalSelector.add(8, PokemonPointAtSpawnGoal(this))
    }

    fun canSleep(): Boolean {
        val rest = behaviour.resting
        val worldTime = (world.timeOfDay % 24000).toInt()
        val light = world.getLightLevel(blockPos)
        val block = world.getBlockState(blockPos).block
        val biome = world.getBiome(blockPos).value()

        return rest.canSleep &&
                !this.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) &&
                worldTime in this.behaviour.resting.times &&
                light in rest.light &&
                (rest.blocks.isEmpty() || rest.blocks.any { it.fits(block, this.world.registryManager.get(RegistryKeys.BLOCK)) }) &&
                (rest.biomes.isEmpty() || rest.biomes.any { it.fits(biome, this.world.registryManager.get(RegistryKeys.BIOME)) })
    }

    fun <T> addEntityProperty(accessor: TrackedData<T>, initialValue: T): EntityProperty<T> {
        val property = EntityProperty(
            dataTracker = dataTracker,
            accessor = accessor,
            initialValue = initialValue
        )
        entityProperties.add(property)
        return property
    }

    override fun createChild(level: ServerWorld, partner: PassiveEntity) = null

    override fun isReadyToSitOnPlayer(): Boolean {
        return pokemon.form.shoulderMountable
    }

    override fun interactMob(player: PlayerEntity, hand: Hand) : ActionResult {
        val itemStack = player.getStackInHand(hand)

        if (itemStack.isOf(Items.SHEARS) && this.isShearable) {
            this.sheared(SoundCategory.PLAYERS)
            this.emitGameEvent(GameEvent.SHEAR, player)
            itemStack.damage(1, player) { it.sendToolBreakStatus(hand) }
            return ActionResult.SUCCESS
        }
        else if (itemStack.isOf(Items.BUCKET)) {
            if (pokemon.getFeature<FlagSpeciesFeature>(DataKeys.CAN_BE_MILKED) != null) {
                player.playSound(SoundEvents.ENTITY_GOAT_MILK, 1.0f, 1.0f)
                val milkBucket = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.defaultStack)
                player.setStackInHand(hand, milkBucket)
                return ActionResult.success(world.isClient)
            }
        }

        if (hand == Hand.MAIN_HAND && player is ServerPlayerEntity && pokemon.getOwnerPlayer() == player) {
            if (player.isSneaking) {
                InteractPokemonUIPacket(this.getUuid(), isReadyToSitOnPlayer && pokemon in player.party(), this.canStartRiding(player)).sendToPlayer(player)
            } else {
                // TODO #105
                if (this.attemptItemInteraction(player, player.getStackInHand(hand))) return ActionResult.SUCCESS
            }
        }

        return super.interactMob(player, hand)
    }

    override fun getDimensions(pose: EntityPose): EntityDimensions {
        val scale = pokemon.form.baseScale * pokemon.scaleModifier
        return pokemon.form.hitbox.scaled(scale)
    }

    override fun canTakeDamage() = super.canTakeDamage() && !isBusy
    override fun damage(source: DamageSource?, amount: Float): Boolean {
        return if (super.damage(source, amount)) {
            if (this.health == 0F) {
                pokemon.currentHealth = 0
            } else {
//                pokemon.currentHealth = this.health.toInt()
            }
            true
        } else false
    }

    override fun shouldSave(): Boolean {
        if (ownerUuid == null && (Cobblemon.config.savePokemonToWorld || isPersistent)) {
            CobblemonEvents.POKEMON_ENTITY_SAVE_TO_WORLD.postThen(PokemonEntitySaveToWorldEvent(this)) {
                return true
            }
        }
        return tethering != null
    }

    override fun checkDespawn() {
        if (pokemon.getOwnerUUID() == null && !isPersistent && despawner.shouldDespawn(this)) {
            discard()
        }
    }

    override fun getEyeHeight(pose: EntityPose): Float = this.pokemon.form.eyeHeight(this)

    @Suppress("SENSELESS_COMPARISON")
    override fun getActiveEyeHeight(pose: EntityPose, dimensions: EntityDimensions): Float {
        // DO NOT REMOVE
        // LivingEntity#getActiveEyeHeight is called in the constructor of Entity
        // Pokémon param is not available yet
        if (this.pokemon == null) {
            return super.getActiveEyeHeight(pose, dimensions)
        }
        return this.pokemon.form.eyeHeight(this)
    }

    fun setBehaviourFlag(flag: PokemonBehaviourFlag, on: Boolean) {
        behaviourFlags.set(setBitForByte(behaviourFlags.get(), flag.bit, on))
    }

    fun getBehaviourFlag(flag: PokemonBehaviourFlag): Boolean = getBitForByte(behaviourFlags.get(), flag.bit)

    @Suppress("UNUSED_PARAMETER")
    fun canBattle(player: PlayerEntity): Boolean {
        if (unbattleable.get()) {
            return false
        } else if (isBusy) {
            return false
        } else if (battleId.get().isPresent) {
            return false
        } else if (ownerUuid != null) {
            return false
        } else if (health <= 0F || isDead) {
            return false
        }

        return true
    }

    /**
     * The level this entity should display.
     *
     * @return The level that should be displayed, if equal or lesser than 0 the level is not intended to be displayed.
     */
    fun labelLevel() = this.labelLevel.get()

    override fun playAmbientSound() {
        if (!this.isSilent || this.busyLocks.filterIsInstance<EmptyPokeBallEntity>().isEmpty()) {
            val sound = Identifier(this.pokemon.species.resourceIdentifier.namespace, "pokemon.${this.pokemon.showdownId()}.ambient")
            // ToDo distance to travel is currently hardcoded to default we can maybe find a way to work around this down the line
            UnvalidatedPlaySoundS2CPacket(sound, this.soundCategory, this.x, this.y, this.z, this.soundVolume, this.soundPitch)
                .sendToPlayersAround(this.x, this.y, this.z, 16.0, this.world.registryKey)
        }
    }

    // We never want to allow an actual sound event here, we do not register our sounds to the sound registry as species are loaded by the time the registry is frozen.
    // Super call would do the same but might as well future-proof.
    override fun getAmbientSound() = null
    override fun getMinAmbientSoundDelay() = Cobblemon.config.ambientPokemonCryTicks

    private fun attemptItemInteraction(player: PlayerEntity, stack: ItemStack): Boolean {
        if (stack.isEmpty) {
            return false
        }

        if (player is ServerPlayerEntity && isBattling) {
            val battle = battleId.get().orElse(null)?.let(BattleRegistry::getBattle) ?: return false

            val bagItemConvertible = BagItems.getConvertibleForStack(stack) ?: return false

            val battlePokemon = battle.actors.flatMap { it.pokemonList }.find { it.effectedPokemon.uuid == pokemon.uuid } ?: return false // Shouldn't be possible but anyway
            if (battlePokemon.actor.getSide().actors.none { it.isForPlayer(player)}) {
                return true
            }

            return bagItemConvertible.handleInteraction(player, battlePokemon, stack)
        }
        if (player !is ServerPlayerEntity || this.isBusy) {
            return false
        }

        // Check evolution item interaction
        if (pokemon.getOwnerPlayer() == player) {
            val context = ItemInteractionEvolution.ItemInteractionContext(stack, player.world)
            pokemon.evolutions
                .filterIsInstance<ItemInteractionEvolution>()
                .forEach { evolution ->
                    if (evolution.attemptEvolution(pokemon, context)) {
                        if (!player.isCreative) {
                            stack.decrement(1)
                        }
                        this.world.playSoundServer(position = this.pos, sound = CobblemonSounds.ITEM_USE, volume = 1F, pitch = 1F)
                        return true
                    }
                }
        }

        (stack.item as? PokemonEntityInteraction)?.let {
            if (it.onInteraction(player, this, stack)) {
                it.sound?.let {
                    this.world.playSoundServer(
                        position = this.pos,
                        sound = it,
                        volume = 1F,
                        pitch = 1F
                    )
                }
                return true
            }
        }
        return false
    }

    fun offerHeldItem(player: PlayerEntity, stack: ItemStack): Boolean {
        if (player !is ServerPlayerEntity || this.isBusy || this.pokemon.getOwnerPlayer() != player) {
            return false
        }
        // We want the count of 1 in order to match the ItemStack#areEqual
        val giving = stack.copy().apply { count = 1 }
        val possibleReturn = this.pokemon.heldItemNoCopy()
        if (stack.isEmpty && possibleReturn.isEmpty) {
            return false
        }
        if (ItemStack.areEqual(giving, possibleReturn)) {
            player.sendMessage(lang("held_item.already_holding", this.pokemon.getDisplayName(), stack.name))
            return true
        }
        val returned = this.pokemon.swapHeldItem(stack, !player.isCreative)
        val text = when {
            giving.isEmpty -> lang("held_item.take", returned.name, this.pokemon.getDisplayName())
            returned.isEmpty -> lang("held_item.give", this.pokemon.getDisplayName(), giving.name)
            else -> lang("held_item.replace", returned.name, this.pokemon.getDisplayName(), giving.name)
        }
        player.giveOrDropItemStack(returned)
        player.sendMessage(text)
        return true
    }

    fun tryMountingShoulder(player: ServerPlayerEntity): Boolean {
        if (this.pokemon.belongsTo(player) && this.hasRoomToMount(player)) {
            CobblemonEvents.SHOULDER_MOUNT.postThen(ShoulderMountEvent(player, pokemon, isLeft = player.shoulderEntityLeft.isEmpty)) {
                val dirToPlayer = player.eyePos.subtract(pos).multiply(1.0, 0.0, 1.0).normalize()
                velocity = dirToPlayer.multiply(0.8).add(0.0, 0.5, 0.0)
                val lock = Any()
                busyLocks.add(lock)
                afterOnMain(seconds = 0.5F) {
                    busyLocks.remove(lock)
                    if (!isBusy && isAlive) {
                        val isLeft = player.shoulderEntityLeft.isEmpty
                        if (isLeft || player.shoulderEntityRight.isEmpty) {
                            pokemon.state = ShoulderedState(player.uuid, isLeft, pokemon.uuid)
                            this.mountOnto(player)
                            this.pokemon.form.shoulderEffects.forEach { it.applyEffect(this.pokemon, player, isLeft) }
                            this.world.playSoundServer(position = this.pos, sound = SoundEvents.ENTITY_ITEM_PICKUP, volume = 0.7F, pitch = 1.4F)
                            discard()
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    override fun mountOnto(player: ServerPlayerEntity): Boolean {
        if (!super.mountOnto(player)) {
            return false
        }
        val nbt = when {
            player.shoulderEntityRight.isPokemonEntity() && player.shoulderEntityRight.getCompound(DataKeys.POKEMON).getUuid(DataKeys.POKEMON_UUID) == this.pokemon.uuid -> player.shoulderEntityRight
            player.shoulderEntityLeft.isPokemonEntity() && player.shoulderEntityLeft.getCompound(DataKeys.POKEMON).getUuid(DataKeys.POKEMON_UUID) == this.pokemon.uuid -> player.shoulderEntityLeft
            else -> return true
        }
        nbt.putUuid(DataKeys.SHOULDER_UUID, this.pokemon.uuid)
        nbt.putString(DataKeys.SHOULDER_SPECIES, this.pokemon.species.resourceIdentifier.toString())
        nbt.putString(DataKeys.SHOULDER_FORM, this.pokemon.form.name)
        nbt.put(DataKeys.SHOULDER_ASPECTS, this.pokemon.aspects.map(NbtString::of).toNbtList())
        nbt.putFloat(DataKeys.SHOULDER_SCALE_MODIFIER, this.pokemon.scaleModifier)
        return true
    }

    override fun remove(reason: RemovalReason) {
        val stateEntity = (pokemon.state as? ActivePokemonState)?.entity
        super.remove(reason)
        if (stateEntity == this) {
            pokemon.state = InactivePokemonState()
        }
        subscriptions.forEach(ObservableSubscription<*>::unsubscribe)
        removalObservable.emit(reason)

        if (reason.shouldDestroy() && pokemon.tetheringId != null) {
            pokemon.tetheringId = null
        }
    }

    // Copy and paste of how vanilla checks it, unfortunately no util method you can only add then wait for the result
    fun hasRoomToMount(player: PlayerEntity): Boolean {
        return (player.shoulderEntityLeft.isEmpty || player.shoulderEntityRight.isEmpty)
                && !player.hasVehicle()
                && player.isOnGround
                && !player.isTouchingWater
                && !player.inPowderSnow
    }

    fun cry() {
        val pkt = PokemonCryPacket(id)
        world.getEntitiesByClass(ServerPlayerEntity::class.java, Box.of(pos, 64.0, 64.0, 64.0), { true }).forEach {
            it.sendPacket(pkt)
        }
    }

    override fun drop(source: DamageSource?) {
        if (pokemon.isWild()) {
            super.drop(source)
            delegate.drop(source)
        }
    }

    override fun dropXp() {
        // Copied over the entire function because it's the simplest way to switch out the gamerule check
        if (
            world is ServerWorld && !this.isExperienceDroppingDisabled &&
            (shouldAlwaysDropXp() || playerHitTimer > 0 && shouldDropXp() && world.gameRules.getBoolean(CobblemonGameRules.DO_POKEMON_LOOT))
        ) {
            ExperienceOrbEntity.spawn(world as ServerWorld, pos, this.xpToDrop)
        }
    }

    override fun updatePostDeath() {
        // Do not invoke super we need to keep a tight lid on this due to the Thorium mod forcing the ticks to a max of 20 on server side if we invoke a field update here
        // Client delegate is mimicking expected behavior on client end.
        delegate.updatePostDeath()
    }

    override fun onEatingGrass() {
        super.onEatingGrass()

        val feature = pokemon.getFeature<FlagSpeciesFeature>(DataKeys.HAS_BEEN_SHEARED)
        if (feature != null) {
            feature.enabled = false
            pokemon.markFeatureDirty(feature)
            pokemon.updateAspects()
        }
    }

    override fun travel(movementInput: Vec3d) {
        val prevBlockPos = this.blockPos
        super.travel(movementInput)
        this.updateBlocksTraveled(prevBlockPos)
    }

    private fun updateBlocksTraveled(fromBp: BlockPos) {
        // Riding or falling shouldn't count, other movement sources are fine
        if (this.hasVehicle() || this.isFalling()) {
            return
        }
        val blocksTaken = this.blockPos.getSquaredDistance(fromBp)
        if (blocksTaken > 0) this.blocksTraveled += blocksTaken
    }

    private fun updateEyeHeight() {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (this as com.cobblemon.mod.common.mixin.accessor.AccessorEntity).standingEyeHeight(this.getActiveEyeHeight(EntityPose.STANDING, this.type.dimensions))
    }

    fun isFlying() = this.getBehaviourFlag(PokemonBehaviourFlag.FLYING)
    fun couldStopFlying() = isFlying() && !behaviour.moving.walk.avoidsLand && behaviour.moving.walk.canWalk
    fun isFalling() = this.fallDistance > 0 && this.world.getBlockState(this.blockPos.down()).isAir && !this.isFlying()
    fun getIsSubmerged() = isInLava || isSubmergedInWater
    override fun getPoseType(): PoseType = this.poseType.get()

    /**
     * Returns the [Species.translatedName] of the backing [pokemon].
     *
     * @return The [Species.translatedName] of the backing [pokemon].
     */
    override fun getDefaultName(): Text = this.pokemon.species.translatedName

    /**
     * If this Pokémon has a nickname, then the nickname is returned.
     * Otherwise, [getDefaultName] is returned
     *
     * @return The current display name of this entity.
     */
    override fun getName(): Text {
        if (!nicknameVisible.get()) return defaultName
        return nickname.get()?.takeIf { it.content != TextContent.EMPTY } ?: pokemon.getDisplayName()
    }

    /**
     * Returns the custom name of this entity, in the context of Cobblemon it is the [Pokemon.nickname].
     *
     * @return The nickname of the backing [pokemon].
     */
    override fun getCustomName(): Text? = pokemon.nickname

    /**
     * Sets the custom name of this entity.
     * In the context of a Pokémon entity this affects the [Pokemon.nickname].
     *
     * @param name The new name being set, if null the [Pokemon.nickname] is removed.
     */
    override fun setCustomName(name: Text?) {
        // We do this as a compromise to keep as much compatibility as possible with other mods expecting this entity to act like a vanilla one
        this.pokemon.nickname = Text.literal(name?.string)
    }

    /**
     * Checks if the backing [pokemon] has a non-null [Pokemon.nickname].
     *
     * @return If the backing [pokemon] has a non-null [Pokemon.nickname].
     */
    override fun hasCustomName(): Boolean = pokemon.nickname != null && pokemon.nickname?.content != TextContent.EMPTY

    /**
     * This method toggles the visibility of the entity name,
     * Unlike the vanilla implementation in our context it changes between displaying the species name or nickname of the Pokémon.
     *
     * @param visible The state of custom name visibility.
     */
    override fun setCustomNameVisible(visible: Boolean) {
        // We do this as a compromise to keep as much compatibility as possible with other mods expecting this entity to act like a vanilla one
        nicknameVisible.set(visible)
    }

    /**
     * In the context of a Pokémon entity this checks if the Pokémon is currently set to displaying its nickname.
     *
     * @return If the custom name of this entity should display, in this case the [getCustomName] is the nickname but if null the [getDefaultName] will be used.
     */
    override fun isCustomNameVisible(): Boolean = nicknameVisible.get()

    /**
     * Returns whether the entity is currently set to having its name displayed.
     *
     * @return If this entity should render the name label.
     */
    override fun shouldRenderName(): Boolean = shouldRenderName.get()

    /**
     * Sets the entity to having its name hidden.
     */
    fun hideNameRendering() { shouldRenderName.set(false) }

    override fun isBreedingItem(stack: ItemStack): Boolean = false

    override fun canBreedWith(other: AnimalEntity): Boolean = false

    override fun breed(world: ServerWorld, other: AnimalEntity) {}

    override fun method_48926(): EntityView {
        return this.world
    }

    override fun sheared(shearedSoundCategory: SoundCategory) {
        this.world.playSoundFromEntity(null, this,SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F)
        val feature = this.pokemon.getFeature<FlagSpeciesFeature>(DataKeys.HAS_BEEN_SHEARED) ?: return
        feature.enabled = true
        this.pokemon.markFeatureDirty(feature)
        this.pokemon.updateAspects()
        val i = this.random.nextInt(3) + 1
        for (j in 0 until i) {
            val itemEntity = this.dropItem(Items.WHITE_WOOL, 1) ?: return
            itemEntity.velocity = itemEntity.velocity.add(
                ((this.random.nextFloat() - this.random.nextFloat()) * 0.1f).toDouble(),
                (this.random.nextFloat() * 0.05f).toDouble(),
                ((this.random.nextFloat() - this.random.nextFloat()) * 0.1f).toDouble()
            )
        }
    }

    override fun isShearable(): Boolean {
        val feature = this.pokemon.getFeature<FlagSpeciesFeature>(DataKeys.HAS_BEEN_SHEARED) ?: return false
        return !this.isBusy && !this.pokemon.isFainted() && !feature.enabled
    }

    override fun canUsePortals() = false

    override fun canStartRiding(entity: Entity): Boolean {
        if(this.pokemon.riding.supported() && super.canStartRiding(entity)) {
            val seats = this.seats
            return seats.any { it.acceptsRider(entity) }
        }

        return false
    }

    override fun tickControlled(controllingPlayer: PlayerEntity, movementInput: Vec3d) {
        super.tickControlled(controllingPlayer, movementInput)

        if (movementInput != Vec3d.ZERO) {
            this.isMoving.set(true)
            this.ridingState.momentum = SpeedCalculator.calculate(
                ++this.ridingState.tick,
                movementInput,
                RidingDevParameters.speed,
                RidingDevParameters.acceleration,
                RidingDevParameters.weight
            )
        } else {
            this.ridingState.tick = 0
            this.ridingState.momentum = 0.5F
            this.isMoving.set(false)
        }

        if (this.isSubmergedInWater && this.shouldDismountUnderwater()) {
            this.seats.forEach { it.dismount(false) }
            this.removeAllPassengers()

            this.seatUpdater.set(this.seats.map { it.toDTO() })
        }

        val vec2f: Vec2f = this.getControlledRotation(controllingPlayer)
        setRotation(vec2f.y, vec2f.x)
        this.headYaw = this.yaw
        this.bodyYaw = this.yaw
        this.prevYaw = this.yaw

        this.updatePoseType()
    }

    private fun getControlledRotation(controllingPassenger: LivingEntity): Vec2f {
        return Vec2f(controllingPassenger.pitch * 0.5f, controllingPassenger.yaw)
    }

    override fun updatePassengerPosition(passenger: Entity, positionUpdater: PositionUpdater) {
        if (this.hasPassenger(passenger)) {
            val seat = this.seats.firstOrNull { it.occupant() == passenger }
            if (seat != null) {
                val offset = seat.properties.offset.rotateY(-this.bodyYaw * (Math.PI.toFloat() / 180))
                positionUpdater.accept(passenger, this.x + offset.x, this.y + offset.y, this.z + offset.z)
                if (passenger is LivingEntity) {
                    passenger.bodyYaw = this.bodyYaw
                }
            }
        }
    }

    override fun getControllingPassenger(): LivingEntity? {
        val seat = this.seats.firstOrNull { it.properties.driver }
        val occupant = seat?.occupant()

        if (occupant is LivingEntity) {
            return occupant
        }

        return null
    }

    override fun updatePassengerForDismount(passenger: LivingEntity): Vec3d {
        val seat = this.seats.firstOrNull { it.occupant() == passenger }
        seat?.dismount()
        return super.updatePassengerForDismount(passenger)
    }

    override fun getControlledMovementInput(controllingPlayer: PlayerEntity, movementInput: Vec3d): Vec3d {
        super.getControlledMovementInput(controllingPlayer, movementInput)
        val f = controllingPlayer.sidewaysSpeed * 0.2f
        var g = controllingPlayer.forwardSpeed * 0.5f
        if (g <= 0.0f) {
            g *= 0.25f
        }

        return Vec3d(f.toDouble(), 0.0, g.toDouble())
    }

    override fun getSaddledSpeed(controllingPlayer: PlayerEntity): Float {
//        val speed = this.capabilities.capability(this)
//            ?.attribute(RidingAttributeOption.SPEED)
//            ?.apply()

        // return speed ?? this.pokemon.form.behaviour.moving.walk.walkSpeed
//        return this.pokemon.form.behaviour.moving.walk.walkSpeed
        return this.ridingState.momentum
    }

    override fun setJumpStrength(strength: Int) {
        // See if this controls the hot bar element

    }

    override fun canJump(): Boolean {
        return true
    }

    override fun startJumping(height: Int) {

    }

    override fun stopJumping() {
        // Set back to land pose type?
    }

    override fun shouldDismountUnderwater(): Boolean {
        return true
    }

    fun updatePoseType() {
        val isSleeping = this.pokemon.status?.status == Statuses.SLEEP && this.behaviour.resting.canSleep
        val isMoving = this.isMoving.get()
        val isUnderwater = this.getIsSubmerged()
        val isFlying = this.getBehaviourFlag(PokemonBehaviourFlag.FLYING)

        val poseType = when {
            isSleeping -> PoseType.SLEEP
            isMoving && isUnderwater -> PoseType.SWIM
            isUnderwater -> PoseType.FLOAT
            isMoving && isFlying -> PoseType.FLY
            isFlying -> PoseType.HOVER
            isMoving -> PoseType.WALK
            else -> PoseType.STAND
        }

        if (poseType != this.poseType.get()) {
            this.poseType.set(poseType)
        }
    }


    override fun setAir(air: Int) {
        if (this.isBattling) {
            this.dataTracker.set(AIR, 300)
            return
        }
        super.setAir(air)
    }

    override fun onStoppedTrackingBy(player: ServerPlayerEntity?) {
        if (player == null) {
            return
        }

        if(this.ownerUuid == player.uuid && tethering == null) {
            if (player.isDisconnected) {
                this.remove(RemovalReason.DISCARDED)
                return
            }

            val chunkPos = ChunkPos(BlockPos(x.toInt(), y.toInt(), z.toInt()))
            (world as ServerWorld).chunkManager
                .addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 0, id)
            this.goalSelector.tick()
            if(distanceTo(player.blockPos) > 100) pokemon.recall()
        }
    }

    override fun canBeLeashedBy(player: PlayerEntity): Boolean {
        return this.ownerUuid == null || this.ownerUuid == player.uuid
    }
}
