/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class YamaskModel (root: ModelPart) : PokemonPoseableModel(), BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("yamask")

    override val leftArm = getPart("arm_left")
    override val rightArm = getPart("arm_right")

    override val portraitScale = 2.5F
    override val portraitTranslation = Vec3d(-0.15, -0.45, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.7, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("yamask", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        sleep = registerPose(
                poseType = PoseType.SLEEP,
                idleAnimations = arrayOf(bedrock("yamask", "sleep"))
        )

        val blink = quirk("blink") { bedrockStateful("yamask", "blink").setPreventsIdle(false) }

        standing = registerPose(
                poseName = "standing",
                poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                    bedrock("yamask", "ground_idle")
                )
        )

        walk = registerPose(
                poseName = "walk",
                poseTypes = PoseType.MOVING_POSES,
                transformTicks = 5,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                    bedrock("yamask", "ground_walk")
                )
        )
    }

    override fun getFaintAnimation(
            pokemonEntity: PokemonEntity,
            state: PoseableEntityState<PokemonEntity>
    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("yamask", "faint") else null
}