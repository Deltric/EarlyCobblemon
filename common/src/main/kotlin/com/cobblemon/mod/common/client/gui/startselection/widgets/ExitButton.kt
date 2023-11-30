/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.startselection.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class ExitButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    onPress: PressAction
): ButtonWidget(pX, pY, pWidth, pHeight, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        private const val EXIT_BUTTON_WIDTH = 15.95F
        private const val EXIT_BUTTON_HEIGHT = 11.95F
        private val exitButtonResource = cobblemonResource("textures/gui/starterselection/starterselection_exit.png")
    }

    override fun renderWidget(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height
        if (isHovered) {
            blitk(
                matrixStack = context.matrices,
                x = x + 0.075f, y = y + 1.05f,
                texture = exitButtonResource,
                width = EXIT_BUTTON_WIDTH, height = EXIT_BUTTON_HEIGHT
            )
        }
    }

    override fun playDownSound(soundManager: SoundManager) { }

}