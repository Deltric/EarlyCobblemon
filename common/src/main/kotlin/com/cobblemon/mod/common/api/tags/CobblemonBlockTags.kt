/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tags

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

/**
 * A collection of the Cobblemon [TagKey]s related to the [Registries.BLOCK].
 *
 * @author Licious
 * @since October 29th, 2022
 */
@Suppress("HasPlatformType", "unused")
object CobblemonBlockTags {

    @JvmField val APRICORN_LEAVES = createTag("apricorn_leaves")
    @JvmField val APRICORN_LOGS = createTag("apricorn_logs")
    @JvmField val APRICORN_SAPLINGS = createTag("apricorn_saplings")
    @JvmField val APRICORNS = createTag("apricorns")
    @JvmField val BERRY_WILD_SOIL = createTag("berry_wild_soil")
    @JvmField val BERRY_SOIL = createTag("berry_soil")
    @JvmField val BERRY_REPLACEABLE = createTag("berry_replaceable")
    @JvmField val CROPS = createTag("crops")
    @JvmField val DRIPSTONE_GROWABLE = createTag("dripstone_growable")
    @JvmField val DRIPSTONE_REPLACEABLES = createTag("dripstone_replaceables")
    @JvmField val FLOWERS = createTag("flowers")
    @JvmField val MEDICINAL_LEEK_PLANTABLE = createTag("medicinal_leek_plantable")
    @JvmField val MINTS = createTag("mints")
    @JvmField val ROOTS_SPREADABLE = createTag("roots_spreadable")
    @JvmField val SMALL_FLOWERS = createTag("small_flowers")
    @JvmField val SEES_SKY = createTag("sees_sky")
    @JvmField val SNOW_BLOCK = createTag("snow_block")
    @JvmField val ROOTS = createTag("roots")
    private fun createTag(name: String) = TagKey.of(RegistryKeys.BLOCK, cobblemonResource(name))

}
