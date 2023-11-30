/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.wheel

import com.cobblemon.mod.common.api.gui.blitk
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Vector3f
import org.joml.Vector4f

class InteractWheelButton(
    private val iconResource: Identifier?,
    private val buttonResource: Identifier,
    x: Int,
    y: Int,
    private val isEnabled: Boolean,
    private val colour: () -> Vector3f?,
    onPress: PressAction
) : ButtonWidget(x, y, BUTTON_SIZE, BUTTON_SIZE, Text.literal("Interact"), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        const val BUTTON_SIZE = 69
        const val TEXTURE_HEIGHT = BUTTON_SIZE * 2
        const val ICON_SIZE = 32
        const val ICON_SCALE = 0.5f
        const val ICON_OFFSET = 26.5
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices
        blitk(
            matrixStack = matrices,
            texture = buttonResource,
            x = x,
            y = y,
            width = BUTTON_SIZE,
            height = BUTTON_SIZE,
            vOffset = if (isHovered(mouseX.toFloat(), mouseY.toFloat()) && isEnabled) BUTTON_SIZE else 0,
            textureHeight = TEXTURE_HEIGHT,
            alpha = if (isEnabled) 1f else 0.4f
        )

        if (iconResource != null) {
            val (iconX, iconY) = getIconPosition()
            val colour = this.colour() ?: Vector3f(1F, 1F, 1F)
            blitk(
                matrixStack = matrices,
                texture = iconResource,
                x = iconX,
                y = iconY,
                width = ICON_SIZE,
                height = ICON_SIZE,
                alpha = if (isEnabled) 1f else 0.4f,
                red = colour.x,
                green = colour.y,
                blue = colour.z,
                scale = ICON_SCALE
            )
        }
    }

    private fun getIconPosition(): Pair<Number, Number> {
        return Pair(
            (x + ICON_OFFSET) / ICON_SCALE,
            (y + ICON_OFFSET) / ICON_SCALE
        )
    }

    override fun playDownSound(soundManager: SoundManager?) {}

    private fun isHovered(mouseX: Float, mouseY: Float): Boolean {
        val xMin = x.toFloat()
        val xMax = xMin + BUTTON_SIZE
        val yMin = y.toFloat()
        val yMax = yMin + BUTTON_SIZE
        return mouseX in xMin..xMax && mouseY in yMin..yMax
    }

}