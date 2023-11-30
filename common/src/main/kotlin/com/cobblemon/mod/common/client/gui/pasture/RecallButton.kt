/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pasture

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text

class RecallButton(
    x: Int, y: Int,
    onPress: PressAction
) : ButtonWidget(x, y, WIDTH, HEIGHT, Text.literal("Retrieve"), onPress, DEFAULT_NARRATION_SUPPLIER) {

    companion object {
        private const val WIDTH = 70
        private const val HEIGHT = 17

        private val buttonResource = cobblemonResource("textures/gui/pasture/pasture_button.png")
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = context.matrices,
            texture = buttonResource,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT,
            vOffset = if (isHovered(mouseX.toDouble(), mouseY.toDouble())) HEIGHT else 0,
            textureHeight = HEIGHT * 2
        )

        // Render Text
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = lang("ui.pasture.recall_all").bold(),
            x = x + (WIDTH / 2),
            y = y + 4,
            centered = true,
            shadow = true
        )
    }

    override fun playDownSound(pHandler: SoundManager) {
    }

    fun isHovered(mouseX: Double, mouseY: Double) = mouseX.toFloat() in (x.toFloat()..(x.toFloat() + WIDTH)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + HEIGHT))
}