/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.spawn

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.entity.Entity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class SpawnPokemonPacket(
    private val ownerId: UUID?,
    private val scaleModifier: Float,
    private val species: Species,
    private val form: FormData,
    private val aspects: Set<String>,
    private val phasingTargetId: Int,
    private val beamModeEmitter: Byte,
    private val nickname: MutableText?,
    private val labelLevel: Int,
    private val poseType: PoseType,
    private val unbattlable: Boolean,
    private val hideLabel: Boolean,
    vanillaSpawnPacket: EntitySpawnS2CPacket
) : SpawnExtraDataEntityPacket<SpawnPokemonPacket, PokemonEntity>(vanillaSpawnPacket) {

    override val id: Identifier = ID

    constructor(entity: PokemonEntity, vanillaSpawnPacket: EntitySpawnS2CPacket) : this(
        entity.ownerUuid,
        entity.pokemon.scaleModifier,
        entity.pokemon.species,
        entity.pokemon.form,
        entity.pokemon.aspects,
        entity.phasingTargetId.get(),
        entity.beamModeEmitter.get(),
        entity.pokemon.nickname,
        if (Cobblemon.config.displayEntityLevelLabel) entity.labelLevel.get() else -1,
        entity.getPoseType(),
        entity.unbattleable.get(),
        entity.hideLabel.get(),
        vanillaSpawnPacket
    )

    override fun encodeEntityData(buffer: PacketByteBuf) {
        buffer.writeNullable(ownerId) { _, v -> buffer.writeUuid(v) }
        buffer.writeFloat(this.scaleModifier)
        buffer.writeIdentifier(this.species.resourceIdentifier)
        buffer.writeString(this.form.formOnlyShowdownId())
        buffer.writeCollection(this.aspects) { pb, value -> pb.writeString(value) }
        buffer.writeInt(this.phasingTargetId)
        buffer.writeByte(this.beamModeEmitter.toInt())
        buffer.writeNullable(this.nickname) { _, v -> buffer.writeText(v) }
        buffer.writeInt(this.labelLevel)
        buffer.writeEnumConstant(this.poseType)
        buffer.writeBoolean(this.unbattlable)
        buffer.writeBoolean(this.hideLabel)
    }

    override fun applyData(entity: PokemonEntity) {
        entity.ownerUuid = ownerId
        entity.pokemon.apply {
            scaleModifier = this@SpawnPokemonPacket.scaleModifier
            species = this@SpawnPokemonPacket.species
            form = this@SpawnPokemonPacket.form
            aspects = this@SpawnPokemonPacket.aspects
            nickname = this@SpawnPokemonPacket.nickname
        }
        entity.phasingTargetId.set(this.phasingTargetId)
        entity.beamModeEmitter.set(this.beamModeEmitter)
        entity.labelLevel.set(this.labelLevel)
        entity.species.set(entity.pokemon.species.resourceIdentifier.toString())
        entity.aspects.set(aspects)
        entity.poseType.set(poseType)
        entity.unbattleable.set(unbattlable)
        entity.hideLabel.set(hideLabel)
    }

    override fun checkType(entity: Entity): Boolean = entity is PokemonEntity

    companion object {
        val ID = cobblemonResource("spawn_pokemon_entity")
        fun decode(buffer: PacketByteBuf): SpawnPokemonPacket {
            val ownerId = buffer.readNullable { buffer.readUuid() }
            val scaleModifier = buffer.readFloat()
            val species = PokemonSpecies.getByIdentifier(buffer.readIdentifier())!!
            val showdownId = buffer.readString()
            val form = species.forms.firstOrNull { it.formOnlyShowdownId() == showdownId } ?: species.standardForm
            val aspects = buffer.readList(PacketByteBuf::readString).toSet()
            val phasingTargetId = buffer.readInt()
            val beamModeEmitter = buffer.readByte()
            val nickname = buffer.readNullable { buffer.readText().copy() }
            val labelLevel = buffer.readInt()
            val poseType = buffer.readEnumConstant(PoseType::class.java)
            val unbattlable = buffer.readBoolean()
            val hideLabel = buffer.readBoolean()
            val vanillaPacket = decodeVanillaPacket(buffer)
            return SpawnPokemonPacket(ownerId, scaleModifier, species, form, aspects, phasingTargetId, beamModeEmitter, nickname, labelLevel, poseType, unbattlable, hideLabel, vanillaPacket)
        }
    }

}