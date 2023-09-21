/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.battle.ClientBattleMessageQueue
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.OrderedText

/**
 * Pane for seeing and interacting with battle messages.
 *
 * @author Hiroku
 * @since June 24th, 2022
 */
class BattleMessagePane(
    messageQueue: ClientBattleMessageQueue
): AlwaysSelectedEntryListWidget<BattleMessagePane.BattleMessageLine>(
    MinecraftClient.getInstance(),
    TEXT_BOX_WIDTH, // width
    TEXT_BOX_HEIGHT, // height
    1, // top
    1 + TEXT_BOX_HEIGHT, // bottom
    LINE_HEIGHT
) {
    var opacity = 1F
    private var scrolling = false

    val appropriateX: Int
        get() = client.window.scaledWidth - (FRAME_WIDTH + 12)
    val appropriateY: Int
        get() = client.window.scaledHeight - (30 + (if (expanded) FRAME_EXPANDED_HEIGHT else FRAME_HEIGHT))

    init {
        correctSize()
        setRenderBackground(false)
        messageQueue.subscribe {
            val fullyScrolledDown = maxScroll - scrollAmount < 10
            addEntry(BattleMessageLine(this, it))
            if (fullyScrolledDown) {
                scrollAmount = maxScroll.toDouble()
            }
        }
    }

    private fun correctSize() {
        val textBoxHeight = if (expanded) TEXT_BOX_HEIGHT * 2 else TEXT_BOX_HEIGHT
        updateSize(TEXT_BOX_WIDTH, textBoxHeight, appropriateY + 6, appropriateY + 6 + textBoxHeight)
        setLeftPos(appropriateX)
    }

    companion object {
        const val LINE_HEIGHT = 10
        const val LINE_WIDTH = 142
        const val FRAME_WIDTH = 169
        const val FRAME_HEIGHT = 55
        const val FRAME_EXPANDED_HEIGHT = 101
        const val TEXT_BOX_WIDTH = 153
        const val TEXT_BOX_HEIGHT = 46
        const val EXPAND_TOGGLE_SIZE = 5

        private val battleMessagePaneFrameResource = cobblemonResource("textures/gui/battle/battle_log.png")
        private val battleMessagePaneFrameExpandedResource = cobblemonResource("textures/gui/battle/battle_log_expanded.png")
        private var expanded = false
    }

    override fun addEntry(entry: BattleMessageLine): Int {
        return super.addEntry(entry)
    }

    override fun getRowWidth(): Int {
        return 80
    }

    override fun getScrollbarPositionX(): Int {
        return left + 154
    }

    override fun getScrollAmount(): Double {
        return super.getScrollAmount()
    }

    private fun scaleIt(i: Number): Int {
        return (client.window.scaleFactor * i.toFloat()).toInt()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()
        blitk(
            matrixStack = context.matrices,
            texture = if (expanded) battleMessagePaneFrameExpandedResource else battleMessagePaneFrameResource,
            x = left,
            y = appropriateY,
            height = if (expanded) FRAME_EXPANDED_HEIGHT else FRAME_HEIGHT,
            width = FRAME_WIDTH,
            alpha = opacity
        )

        val textBoxHeight = if (expanded) TEXT_BOX_HEIGHT * 2 else TEXT_BOX_HEIGHT
        context.enableScissor(
            left + 5,
            appropriateY + 6,
            left + 5 + width,
            appropriateY + 6 + textBoxHeight
        )
        super.render(context, mouseX, mouseY, partialTicks)
        context.disableScissor()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val toggleOffsetY = if (expanded) 92 else 46
        if (mouseX > (left + 160) && mouseX < (left + 160 + EXPAND_TOGGLE_SIZE) && mouseY > (appropriateY + toggleOffsetY) && mouseY < (appropriateY + toggleOffsetY + EXPAND_TOGGLE_SIZE)) {
            expanded = !expanded
        }

        updateScrollingState(mouseX, mouseY)
        if (scrolling) {
            focused = getEntryAtPosition(mouseX, mouseY)
            isDragging = true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scrolling) {
            if (mouseY < top) {
                scrollAmount = 0.0
            } else if (mouseY > bottom) {
                scrollAmount = maxScroll.toDouble()
            } else {
                scrollAmount += deltaY
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollingState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= this.scrollbarPositionX.toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= top
                && mouseY < bottom
    }

    class BattleMessageLine(val pane: BattleMessagePane, val line: OrderedText) : Entry<BattleMessageLine>() {
        override fun getNarration() = "".text()
        override fun render(
            context: DrawContext,
            index: Int,
            rowTop: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            partialTicks: Float
        ) {
            drawScaledText(
                context,
                line,
                rowLeft - 29,
                rowTop - 2,
                opacity = pane.opacity
            )
        }
    }
}