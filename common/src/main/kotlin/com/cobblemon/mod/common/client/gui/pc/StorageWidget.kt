/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pc

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.storage.StorePosition
import com.cobblemon.mod.common.api.storage.party.PartyPosition
import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.settings.ServerSettings
import com.cobblemon.mod.common.client.storage.ClientPC
import com.cobblemon.mod.common.client.storage.ClientParty
import com.cobblemon.mod.common.net.messages.server.storage.SwapPCPartyPokemonPacket
import com.cobblemon.mod.common.net.messages.server.storage.party.MovePartyPokemonPacket
import com.cobblemon.mod.common.net.messages.server.storage.party.ReleasePartyPokemonPacket
import com.cobblemon.mod.common.net.messages.server.storage.party.SwapPartyPokemonPacket
import com.cobblemon.mod.common.net.messages.server.storage.pc.MovePCPokemonPacket
import com.cobblemon.mod.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cobblemon.mod.common.net.messages.server.storage.pc.MovePartyPokemonToPCPacket
import com.cobblemon.mod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket
import com.cobblemon.mod.common.net.messages.server.storage.pc.SwapPCPokemonPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text

class StorageWidget(
    pX: Int, pY: Int,
    val pcGui: PCGUI,
    private val pc: ClientPC,
    private val party: ClientParty
) : SoundlessWidget(pX, pY, WIDTH, HEIGHT, Text.literal("PCWidget")) {

    companion object {
        const val WIDTH = 263
        const val HEIGHT = 155
        const val BOX_SLOT_START_OFFSET_X = 7
        const val BOX_SLOT_START_OFFSET_Y = 11
        const val PARTY_SLOT_START_OFFSET_X = 193
        const val PARTY_SLOT_START_OFFSET_Y = 8
        const val SLOT_SIZE = 25
        const val BOX_SLOT_PADDING = 2
        const val PARTY_SLOT_PADDING = 6
        const val GRAB_DEBOUNCE = 10

        private val screenOverlayResource = cobblemonResource("ui/pc/pc_screen_overlay.png")
    }

    private val partySlots = arrayListOf<PartyStorageSlot>()
    private val boxSlots = arrayListOf<BoxStorageSlot>()
    private val releaseButton: ReleaseButton
    private val releaseYesButton: ReleaseConfirmButton
    private val releaseNoButton: ReleaseConfirmButton

    var displayConfirmRelease = false
    var screenLoaded = false
    var pokemonClicked = false
    var mouseDownCount = 0
    var selectedPosition: StorePosition? = null
    var grabbedSlot: GrabbedStorageSlot? = null

    var box = 0
        set(value) {
            // If value is within min and max
            field = if (value > 0 && value < pc.boxes.size) value
            // If value is less than zero, wrap around to end
            else if (value < 0) pc.boxes.size - 1
            // Else it's greater than max, wrap around to start
            else 0
            this.setupStorageSlots()
        }

    init {
        this.setupStorageSlots()

        this.releaseButton = ReleaseButton(
            x = x + 194,
            y = y + 124,
            parent = this,
            onPress = {
                if (!displayConfirmRelease) {
                    displayConfirmRelease = true
                    playSound(CobblemonSounds.GUI_CLICK.get())
                }
            }
        )

        this.releaseYesButton = ReleaseConfirmButton(
            x = x + 190,
            y = y + 131,
            parent = this,
            subKey = "ui.generic.yes",
            onPress = {
                if (canDeleteSelected() && displayConfirmRelease) {
                    val position = selectedPosition ?: return@ReleaseConfirmButton
                    val pokemon = getSelectedPokemon() ?: return@ReleaseConfirmButton

                    val packet = when (position) {
                        is PartyPosition -> ReleasePartyPokemonPacket(pokemon.uuid, position)
                        is PCPosition -> ReleasePCPokemonPacket(pokemon.uuid, position)
                        else -> return@ReleaseConfirmButton
                    }

                    CobblemonNetwork.sendToServer(packet)
                    playSound(CobblemonSounds.PC_RELEASE.get())
                    resetSelected()
                    displayConfirmRelease = false
                }
            }
        )

        this.releaseNoButton = ReleaseConfirmButton(
            x = x + 226,
            y = y + 131,
            parent = this,
            subKey = "ui.generic.no",
            onPress = {
                if (displayConfirmRelease) {
                    displayConfirmRelease = false
                    playSound(CobblemonSounds.GUI_CLICK.get())
                }
            }
        )
    }

    fun canDeleteSelected(): Boolean {
        return !(selectedPosition is PartyPosition && party.filterNotNull().size <= 1)
                && selectedPosition != null
                && grabbedSlot != null
    }

    private fun setupStorageSlots() {
        this.resetStorageSlots()
        var index = 0

        // Box Slots
        val boxStartX = x + BOX_SLOT_START_OFFSET_X
        val boxStartY = y + BOX_SLOT_START_OFFSET_Y

        for (row in 1..5) {
            for (col in 1..6) {
                BoxStorageSlot(
                    x = boxStartX + ((col - 1) * (SLOT_SIZE + BOX_SLOT_PADDING)),
                    y = boxStartY + ((row - 1) * (SLOT_SIZE + BOX_SLOT_PADDING)),
                    parent = this,
                    pc = pc,
                    position = PCPosition(box, index),
                    onPress = { this.onStorageSlotClicked(it) }
                ).also {  widget ->
                    this.addWidget(widget)
                    this.boxSlots.add(widget)
                }
                index++
            }
        }

        // Party Slots
        for (partyIndex in 0..5) {
            var partyX = x + PARTY_SLOT_START_OFFSET_X
            var partyY = y + PARTY_SLOT_START_OFFSET_Y

            if (partyIndex > 0) {
                val isEven = partyIndex % 2 == 0
                val offsetIndex = (partyIndex - (if (isEven) 0 else 1)) / 2
                val offsetX = if (isEven) 0 else (SLOT_SIZE + PARTY_SLOT_PADDING)
                val offsetY = if (isEven) 0 else 8

                partyX += offsetX
                partyY += ((SLOT_SIZE + PARTY_SLOT_PADDING) * offsetIndex) + offsetY
            }

            PartyStorageSlot(
                x = partyX,
                y = partyY,
                parent = this,
                party = party,
                position = PartyPosition(partyIndex),
                onPress = { this.onStorageSlotClicked(it) }
            ).also { widget ->
                this.addWidget(widget)
                this.partySlots.add(widget)
            }
        }
    }

    private fun getSelectedPokemon(): Pokemon? {
        val selectedPosition = this.selectedPosition ?: return null
        return when (selectedPosition) {
            is PCPosition -> pc.get(selectedPosition)
            is PartyPosition -> party.get(selectedPosition)
            else -> null
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        // Party  Label
        drawScaledText(
            matrixStack = matrices,
            font = CobblemonResources.DEFAULT_LARGE,
            text = lang("ui.party").bold(),
            x = x + 213,
            y = y - 15.5,
            centered = true,
            shadow = true
        )

        if (canDeleteSelected() && displayConfirmRelease) {
            drawScaledText(
                matrixStack = matrices,
                font = CobblemonResources.DEFAULT_LARGE,
                text = lang("ui.pc.release").bold(),
                x = x + 223,
                y = y + 119,
                centered = true
            )
        }

        this.releaseButton.render(matrices, mouseX, mouseY, delta)
        this.releaseYesButton.render(matrices, mouseX, mouseY, delta)
        this.releaseNoButton.render(matrices, mouseX, mouseY, delta)
        
        // Screen Overlay
        blitk(
            matrixStack = matrices,
            texture = screenOverlayResource,
            x = x - 17,
            y = y - 17,
            width = 208,
            height = 189,
            alpha = if (screenLoaded) 1F else ((pcGui.ticksElapsed).toFloat() / 10F).coerceIn(0F, 1F)
        )

        if (screenLoaded) {
            this.boxSlots.forEach { widget -> widget.render(matrices, mouseX, mouseY, delta) }
        } else {
            if (pcGui.ticksElapsed >= 10)  screenLoaded = true
        }

        this.partySlots.forEach { widget -> widget.render(matrices, mouseX, mouseY, delta) }

        if (pokemonClicked && grabbedSlot == null) {
            mouseDownCount++
            if (mouseDownCount >= GRAB_DEBOUNCE) {
                val selectedPokemon = pcGui.selectedPokemon
                if (selectedPokemon != null) {
                    grabbedSlot = GrabbedStorageSlot(
                        x = mouseX - (SLOT_SIZE / 2),
                        y = mouseY - (SLOT_SIZE / 2),
                        parent = this,
                        pokemon = selectedPokemon
                    )
                    playSound(CobblemonSounds.PC_GRAB.get())
                }
                mouseDownCount = 0
            }
        }
        grabbedSlot?.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (displayConfirmRelease) {
            if (releaseYesButton.isHovered(pMouseX, pMouseY)) releaseYesButton.mouseClicked(pMouseX, pMouseY, pButton)
            if (releaseNoButton.isHovered(pMouseX, pMouseY)) releaseNoButton.mouseClicked(pMouseX, pMouseY, pButton)
        } else {
            if (releaseButton.isHovered(pMouseX, pMouseY)) releaseButton.mouseClicked(pMouseX, pMouseY, pButton)
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }

    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        pokemonClicked = false
        mouseDownCount = 0
        return super.mouseReleased(pMouseX, pMouseY, pButton)
    }

    private fun resetStorageSlots() {
        this.partySlots.forEach(this::removeWidget)
        this.partySlots.clear()

        this.boxSlots.forEach(this::removeWidget)
        this.boxSlots.clear()
    }

    private fun playSound(soundEvent: SoundEvent) {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0F))
    }

    private fun onStorageSlotClicked(button: ButtonWidget) {

        // Check if storage slot
        val clickedPosition = when(button) {
            is BoxStorageSlot -> button.position
            is PartyStorageSlot -> button.position
            else -> return
        }

        // Reset release confirmation
        displayConfirmRelease = false

        // Clicking on itself, so unselect position
        if (this.selectedPosition != null && this.selectedPosition == clickedPosition) {
            if (grabbedSlot != null) playSound(CobblemonSounds.PC_DROP.get())
            resetSelected()
            return
        }

        // Check for selecting a Pokémon
        val clickedPokemon = when(button) {
            is BoxStorageSlot -> pc.get(clickedPosition as PCPosition)
            is PartyStorageSlot -> party.get(clickedPosition as PartyPosition)
            else -> null
        }

        if (grabbedSlot == null) {
            if (clickedPokemon != null) {
                pokemonClicked = true
                this.selectedPosition = clickedPosition
                this.pcGui.setSelectedPokemon(clickedPokemon)
            }
        } else  {
            // Handle movement within the PC
            val selectedPokemon = when(this.selectedPosition) {
                is PCPosition -> pc.get(this.selectedPosition as PCPosition)
                is PartyPosition -> party.get(this.selectedPosition as PartyPosition)
                else -> null
            } ?: return

            // PC -> PC
            if (this.selectedPosition is PCPosition && clickedPosition is PCPosition) {
                val packet = clickedPokemon?.let { SwapPCPokemonPacket(it.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PCPosition) }
                    ?: MovePCPokemonPacket(selectedPokemon.uuid, selectedPosition as PCPosition, clickedPosition)
                packet.sendToServer()
                playSound(CobblemonSounds.PC_DROP.get())
                resetSelected()
            }
            // PC -> Party
            else if (this.selectedPosition is PCPosition && clickedPosition is PartyPosition) {
                val packet = clickedPokemon?.let { SwapPCPartyPokemonPacket(clickedPokemon.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PCPosition) }
                    ?: MovePCPokemonToPartyPacket(selectedPokemon.uuid, this.selectedPosition as PCPosition, clickedPosition)
                packet.sendToServer()
                playSound(CobblemonSounds.PC_DROP.get())
                resetSelected()
            }
            // Party -> PC
            else if (this.selectedPosition is PartyPosition && clickedPosition is PCPosition) {
                if (ServerSettings.preventCompletePartyDeposit && this.party.filterNotNull().size == 1 && clickedPokemon == null) {
                    return
                }
                val packet = clickedPokemon?.let { SwapPCPartyPokemonPacket(selectedPokemon.uuid, this.selectedPosition as PartyPosition, clickedPokemon.uuid, clickedPosition) }
                    ?: MovePartyPokemonToPCPacket(selectedPokemon.uuid, this.selectedPosition as PartyPosition, clickedPosition)
                packet.sendToServer()
                playSound(CobblemonSounds.PC_DROP.get())
                resetSelected()
            }
            // Party -> Party
            else if (this.selectedPosition is PartyPosition && clickedPosition is PartyPosition) {
                val packet = clickedPokemon?.let { SwapPartyPokemonPacket(it.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PartyPosition) }
                    ?: MovePartyPokemonPacket(selectedPokemon.uuid, selectedPosition as PartyPosition, clickedPosition)
                packet.sendToServer()
                playSound(CobblemonSounds.PC_DROP.get())
                resetSelected()
            }
        }
    }

    private fun resetSelected() {
        selectedPosition = null
        grabbedSlot = null
        pcGui.setSelectedPokemon(null)
    }
}