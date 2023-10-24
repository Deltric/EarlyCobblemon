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
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class VolbeatModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("volbeat")
    override val leftWing = getPart("left_wing")
    override val rightWing = getPart("right_wing")
    override val leftLeg = getPart("left_leg")
    override val rightLeg = getPart("right_leg")
    override val head = getPart("head")

    override val portraitScale = 2.0F
    override val portraitTranslation = Vec3d(-0.2, -0.3, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.3, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var battleidle: PokemonPose

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("volbeat", "blink").setPreventsIdle(false) }

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("volbeat", "sleep"))
        )

        stand = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STAND,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.isBattling && !it.isTouchingWater},
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "ground_idle")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseTypes = setOf(PoseType.HOVER, PoseType.FLOAT),
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseTypes = setOf(PoseType.FLY, PoseType.SWIM),
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "air_fly")
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseType = PoseType.WALK,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volbeat", "battle_idle")
            )
        )
    }
}