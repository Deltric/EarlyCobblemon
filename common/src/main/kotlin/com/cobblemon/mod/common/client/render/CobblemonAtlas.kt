/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render

import net.minecraft.client.texture.SpriteAtlasHolder
import net.minecraft.client.texture.TextureManager
import net.minecraft.util.Identifier

class CobblemonAtlas(
    textureManager: TextureManager,
    atlasId: Identifier,
    sourcePath: Identifier
) : SpriteAtlasHolder(
    textureManager,
    atlasId,
    sourcePath
) {
}
