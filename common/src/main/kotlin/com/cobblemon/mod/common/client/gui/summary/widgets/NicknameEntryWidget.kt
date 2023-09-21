/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary.widgets

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.net.messages.server.pokemon.update.SetNicknamePacket
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text

class NicknameEntryWidget(
    var pokemon: Pokemon, x: Int, y: Int, width: Int, height: Int, val isParty: Boolean, text: Text
): TextFieldWidget(
    MinecraftClient.getInstance().textRenderer,
    x, y, width, height, text
) {
    companion object {
        private const val MAX_NAME_LENGTH = 12
    }

    var lastSavedName: String? = null
    var pokemonName = ""

    init {
        setMaxLength(MAX_NAME_LENGTH)
        setSelectedPokemon(pokemon)
    }

    fun setSelectedPokemon(pokemon: Pokemon) {
        if (isFocused) {
            isFocused = false
        }
        this.pokemon = pokemon
        pokemonName = I18n.translate(pokemon.species.translatedName.string)
        this.lastSavedName = pokemon.nickname?.string
        setChangedListener {
            if (it != pokemonName && it.isNotBlank()) {
                pokemon.nickname = Text.literal(it)
            } else {
                pokemon.nickname = null
            }
        }
        text = pokemon.getDisplayName().string
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (mouseX.toInt() in x..(x + width) && mouseY.toInt() in y..(y + height)) {
            isFocused = true
            return true
        } else {
            return false
        }
    }

    override fun setFocused(focused: Boolean) {
        super.setFocused(focused)
        val oldText = text.trim()
        val pokemonName = I18n.translate(pokemon.species.translatedName.string)
        text = text.trim().ifBlank { pokemonName }
        if (!focused) {
            val newNickname = if (text == pokemonName) null else text
            updateNickname(oldText, newNickname)
        }
    }

    private fun updateNickname(oldText: String, newNickname: String?) {
        if (!(newNickname == null && pokemon.nickname == null)) {

            val pokemonName = I18n.translate(pokemon.species.translatedName.string)
            val name = if (pokemonName == newNickname) {
                null
            } else {
                newNickname
            }

            if (name == lastSavedName) {
                return
            }

            println("Setting ${pokemon.species.name} nickname to: $name")
            lastSavedName = name
            CobblemonNetwork.sendToServer(
                SetNicknamePacket(
                    pokemonUUID = pokemon.uuid,
                    nickname = name,
                    isParty = isParty
                )
            )
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (cursor != text.length) setCursorToEnd(Screen.hasShiftDown())

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = Text.translatable(if (isFocused) "$text|" else text).bold(),
            x = x,
            y = y,
            shadow = true
        )
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == InputUtil.GLFW_KEY_ESCAPE) {
            val oldText = text.trim()
            val pokemonName = I18n.translate(pokemon.species.translatedName.string)
            val newNickname = if (text == pokemonName) null else text
            updateNickname(oldText, newNickname)
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}
