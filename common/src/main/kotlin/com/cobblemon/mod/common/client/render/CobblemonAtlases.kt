/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.SpriteAtlasHolder

object CobblemonAtlases {
    val atlases = mutableSetOf<SpriteAtlasHolder>()

    val BERRY_SPRITE_ATLAS = register("textures/atlas/berries.png", "berries")
    fun register(atlasId: String, sourcePath: String): SpriteAtlasHolder {
        val atlas = CobblemonAtlas(
            MinecraftClient.getInstance().textureManager,
            cobblemonResource(atlasId),
            cobblemonResource(sourcePath)
        )
        atlases.add(atlas)
        return atlas
    }
}