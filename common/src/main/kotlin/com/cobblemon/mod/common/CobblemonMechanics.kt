/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.ExpressionAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object CobblemonMechanics : DataRegistry {
    override val id: Identifier = cobblemonResource("mechanics")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<CobblemonMechanics>()
    val gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Expression::class.java, ExpressionAdapter)
        .create()

    var remedies = mutableMapOf<String, Expression>()

    override fun sync(player: ServerPlayerEntity) {}
    override fun reload(manager: ResourceManager) {
        manager.getResourceOrThrow(cobblemonResource("remedies")).inputStream.use {
            remedies = gson.fromJson(it.reader(), TypeToken.getParameterized(MutableMap::class.java, String::class.java, Expression::class.java).type)
        }
    }
}