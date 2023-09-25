/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class PelipperModel (root: ModelPart) : PokemonPoseableModel(), BipedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("pelipper")
    override val leftWing = getPart("wing_left")
    override val rightWing = getPart("wing_right")
    override val leftLeg = getPart("foot_left")
    override val rightLeg = getPart("foot_right")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(-0.2, 0.0, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.3, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var water_surface_idle: PokemonPose
    lateinit var water_surface_swim: PokemonPose
    lateinit var water_surface_sleep: PokemonPose

    val wateroffset = -6

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("pelipper", "blink").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            condition = { !it.isTouchingWater },
            idleAnimations = arrayOf(bedrock("pelipper", "sleep"))
        )

        water_surface_sleep = registerPose(
            poseType = PoseType.SLEEP,
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(bedrock("pelipper", "surfacewater_sleep")),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.Y_AXIS, wateroffset)
            )
        )

        stand = registerPose(
            poseName = "standing",
            poseTypes = PoseType.SHOULDER_POSES + PoseType.UI_POSES + PoseType.STATIONARY_POSES - PoseType.HOVER,
            transformTicks = 10,
            condition = { !it.isTouchingWater },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("pelipper", "ground_idle")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            transformTicks = 10,
            condition = { !it.isTouchingWater },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("pelipper", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            transformTicks = 10,
            condition = { !it.isTouchingWater },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("pelipper", "air_fly")
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY,
            transformTicks = 10,
            condition = { !it.isTouchingWater },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("pelipper", "ground_walk")
            )
        )

        water_surface_idle = registerPose(
            poseName = "surface_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink),
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                bedrock("pelipper", "surfacewater_idle"),
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.Y_AXIS, wateroffset)
            )
        )

        water_surface_swim = registerPose(
            poseName = "surface_swim",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink),
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                bedrock("pelipper", "surfacewater_swim"),
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.Y_AXIS, wateroffset)
            )
        )
    }
}