package com.cablemc.pokemoncobbled.common.client.gui.battle

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawPortraitPokemon
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon
import com.cablemc.pokemoncobbled.common.client.battle.ClientBallDisplay
import com.cablemc.pokemoncobbled.common.client.keybind.currentKey
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PartySendBinding
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.client.render.getDepletableRedGreen
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.util.battleLang
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.lang
import com.mojang.blaze3d.systems.RenderSystem
import java.lang.Double.max
import java.lang.Double.min
import kotlin.math.roundToInt
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.util.math.Vec3f

class BattleOverlay : InGameHud(MinecraftClient.getInstance(), MinecraftClient.getInstance().itemRenderer) {
    companion object {
        const val MAX_OPACITY = 1.0
        const val MIN_OPACITY = 0.5
        const val OPACITY_CHANGE_PER_SECOND = 0.1
        const val HORIZONTAL_INSET = 20
        const val VERTICAL_INSET = 10
        const val HORIZONTAL_SPACING = 15
        const val VERTICAL_SPACING = 40
        const val INFO_OFFSET_X = 5

        const val TILE_WIDTH_TO_HEIGHT = 1 / 3.1764705F
        const val TILE_WIDTH = 120
        const val TILE_HEIGHT = TILE_WIDTH * TILE_WIDTH_TO_HEIGHT
        const val PORTRAIT_DIAMETER = 116 / 432F * TILE_WIDTH
        const val PORTRAIT_OFFSET = 10 / 432F * TILE_WIDTH

        private val PROMPT_TEXT_OPACITY_CURVE = sineFunction(period = 4F, verticalShift = 0.5F, amplitude = 0.5F)

        val battleInfoBase = cobbledResource("ui/battle/battle_info_base.png")
        val battleInfoBaseFlipped = cobbledResource("ui/battle/battle_info_base_flipped.png")
        val battleInfoUnderlay = cobbledResource("ui/battle/battle_info_underlay.png")
    }

    var opacity = MIN_OPACITY
    val opacityRatio: Double
        get() = (opacity - MIN_OPACITY) / (MAX_OPACITY - MIN_OPACITY)
    var passedSeconds = 0F

    override fun render(matrices: MatrixStack, tickDelta: Float) {
        passedSeconds += tickDelta / 20
        if (passedSeconds > 100) {
            passedSeconds -= 100
        }
        val battle = PokemonCobbledClient.battle ?: return
        opacity = if (battle.minimised) {
            max(opacity - tickDelta * OPACITY_CHANGE_PER_SECOND, MIN_OPACITY)
        } else {
            min(opacity + tickDelta * OPACITY_CHANGE_PER_SECOND, MAX_OPACITY)
        }

        val playerUUID = MinecraftClient.getInstance().player?.uuid ?: return
        val side1 = if (battle.side1.actors.any { it.uuid == playerUUID }) battle.side1 else battle.side2
        val side2 = if (side1 == battle.side1) battle.side2 else battle.side1

        side1.activeClientBattlePokemon.forEachIndexed { index, activeClientBattlePokemon -> drawTile(matrices, tickDelta, activeClientBattlePokemon, true, index) }
        side2.activeClientBattlePokemon.forEachIndexed { index, activeClientBattlePokemon -> drawTile(matrices, tickDelta, activeClientBattlePokemon, false, index) }

        if (MinecraftClient.getInstance().currentScreen !is BattleGUI && battle.mustChoose) {
            val textOpacity = PROMPT_TEXT_OPACITY_CURVE(passedSeconds)
            drawScaledText(
                matrixStack = matrices,
                text = battleLang("ui.actions_label", PartySendBinding.currentKey().localizedText),
                x = MinecraftClient.getInstance().window.scaledWidth / 2,
                y = 40,
                opacity = textOpacity,
                centered = true
            )
        }
    }


    fun drawTile(matrices: MatrixStack, tickDelta: Float, activeBattlePokemon: ActiveClientBattlePokemon, left: Boolean, rank: Int) {
        val mc = MinecraftClient.getInstance()

        val battlePokemon = activeBattlePokemon.battlePokemon ?: return
        // First render the underlay
        var x = HORIZONTAL_INSET + rank * HORIZONTAL_SPACING.toFloat()
        val y = VERTICAL_INSET + rank * VERTICAL_SPACING
        if (!left) {
            x = mc.window.scaledWidth - x - TILE_WIDTH
        }
        val invisibleX = if (left) {
            -TILE_WIDTH - 1F
        } else {
            mc.window.scaledWidth.toFloat()
        }

        activeBattlePokemon.invisibleX = invisibleX
        activeBattlePokemon.xDisplacement = x
        activeBattlePokemon.animate(tickDelta)
        x = activeBattlePokemon.xDisplacement

        val hue = activeBattlePokemon.getHue()
        val r = ((hue shr 16) and 0b11111111) / 255F
        val g = ((hue shr 8) and 0b11111111) / 255F
        val b = (hue and 0b11111111) / 255F

        drawBattleTile(
            matrices = matrices,
            x = x,
            y = y.toFloat(),
            reversed = !left,
            species = battlePokemon.species,
            level = battlePokemon.level,
            aspects = battlePokemon.properties.aspects,
            displayName = battlePokemon.displayName,
            hpRatio = battlePokemon.hpRatio,
            state = battlePokemon.state,
            colour = Triple(r, g, b),
            opacity = opacity.toFloat(),
            ballState = activeBattlePokemon.ballCapturing
        )
    }

