/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.boat

import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.mixin.accessor.BoatEntityAccessor
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.WoodType
import net.minecraft.entity.EntityType
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.registry.tag.FluidTags
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.GameRules
import net.minecraft.world.World

@Suppress("unused")
open class CobblemonBoatEntity(entityType: EntityType<out BoatEntity>, world: World) : BoatEntity(entityType, world) {

    constructor(world: World) : this(CobblemonEntities.BOAT, world)

    // This exists cause super passes in vanilla boat entity type
    constructor(world: World, x: Double, y: Double, z: Double) : this(CobblemonEntities.BOAT, world) {
        this.setPosition(x, y, z)
        this.prevX = x
        this.prevY = y
        this.prevZ = z
    }

    /**
     * The [CobblemonBoatType] of this boat.
     */
    var boatType: CobblemonBoatType
        get() = CobblemonBoatType.ofOrdinal(this.dataTracker.get(TYPE_TRACKED_DATA))
        set(value) {
            this.dataTracker.set(TYPE_TRACKED_DATA, value.ordinal)
        }

    /**
     * The [WoodType] that represents this boat.
     */
    val woodType: WoodType get() = this.boatType.woodType

    /**
     * The [Block] that represents the base material for this boat.
     */
    val baseBlock: Block get() = this.boatType.baseBlock

    override fun asItem(): Item = this.boatType.boatItem

    override fun createSpawnPacket(): Packet<ClientPlayPacketListener> = EntitySpawnS2CPacket(this)

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(TYPE_TRACKED_DATA, CobblemonBoatType.APRICORN.ordinal)
    }

    override fun getDefaultName(): Text = EntityType.BOAT.name

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        if (nbt.contains(TYPE_KEY, NbtElement.STRING_TYPE.toInt())) {
            this.boatType = CobblemonBoatType.valueOf(nbt.getString(TYPE_KEY))
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        nbt.put(TYPE_KEY, NbtString.of(this.boatType.name))
    }

    override fun setVariant(type: Type) {
        throw UnsupportedOperationException("The vanilla boat type is not present in the Cobblemon implementation use the type property")
    }

    override fun getVariant(): Type {
        throw UnsupportedOperationException("The vanilla boat type is not present in the Cobblemon implementation use the type property")
    }

    override fun getPassengerHorizontalOffset(): Float = this.boatType.mountedOffset

    override fun fall(heightDifference: Double, onGround: Boolean, state: BlockState, landedPosition: BlockPos) {
        val accessor = this.accessor()
        accessor.setFallVelocity(this.velocity.y)
        if (!this.hasVehicle()) {
            return
        }
        if (!this.world.getFluidState(this.blockPos.down()).isIn(FluidTags.WATER) && heightDifference < 0.0) {
            this.fallDistance -= heightDifference.toFloat()
        }
        if (!onGround) {
            return
        }
        if (this.fallDistance < 3F || accessor.location != Location.ON_LAND) {
            this.onLanding()
            return
        }
        this.handleFallDamage(this.fallDistance, 1F, this.damageSources.fall())
        if (this.world.isClient || this.isRemoved) {
            return
        }
        this.kill()
        if (this.world.gameRules.getBoolean(GameRules.DO_ENTITY_DROPS)) {
            repeat(3) {
                this.dropItem(this.boatType.baseBlock)
            }
            repeat(2) {
                this.dropItem(Items.STICK)
            }
        }
    }

    protected fun accessor(): BoatEntityAccessor = this as BoatEntityAccessor

    companion object {

        private const val TYPE_KEY = "type"
        private val TYPE_TRACKED_DATA = DataTracker.registerData(CobblemonBoatEntity::class.java, TrackedDataHandlerRegistry.INTEGER)

    }

}