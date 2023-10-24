/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.structureprocessors

import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.structure.processor.StructureProcessor
import net.minecraft.structure.processor.StructureProcessorType

object CobblemonProcessorTypes {
    val registry = Registries.STRUCTURE_PROCESSOR
    val lists = CobblemonStructureProcessorLists

    @JvmField
    val RANDOM_POOLED_STATES = register("random_pooled_states", RandomizedStructureMappedBlockStatePairProcessor.CODEC)

    fun <T : StructureProcessor> register(id: String, codec: Codec<T>): StructureProcessorType<T> {
        return Registry.register(registry, cobblemonResource(id), StructureProcessorType { codec })
    }

    fun touch() = Unit
}
