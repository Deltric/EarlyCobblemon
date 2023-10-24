/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.predicate

import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.gen.blockpredicate.BlockPredicate
import net.minecraft.world.gen.blockpredicate.BlockPredicateType

object CobblemonBlockPredicates {
    @JvmField
    val ALTITUDE = register("altitude", AltitudePredicate.CODEC)
    val BIOME = register("biome", BiomePredicate.CODEC)

    fun <P : BlockPredicate?> register(id: String, codec: Codec<P>): BlockPredicateType<P> {
        return Registry.register(Registries.BLOCK_PREDICATE_TYPE, cobblemonResource(id), BlockPredicateType { codec })
    }

    fun touch() = Unit
}