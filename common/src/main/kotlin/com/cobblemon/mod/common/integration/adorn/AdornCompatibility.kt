/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.integration.adorn

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.ResourcePackActivationBehaviour
import com.cobblemon.mod.common.util.cobblemonResource
import juuxel.adorn.block.variant.BlockKind
import juuxel.adorn.block.variant.BlockVariant
import juuxel.adorn.block.variant.BlockVariantSet
import juuxel.adorn.block.variant.BlockVariantSets
import net.minecraft.text.Text
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
object AdornCompatibility : BlockVariantSet {

    override val woodVariants: List<BlockVariant> = listOf(
        BlockVariant.Wood("${Cobblemon.MODID}/apricorn")
    )

    private val registerMethod by lazy {
        BlockVariantSets::class.java.getDeclaredMethod("register", BlockKind::class.java, BlockVariant::class.java).apply { isAccessible = true }
    }

    @ApiStatus.Internal
    fun register() {
        var count = 0
        Cobblemon.implementation.registerBuiltinResourcePack(cobblemonResource("adorncompatibility"), Text.literal("Adorn Compatibility"), ResourcePackActivationBehaviour.ALWAYS_ENABLED)
        BlockKind.values().forEach { kind ->
            this.woodVariants.forEach { variant ->
                // We use this hack because we can't just call BlockVariantSets#register
                // This would attempt to register blocks and items multiple times
                // Unfortunately Adorn does all these registrations on mod initialization on all implementations making it impossible to "correctly" add our things
                this.registerMethod.invoke(BlockVariantSets, kind, variant)
                count++
            }
        }
        Cobblemon.LOGGER.info("Registered {} blocks to Adorn", count)
    }

}