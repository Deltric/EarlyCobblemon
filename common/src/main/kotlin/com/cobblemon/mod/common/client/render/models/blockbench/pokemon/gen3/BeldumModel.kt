/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class BeldumModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("beldum")

    override val portraitScale = 2.3F
    override val portraitTranslation = Vec3d(-0.3, -1.2, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("beldum", "blink").setPreventsIdle(false)}
        val quirk = quirk("quirk") { bedrockStateful("beldum", "quirk_spin").setPreventsIdle(false)}

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("beldum", "sleep"))
        )
        standing = registerPose(
            poseName = "stand",
            poseTypes = PoseType.STATIONARY_POSES - PoseType.HOVER - PoseType.FLOAT + PoseType.UI_POSES,
            condition = { !it.isBattling },
            transformTicks = 10,
            quirks = arrayOf(blink, quirk),
            idleAnimations = arrayOf(
                bedrock("beldum", "ground_idle")
            )
        )
        hover = registerPose(
            poseName = "hover",
            poseTypes = setOf(PoseType.HOVER, PoseType.FLOAT),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("beldum", "air_idle")
            )
        )
        fly = registerPose(
            poseName = "fly",
            poseTypes = setOf(PoseType.FLY, PoseType.SWIM),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("beldum", "air_fly")
            )
        )
        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY - PoseType.SWIM,
            transformTicks = 5,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("beldum", "ground_walk")
            )
        )
        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, quirk),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                bedrock("beldum", "battle_idle")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("beldum", "faint") else null
}