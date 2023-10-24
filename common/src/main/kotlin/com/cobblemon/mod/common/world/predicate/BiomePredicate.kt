/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.predicate

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

/**
 * A [BlockPredicate] that is satisfied if the [Biome] of the [BlockPos] is listed in [includedBiomes] and/or not in [excludedBiomes].
 *
 * @author miasmus
 * @since September 12th, 2023
 */
class BiomePredicate(
    val includedBiomes: Optional<MutableList<TagKey<Biome>>>,
    val excludedBiomes: Optional<MutableList<TagKey<Biome>>>
) : BlockPredicate {

    override fun test(world: StructureWorldAccess, block: BlockPos): Boolean {
        val biome = world.getBiome(block)
        // If biomes are not specified, default to true -- exclusions override inclusions
        return (
                (includedBiomes.getOrNull()?.any { biome.isIn(it) } ?: true) &&
                !(excludedBiomes.getOrNull()?.any { biome.isIn(it) } ?: false))
    }
    override fun getType() = CobblemonBlockPredicates.BIOME

    companion object {
        val CODEC : Codec<BiomePredicate> = RecordCodecBuilder.create { instance ->
            instance.group(
                TagKey.codec(RegistryKeys.BIOME).listOf().optionalFieldOf("includedBiomes").forGetter { it.includedBiomes },
                TagKey.codec(RegistryKeys.BIOME).listOf().optionalFieldOf("excludedBiomes").forGetter { it.excludedBiomes }
            ).apply(instance, ::BiomePredicate)
        }
    }
}