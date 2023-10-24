/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen6

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class AvaluggModel (root: ModelPart) : PokemonPoseableModel(), QuadrupedFrame, HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("avalugg")
    override val head = getPart("head")

    override val hindLeftLeg = getPart("leg_back_left")
    override val hindRightLeg = getPart("leg_back_right")
    override val foreLeftLeg= getPart("leg_front_left")
    override val foreRightLeg = getPart("leg_front_right")

    override val portraitScale = 1.0F
    override val portraitTranslation = Vec3d(-1.35, 1.5, 0.0)

    override val profileScale = 0.27F
    override val profileTranslation = Vec3d(-0.05, 1.2, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose
    lateinit var water_surface_idle: PokemonPose
    lateinit var water_surface_swim: PokemonPose

    val wateroffset = -3.5

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("avalugg", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("avalugg", "blink").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("avalugg", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES - PoseType.FLOAT,
            transformTicks = 10,
            condition = { !it.isBattling && !it.isTouchingWater },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("avalugg", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES - PoseType.SWIM,
            transformTicks = 10,
            condition = { !it.isTouchingWater },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("avalugg", "ground_walk")
            )
        )

        water_surface_idle = registerPose(
            poseName = "surface_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink),
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                bedrock("avalugg", "water_idle"),
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.Y_AXIS, wateroffset)
            )
        )

        water_surface_swim = registerPose(
            poseName = "surface_swim",
            poseTypes = PoseType.MOVING_POSES,
            condition = { it.isTouchingWater },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("avalugg", "water_swim"),
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.Y_AXIS, wateroffset)
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
                bedrock("avalugg", "battle_idle")
            )
        )
    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walking, battleidle, sleep, water_surface_idle, water_surface_swim)) bedrockStateful("avalugg", "faint") else null
}