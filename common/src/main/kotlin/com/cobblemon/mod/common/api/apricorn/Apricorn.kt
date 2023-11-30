/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.apricorn

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.block.ApricornBlock
import com.cobblemon.mod.common.block.ApricornSaplingBlock
import com.cobblemon.mod.common.item.ApricornItem
import com.cobblemon.mod.common.world.feature.CobblemonConfiguredFeatures
import com.mojang.serialization.Codec
import net.minecraft.block.MapColor
import net.minecraft.item.BlockItem
import net.minecraft.registry.RegistryKey
import net.minecraft.util.StringIdentifiable
import net.minecraft.world.gen.feature.ConfiguredFeature

/**
 * Contains the different Apricorn variants and util methods to get their item, block, seedling and sapling form.
 *
 * @author Licious
 * @since October 29th, 2022
 */
enum class Apricorn: StringIdentifiable {

    BLACK,
    BLUE,
    GREEN,
    PINK,
    RED,
    WHITE,
    YELLOW;

    override fun asString(): String {
        return this.name.lowercase()
    }

    fun item(): ApricornItem = when(this) {
        BLACK -> CobblemonItems.BLACK_APRICORN
        BLUE -> CobblemonItems.BLUE_APRICORN
        GREEN -> CobblemonItems.GREEN_APRICORN
        PINK -> CobblemonItems.PINK_APRICORN
        RED -> CobblemonItems.RED_APRICORN
        WHITE -> CobblemonItems.WHITE_APRICORN
        YELLOW -> CobblemonItems.YELLOW_APRICORN
    }

    fun seed(): BlockItem = when(this) {
        BLACK -> CobblemonItems.BLACK_APRICORN_SEED
        BLUE -> CobblemonItems.BLUE_APRICORN_SEED
        GREEN -> CobblemonItems.GREEN_APRICORN_SEED
        PINK -> CobblemonItems.PINK_APRICORN_SEED
        RED -> CobblemonItems.RED_APRICORN_SEED
        WHITE -> CobblemonItems.WHITE_APRICORN_SEED
        YELLOW -> CobblemonItems.YELLOW_APRICORN_SEED
    }

    fun block(): ApricornBlock = when(this) {
        BLACK -> CobblemonBlocks.BLACK_APRICORN
        BLUE -> CobblemonBlocks.BLUE_APRICORN
        GREEN -> CobblemonBlocks.GREEN_APRICORN
        PINK -> CobblemonBlocks.PINK_APRICORN
        RED -> CobblemonBlocks.RED_APRICORN
        WHITE -> CobblemonBlocks.WHITE_APRICORN
        YELLOW -> CobblemonBlocks.YELLOW_APRICORN
    }

    fun sapling(): ApricornSaplingBlock = when(this) {
        BLACK -> CobblemonBlocks.BLACK_APRICORN_SAPLING
        BLUE -> CobblemonBlocks.BLUE_APRICORN_SAPLING
        GREEN -> CobblemonBlocks.GREEN_APRICORN_SAPLING
        PINK -> CobblemonBlocks.PINK_APRICORN_SAPLING
        RED -> CobblemonBlocks.RED_APRICORN_SAPLING
        WHITE -> CobblemonBlocks.WHITE_APRICORN_SAPLING
        YELLOW -> CobblemonBlocks.YELLOW_APRICORN_SAPLING
    }

    fun mapColor(): MapColor = when(this) {
        BLACK -> MapColor.BLACK
        BLUE ->  MapColor.BLUE
        GREEN ->  MapColor.GREEN
        PINK ->  MapColor.PINK
        RED ->  MapColor.RED
        WHITE ->  MapColor.WHITE
        YELLOW ->  MapColor.YELLOW
    }

    fun configuredFeature(): RegistryKey<ConfiguredFeature<*, *>> = when(this) {
        BLACK -> CobblemonConfiguredFeatures.BLACK_APRICORN_TREE_KEY
        BLUE -> CobblemonConfiguredFeatures.BLUE_APRICORN_TREE_KEY
        GREEN -> CobblemonConfiguredFeatures.GREEN_APRICORN_TREE_KEY
        PINK -> CobblemonConfiguredFeatures.PINK_APRICORN_TREE_KEY
        RED -> CobblemonConfiguredFeatures.RED_APRICORN_TREE_KEY
        WHITE -> CobblemonConfiguredFeatures.WHITE_APRICORN_TREE_KEY
        YELLOW -> CobblemonConfiguredFeatures.YELLOW_APRICORN_TREE_KEY
    }

    companion object {
        val CODEC: Codec<Apricorn> = StringIdentifiable.createBasicCodec(::values)
    }

}