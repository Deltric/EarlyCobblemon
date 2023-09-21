/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokenav

import com.cobblemon.mod.common.api.gui.ColourLibrary
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.NarrationSupplier
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

open class PokeNavImageButton(
    val posX: Int, val posY: Int,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val resourceLocation: Identifier,
    onPress: PressAction,
    private val text: MutableText,
    private val canClick: () -> Boolean = { true }
): ButtonWidget(pX, pY, pWidth, pHeight, text, onPress, NarrationSupplier { "".text() }) {

    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Render Button Image
        this.applyBlitk(context.matrices, pMouseX, pMouseY, pPartialTicks)
        // Draw Text
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = text.bold(),
            x = x + width / 2, y = y + height + 3,
            colour = ColourLibrary.WHITE, shadow = false,
            centered = true
        )
    }

    fun canClick() = this.canClick.invoke()

    override fun onPress() {
        if (this.canClick()) {
            super.onPress()
        }
    }

    override fun playDownSound(soundManager: SoundManager) {
        // Be silent if the button isn't clickable
        if (this.canClick()) {
            super.playDownSound(soundManager)
        }
    }
    
    protected open fun applyBlitk(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = pMatrixStack,
            texture = resourceLocation,
            x = x, y = y + 0.25,
            width = width, height = height
        )
    }
    
}