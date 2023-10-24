/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.partyselect

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.callback.PartySelectPokemonDTO
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.net.messages.server.callback.party.PartyPokemonSelectedPacket
import com.cobblemon.mod.common.net.messages.server.callback.party.PartySelectCancelledPacket
import com.cobblemon.mod.common.util.cobblemonResource
import java.util.UUID
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class PartySelectConfiguration(
    val title: MutableText,
    val pokemon: List<PartySelectPokemonDTO>,
    val onCancel: (PartySelectGUI) -> Unit,
    val onBack: (PartySelectGUI) -> Unit,
    val onSelect: (PartySelectGUI, PartySelectPokemonDTO) -> Unit,
)
class PartySelectGUI(
    val config: PartySelectConfiguration
) : Screen(Text.translatable("cobblemon.ui.interact.moveselect")) {
    companion object {
        const val WIDTH = 163
        const val HEIGHT = 132
        const val SCALE = 0.5F

        private val baseBackgroundResource = cobblemonResource("textures/gui/interact/party_select.png")
        private val spacerResource = cobblemonResource("textures/gui/interact/party_select_spacer.png")
    }

    var closed = false

    constructor(
        title: MutableText,
        pokemon: List<PartySelectPokemonDTO>,
        uuid: UUID
    ): this(
        PartySelectConfiguration(
            title = title,
            pokemon = pokemon,
            onSelect = { gui, it ->
                CobblemonNetwork.sendToServer(PartyPokemonSelectedPacket(uuid = uuid, pokemon.indexOf(it)))
                gui.closeProperly()
            },
            onCancel = { CobblemonNetwork.sendToServer(PartySelectCancelledPacket(uuid = uuid)) },
            onBack = PartySelectGUI::close
        )
    )

    fun closeProperly() {
        closed = true
        close()
    }

    override fun init() {
        val x = (width - WIDTH) / 2
        val y = (height - HEIGHT) / 2

        config.pokemon.forEachIndexed { index, pokemon ->
            var slotX = x + 11
            var slotY = y + 23

            if (index > 0) {
                val isEven = index % 2 == 0
                val offsetIndex = (index - (if (isEven) 0 else 1)) / 2
                val offsetX = if (isEven) 0 else 74
                val offsetY = if (isEven) 0 else -8

                slotX += offsetX
                slotY += (31 * offsetIndex) + offsetY
            }

            addDrawableChild(
                PartySlotButton(
                    parent = this,
                    x = slotX,
                    y = slotY,
                    pokemon = pokemon.pokemonProperties,
                    aspects = pokemon.aspects,
                    heldItem = pokemon.heldItem,
                    currentHealth = pokemon.currentHealth,
                    maxHealth = pokemon.maxHealth,
                    enabled = pokemon.enabled
                ) { onPress(pokemon) }
            )
        }

        // Add Exit Button
        addDrawableChild(
            ExitButton(
                pX = x + 134,
                pY = y + 116
            ) {
                playSound(CobblemonSounds.GUI_CLICK)
                config.onBack(this)
            }
        )

        super.init()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val x = (width - WIDTH) / 2
        val y = (height - HEIGHT) / 2

        blitk(
            matrixStack = context.matrices,
            texture = baseBackgroundResource,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT
        )

        // Label
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = config.title.bold(),
            x = x + 37,
            y = y + 1.5,
            centered = true
        )

        blitk(
            matrixStack = context.matrices,
            texture = spacerResource,
            x = (x + 86.5) / SCALE,
            y = (y + 111) / SCALE,
            width = 79,
            height = 12,
            scale = SCALE
        )

        // Render all added Widgets
        super.render(context, mouseX, mouseY, partialTicks)
    }

    private fun onPress(pokemon: PartySelectPokemonDTO) {
        if (!pokemon.enabled) {
            return
        }
        playSound(CobblemonSounds.GUI_CLICK)
        config.onSelect(this, pokemon)
    }

    override fun close() {
        if (!closed) {
            config.onCancel(this)
        }
        super.close()
    }

    override fun shouldCloseOnEsc() = true
    override fun shouldPause() = false

    fun playSound(soundEvent: SoundEvent) {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0F))
    }
}