/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.block.VivichokeBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.PlaceableOnWaterItem
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.RaycastContext
import net.minecraft.world.World

class VivichokeItem(block: VivichokeBlock) : AliasedBlockItem(block, Settings()) {
    override fun use(world: World?, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack?> {
        val blockHitResult = PlaceableOnWaterItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY)
        val blockHitResult2 = blockHitResult.withBlockPos(blockHitResult.blockPos.up())
        val actionResult: ActionResult = super.useOnBlock(ItemUsageContext(user, hand, blockHitResult2))
        return TypedActionResult(actionResult, user.getStackInHand(hand))
    }
}
