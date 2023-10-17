/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.transformation.triggers

import com.cobblemon.mod.common.api.pokemon.transformation.trigger.ContextTrigger
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.TriggerContext
import com.cobblemon.mod.common.pokemon.transformation.predicate.NbtItemPredicate
import com.cobblemon.mod.common.registry.ItemIdentifierCondition
import net.minecraft.item.ItemStack
import net.minecraft.predicate.NbtPredicate
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

/**
 * Represents a [ContextTrigger] with [NbtItemPredicate] context.
 * These are triggered upon interaction with any [ItemStack] that matches the given predicate.
 *
 * @property requiredContext The [NbtItemPredicate] expected to match.
 * @author Licious
 * @since March 20th, 2022
 */
open class ItemInteractionTrigger(
    override val requiredContext: NbtItemPredicate =
        NbtItemPredicate(ItemIdentifierCondition(Identifier("minecraft", "fish")), NbtPredicate.ANY),
) : ContextTrigger<NbtItemPredicate> {

    override fun testContext(context: TriggerContext) = context is Context &&
        this.requiredContext.item.fits(context.stack.item, context.world.registryManager.get(RegistryKeys.ITEM)) &&
        this.requiredContext.nbt.test(context.stack)

    data class Context(
        val stack: ItemStack,
        val world: World
    ) : TriggerContext

    companion object {
        const val ADAPTER_VARIANT = "item_interact"
    }

}