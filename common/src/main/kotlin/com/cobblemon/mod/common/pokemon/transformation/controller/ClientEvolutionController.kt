/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.transformation.controller

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.pokemon.transformation.evolution.EvolutionController
import com.cobblemon.mod.common.api.pokemon.transformation.evolution.EvolutionDisplay
import com.cobblemon.mod.common.api.pokemon.transformation.progress.TransformationProgress
import com.cobblemon.mod.common.net.messages.client.pokemon.update.evolution.AddEvolutionPacket
import com.cobblemon.mod.common.net.messages.server.pokemon.update.evolution.AcceptEvolutionPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf

class ClientEvolutionController(override val pokemon: Pokemon) : EvolutionController<EvolutionDisplay> {

    private val evolutions = hashSetOf<EvolutionDisplay>()

    override val size: Int
        get() = this.evolutions.size

    override fun start(evolution: EvolutionDisplay) {
        CobblemonNetwork.sendPacketToServer(AcceptEvolutionPacket(this.pokemon, evolution))
    }

    override fun progress(): Collection<TransformationProgress<*>> {
        // Nothing is done on the client
        return emptyList()
    }

    override fun <P : TransformationProgress<*>> trackProgress(progress: P): P {
        // Nothing is done on the client
        return progress
    }

    override fun <P : TransformationProgress<*>> progressFirstOrCreate(predicate: (progress: TransformationProgress<*>) -> Boolean, progressFactory: () -> P): P {
        // Nothing is done on the client
        return progressFactory()
    }

    override fun saveToNBT(): NbtElement {
        return NbtCompound()
    }

    override fun loadFromNBT(nbt: NbtElement) {
        // Nothing is done on the client
    }

    override fun saveToJson(): JsonElement {
        return JsonArray()
    }

    override fun loadFromJson(json: JsonElement) {
        // Nothing is done on the client
    }

    override fun saveToBuffer(buffer: PacketByteBuf, toClient: Boolean) {
        // Nothing is done on the client
    }

    override fun loadFromBuffer(buffer: PacketByteBuf) {
        buffer.readList(AddEvolutionPacket::decodeDisplay).forEach { this.add(it) }
    }

    override fun add(element: EvolutionDisplay) = this.evolutions.add(element)

    override fun addAll(elements: Collection<EvolutionDisplay>) = this.evolutions.addAll(elements)

    override fun clear() {
        this.evolutions.clear()
    }

    override fun iterator() = this.evolutions.iterator()

    override fun remove(element: EvolutionDisplay) = this.evolutions.remove(element)

    override fun removeAll(elements: Collection<EvolutionDisplay>) = this.evolutions.removeAll(elements.toSet())

    override fun retainAll(elements: Collection<EvolutionDisplay>) = this.evolutions.retainAll(elements.toSet())

    override fun contains(element: EvolutionDisplay) = this.evolutions.contains(element)

    override fun containsAll(elements: Collection<EvolutionDisplay>) = this.evolutions.containsAll(elements)

    override fun isEmpty() = this.evolutions.isEmpty()

}