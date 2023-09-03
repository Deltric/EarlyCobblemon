/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen8

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CorviknightModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("corviknight")
    override val leftWing = getPart("wing_left")
    override val rightWing = getPart("wing_right")
    override val leftLeg = getPart("leg_left")
    override val rightLeg = getPart("leg_right")
    override val head = getPart("head")


    override val portraitScale = 1.0F
    override val portraitTranslation = Vec3d(-0.45, 3.0, 0.0)

    override val profileScale = 0.36F
    override val profileTranslation = Vec3d(0.0, 1.2, 0.0)


    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose

    override fun registerPoses() {

        val blink = quirk("blink") { bedrockStateful("corviknight", "blink").setPreventsIdle(false) }
        stand = registerPose(
            poseName = "standing",
            poseTypes = PoseType.SHOULDER_POSES + PoseType.UI_POSES + PoseType.STAND,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "ground_idle")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "air_idle"),
                WingFlapIdleAnimation(this,
                    flapFunction = sineFunction(verticalShift = -10F.toRadians(), period = 0.9F, amplitude = 0.6F),
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = TransformedModelPart.Z_AXIS
                )
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "air_fly"),
                WingFlapIdleAnimation(this,
                    flapFunction = sineFunction(verticalShift = -14F.toRadians(), period = 0.9F, amplitude = 0.9F),
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = TransformedModelPart.Z_AXIS
                )
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseType = PoseType.WALK,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("corviknight", "ground_idle"),
                BipedWalkAnimation(this, periodMultiplier = 0.6F, amplitudeMultiplier = 0.9F)
            )
        )
    }
}