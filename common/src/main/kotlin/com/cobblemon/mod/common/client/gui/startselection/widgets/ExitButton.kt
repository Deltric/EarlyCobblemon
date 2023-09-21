/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.startselection.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.NarrationSupplier
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text

class ExitButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    text: Text? = null,
    onPress: PressAction
): ButtonWidget(pX, pY, pWidth, pHeight, text ?: "".text(), onPress, NarrationSupplier { "".text() }) {

    companion object {
        private const val EXIT_BUTTON_WIDTH = 15.95F
        private const val EXIT_BUTTON_HEIGHT = 11.95F
        private val exitButtonResource = cobblemonResource("textures/gui/starterselection/starterselection_exit.png")
    }

    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
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