    fun drawBattleTile(
        matrices: MatrixStack,
        x: Float,
        y: Float,
        reversed: Boolean,
        species: Species,
        level: Int,
        aspects: Set<String>,
        displayName: MutableText,
        hpRatio: Float,
        state: PoseableEntityState<PokemonEntity>?,
        colour: Triple<Float, Float, Float>?,
        opacity: Float,
        ballState: ClientBallDisplay? = null
    ) {
        val mc = MinecraftClient.getInstance()
        fun scaleIt(i: Number): Int {
            return (mc.window.scaleFactor * i.toFloat()).roundToInt()
        }

        val portraitStartX = x + if (!reversed) PORTRAIT_OFFSET else { TILE_WIDTH - PORTRAIT_DIAMETER - PORTRAIT_OFFSET }
        blitk(
            matrixStack = matrices,
            texture = battleInfoUnderlay,
            y = y + PORTRAIT_OFFSET,
            x = portraitStartX,
            height = PORTRAIT_DIAMETER,
            width = PORTRAIT_DIAMETER,
            alpha = opacity
        )

        // Second render the Pokémon through the scissors
        RenderSystem.enableScissor(
            scaleIt(portraitStartX),
            mc.window.height - scaleIt(y + PORTRAIT_DIAMETER + PORTRAIT_OFFSET),
            scaleIt(PORTRAIT_DIAMETER.toInt()),
            scaleIt(PORTRAIT_DIAMETER.toInt())
        )
        val matrixStack = MatrixStack()
        matrixStack.translate(
            portraitStartX + PORTRAIT_DIAMETER / 2.0,
            y.toDouble() + PORTRAIT_OFFSET ,
            0.0
        )
        matrixStack.push()
        if (ballState != null && ballState.phase == ClientBallDisplay.Phase.SHAKING) {
            drawPokeBall(
                state = ballState,
                matrixStack = matrixStack,
            )
        } else {
            matrixStack.push()
            drawPortraitPokemon(
                species = species,
                aspects = aspects,
                matrixStack = matrixStack,
                scale = 18F * (ballState?.scale ?: 1F),
                reversed = reversed,
                state = state
            )
            matrixStack.pop()
        }
        matrixStack.pop()
        RenderSystem.disableScissor()

        // Third render the tile
        val colourNonNull = colour ?: Triple(1, 1, 1)
        val (r, g, b) = colourNonNull

        blitk(
            matrixStack = matrices,
            texture = if (reversed) battleInfoBaseFlipped else battleInfoBase,
            x = x,
            y = y,
            height = TILE_HEIGHT,
            width = TILE_WIDTH,
            alpha = opacity,
            red = r,
            green = g,
            blue = b
        )

        // Draw labels
        val infoBoxX = x + if (!reversed) { PORTRAIT_DIAMETER + 2 * PORTRAIT_OFFSET + 2 } else { INFO_OFFSET_X.toFloat() }
        drawScaledText(
            scale = 0.7F,
            matrixStack = matrices,
            text = displayName,
            x = infoBoxX,
            y = y + 5,
            opacity = opacity,
            shadow = false
        )
        drawScaledText(
            scale = 0.65F,
            matrixStack = matrices,
            text = lang("ui.lv"),
            x = infoBoxX + 55,
            y = y + 5,
            opacity = opacity,
            shadow = false
        )

        drawScaledText(
            scale = 0.75F,
            matrixStack = matrices,
            text = level.toString().text(),
            x = infoBoxX + 70,
            y = y + 4.3,
            opacity = opacity,
            shadow = false,
            centered = true
        )

        val (healthRed, healthGreen) = getDepletableRedGreen(hpRatio)
        blitk(
            matrixStack = matrices,
            texture = CobbledResources.WHITE,
            x = infoBoxX - 0.5,
            y = y + 13,
            height = 8.5,
            width = hpRatio * 76.5,
            red = healthRed,
            green = healthGreen,
            blue = 0
        )
    }

    fun drawPokeBall(
        state: ClientBallDisplay,
        matrixStack: MatrixStack,
        scale: Float = 6F,
        reversed: Boolean = false
    ) {
        val model = PokeBallModelRepository.getModel(state.pokeBall).entityModel as PokeBallModel
        val texture = PokeBallModelRepository.getModelTexture(state.pokeBall)
        val renderType = model.getLayer(texture)

        RenderSystem.applyModelViewMatrix()
        val quaternion1 = Vec3f.POSITIVE_Y.getDegreesQuaternion(-32F * if (reversed) -1F else 1F)
        val quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(5F)

        model.getPose(PoseType.PORTRAIT)?.let { state.setPose(it.poseName) }
        model.setupAnimStateful(null, state, 0F, 0F, 0F, 0F, 0F)

        matrixStack.scale(scale, -scale, scale)
        matrixStack.translate(0.0, -4.5, -4.0)
        matrixStack.scale(scale * state.scale, scale * state.scale, 0.01F)

        matrixStack.multiply(quaternion1)
        matrixStack.multiply(quaternion2)

        val light1 = Vec3f(0.2F, 1.0F, -1.0F)
        val light2 = Vec3f(0.1F, -1.0F, 2.0F)
        RenderSystem.setShaderLights(light1, light2)
        quaternion1.conjugate()

        val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val buffer = immediate.getBuffer(renderType)
        val packedLight = LightmapTextureManager.pack(8, 4)
        model.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)

        immediate.draw()

        DiffuseLighting.enableGuiDepthLighting()
    }
}