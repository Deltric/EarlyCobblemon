/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary.widgets.pages.moves

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.summary.Summary
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.pages.moves.change.MoveSwitchPane
import com.cobblemon.mod.common.net.messages.server.RequestMoveSwapPacket
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
class MovesWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val summary: Summary
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("MovesWidget")) {
    companion object {
        private const val MOVE_WIDTH = 120
        private const val MOVE_HEIGHT = 30
        private val movesBaseResource = cobblemonResource("ui/summary/summary_moves.png")
    }

    var moveSwitchPane: MoveSwitchPane? = null
        set(value) {
            if (field != null) {
                this.removeWidget(field!!)
            }
            field = value
            if (value != null) {
                this.addWidget(value)
                summary.sidePanel = null
            }
        }

    init {
        ModelWidget.render = true
    }

    private var index = -1
    private val moves = summary.currentPokemon.moveSet.getMoves().map { move ->
        index++
        MoveWidget(x + 20, y + 26 + (MOVE_HEIGHT + 3) * index, MOVE_WIDTH, MOVE_HEIGHT, move, x + 6, y + 164, this, index)
    }.toMutableList().onEach {
        addWidget(it)
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        // Rendering Moves Texture
        RenderSystem.setShaderTexture(0, movesBaseResource)
        RenderSystem.enableDepthTest()
        drawTexture(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
        moves.forEach {
            it.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
        }
        moveSwitchPane?.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    private fun updateMoves() {
        moves.forEach {
            it.y = y + 27 + (MOVE_HEIGHT + 3) * moves.indexOf(it)
            it.update()
        }
    }

    fun closeSwitchMoveMenu() {
        moveSwitchPane = null
        ModelWidget.render = true
    }

    fun moveMove(move: MoveWidget, up: Boolean) {
        val movePos = moves.indexOf(move)
        if (moves.size <= movePos || movePos == -1) {
            return
        }
        var targetSlot: Int
        if (up) {
            targetSlot = movePos - 1
            if (targetSlot == -1)
                targetSlot = moves.size - 1
        } else {
            targetSlot = movePos + 1
            if (targetSlot >= moves.size)
                targetSlot = 0
        }
//        moves[targetSlot] = moves[movePos].also {
//            moves[movePos] = moves[targetSlot]
//        }
        //summary.currentPokemon.moveSet.swapMove(targetSlot, movePos)
        CobblemonNetwork.sendToServer(
            RequestMoveSwapPacket(
                move1 = movePos,
                move2 = targetSlot,
                slot = CobblemonClient.storage.myParty.getPosition(summary.currentPokemon.uuid)
            )
        )
        //updateMoves()
    }

}