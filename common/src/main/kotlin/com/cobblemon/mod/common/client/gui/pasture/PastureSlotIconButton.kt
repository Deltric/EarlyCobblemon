/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pasture

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text

class PastureSlotIconButton(
    var xPos: Int, var yPos: Int,
    onPress: PressAction
) : ButtonWidget(xPos, yPos, (SIZE * SCALE).toInt(), (SIZE * SCALE).toInt(), Text.literal("Pasture Move"), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        const val SIZE = 14
        private const val SCALE = 0.5F

        private val baseResource = cobblemonResource("textures/gui/pasture/pasture_slot_icon_move.png")
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = context.matrices,
            x = xPos / SCALE,
            y = yPos / SCALE,
            width = SIZE,
            height = SIZE,
            vOffset = if (isHovered(mouseX.toDouble(), mouseY.toDouble())) SIZE else 0,
            textureHeight = SIZE * 2,
            texture = baseResource,
            scale = SCALE
        )
    }

    fun setPos(x: Int, y: Int) {
        xPos = x
        yPos = y
    }

    override fun playDownSound(pHandler: SoundManager) {
    }

    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (xPos.toFloat()..(xPos.toFloat() + (SIZE * SCALE))) && mouseY.toFloat() in (yPos.toFloat()..(yPos.toFloat() + (SIZE * SCALE)))
}