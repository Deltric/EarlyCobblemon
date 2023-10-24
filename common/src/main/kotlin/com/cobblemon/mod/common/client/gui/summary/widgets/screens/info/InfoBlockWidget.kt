/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary.widgets.screens.info

import com.cobblemon.mod.common.api.text.*
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class InfoBlockWidget(
    pX: Int,
    pY: Int,
    blockWidth: Int,
    blockHeight: Int,
    private val text: MutableText,
    private val withinRowVerticalTextOffset: Int,
    private val font: Identifier,
) : SoundlessWidget(pX, pY, blockWidth, blockHeight, Text.literal("InfoBlockWidget")) {


    override fun renderButton(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {

        drawScaledText(
            context = context,
            font = font,
            text = text.bold(),
            x = x,
            y = y + withinRowVerticalTextOffset,
            shadow = true,
            pMouseX = pMouseX,
            pMouseY = pMouseY
        )
    }

}
