/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.block

import com.cablemc.pokemod.common.PokemodBlocks
import com.cablemc.pokemod.common.PokemodItems
import com.cablemc.pokemod.common.world.block.entity.HealingMachineBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3f

class HealingMachineRenderer<T: BlockEntity>(ctx: BlockEntityRendererFactory.Context): BlockEntityRenderer<T> {
    companion object {
        private val offsets = listOf(
            0.0 to 0.0,
            -0.64 to 0.0,
            0.64 to -0.5,
            -0.64 to 0.0,
            0.64 to -0.5,
            -0.64 to 0.0
        )
    }

    override fun render(blockEntity: T, tickDelta: Float, poseStack: MatrixStack, multiBufferSource: VertexConsumerProvider, light: Int, overlay: Int) {
        if (blockEntity !is HealingMachineBlockEntity) return

        poseStack.push()

        val blockState =
            if (blockEntity.world != null) blockEntity.cachedState else (PokemodBlocks.HEALING_MACHINE.get().defaultState.with(
                HorizontalFacingBlock.FACING, Direction.SOUTH
            ) as BlockState)
        val yRot = blockState.get(HorizontalFacingBlock.FACING).asRotation()

        // Position balls
        poseStack.translate(0.5, 0.5, 0.5)
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-yRot))
        poseStack.scale(0.5f, 0.5f, 0.5f)
        poseStack.translate(0.3, 0.4, 0.23)

        for ((index, pokeBall) in blockEntity.pokeBalls.withIndex()) {
            val offset = offsets[index]
            poseStack.translate(offset.first, 0.0, offset.second)
            MinecraftClient.getInstance().itemRenderer.renderItem(ItemStack(PokemodItems.ballMap[pokeBall]?.get() ?: Items.AIR), ModelTransformation.Mode.GROUND, light, overlay, poseStack, multiBufferSource, 0)
        }
        poseStack.pop()
    }
}