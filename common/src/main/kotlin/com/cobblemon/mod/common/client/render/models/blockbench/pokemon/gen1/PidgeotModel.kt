/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class PidgeotModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("pidgeot")
    override val leftWing = getPart("wing_open_left")
    override val rightWing = getPart("wing_open_right")
    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")
    override val head = getPart("neck")
    private val tail = getPart("tail")

    override val portraitScale = 2.2F
    override val portraitTranslation = Vec3d(-0.6, 0.15, 0.0)
    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.4, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("pidgeot", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("pidgeot", "sleep"))
        )

        val blink = quirk("blink") { bedrockStateful("pidgeot", "blink").setPreventsIdle(false)}
        stand = registerPose(
            poseName = "stand",
            poseTypes = PoseType.STATIONARY_POSES - PoseType.HOVER - PoseType.FLOAT + UI_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("pidgeot", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY - PoseType.SWIM,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("pidgeot", "ground_idle"),
                BipedWalkAnimation(this)
            )
        )

        hover = registerPose(
            poseName = "floating",
            poseTypes = setOf(PoseType.FLOAT, PoseType.HOVER),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("pidgeot", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "flying",
            poseTypes = setOf(PoseType.FLY, PoseType.SWIM),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("pidgeot", "air_fly"),
                WingFlapIdleAnimation(this,
                    flapFunction = sineFunction(verticalShift = -14F.toRadians(), period = 0.9F, amplitude = 0.9F),
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = TransformedModelPart.Z_AXIS
                )
            )
        )
    }
}