/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class NinjaskModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("ninjask")

    override val head = getPart("head")

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(-0.2, -0.4, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.3, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("ninjask", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("ninjask", "blink").setPreventsIdle(false) }
        val barrelRoll = quirk("run_quirk") { bedrockStateful("ninjask", "run_quirk").setPreventsIdle(false) }

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("ninjask", "sleep"))
        )

        stand = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES - PoseType.HOVER,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("ninjask", "ground_idle")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            transformTicks = 10,
            quirks = arrayOf(blink, barrelRoll),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("ninjask", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            transformTicks = 10,
            quirks = arrayOf(blink, barrelRoll),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("ninjask", "air_fly")
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY,
            transformTicks = 10,
            quirks = arrayOf(blink, barrelRoll),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("ninjask", "ground_walk")
            )
        )
    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = bedrockStateful("ninjask", "faint")
}