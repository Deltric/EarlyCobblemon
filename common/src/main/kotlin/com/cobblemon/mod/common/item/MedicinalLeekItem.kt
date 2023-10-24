/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.block.MedicinalLeekBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.RaycastContext
import net.minecraft.world.World

class MedicinalLeekItem(block: MedicinalLeekBlock, settings: Settings) : AliasedBlockItem(block, settings) {

    init {
        Cobblemon.implementation.registerCompostable(this, .65F)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult = ActionResult.PASS

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val blockHitResult = PlaceableOnWaterItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY)
        val blockHitResult2 = blockHitResult.withBlockPos(blockHitResult.blockPos.up())
        val placeResult = this.place(ItemPlacementContext(ItemUsageContext(user, hand, blockHitResult2)))
        val stack = user.getStackInHand(hand)
        // This will always be true but just in case we change it down the line.
        if (!placeResult.isAccepted && this.isFood && user.canConsume(this.foodComponent?.isAlwaysEdible == true)) {
            user.setCurrentHand(hand)
            return TypedActionResult.consume(stack)
        }
        return TypedActionResult(placeResult, stack)
    }

}