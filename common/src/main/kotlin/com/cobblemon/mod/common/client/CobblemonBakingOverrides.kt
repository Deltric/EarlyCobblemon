/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client

import com.cobblemon.mod.common.BakingOverride
import com.cobblemon.mod.common.util.cobblemonModel
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.util.Identifier

/**
 * The purpose of this class is to hold models that we want baked, but aren't associated with
 * any block state. Actual registration happens in ModelLoaderMixin/ModelLoader
 */
object CobblemonBakingOverrides {
    val models = mutableListOf<BakingOverride>()

    val FOSSIL_FLUID_BUBBLING = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_bubbling"),
        cobblemonModel("fossil_fluid_bubbling", "none")
    )
    val FOSSIL_FLUID_CHUNKED_1 = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_chunked_1"),
        cobblemonModel("fossil_fluid_chunked", "1")
    )
    val FOSSIL_FLUID_CHUNKED_2 = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_chunked_2"),
        cobblemonModel("fossil_fluid_chunked", "2")
    )
    val FOSSIL_FLUID_CHUNKED_3 = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_chunked_3"),
        cobblemonModel("fossil_fluid_chunked", "3")
    )
    val FOSSIL_FLUID_CHUNKED_4 = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_chunked_4"),
        cobblemonModel("fossil_fluid_chunked", "4")
    )
    val FOSSIL_FLUID_CHUNKED_5 = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_chunked_5"),
        cobblemonModel("fossil_fluid_chunked", "5")
    )
    val FOSSIL_FLUID_CHUNKED_6 = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_chunked_6"),
        cobblemonModel("fossil_fluid_chunked", "6")
    )
    val FOSSIL_FLUID_CHUNKED_7 = registerOverride(
        cobblemonResource("block/fossil/fossil_fluid_chunked_7"),
        cobblemonModel("fossil_fluid_chunked", "7")
    )
    val RESTORATION_TANK_CONNECTOR = registerOverride(
        cobblemonResource("block/fossil/restoration_tank_connector"),
        cobblemonModel("restoration_tank_connector", "none")
    )

    fun registerOverride(modelLocation: Identifier, modelIdentifier: ModelIdentifier): BakingOverride {
        val result = BakingOverride(modelLocation, modelIdentifier)
        models.add(result)
        return result
    }
}