/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen7

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class PrimarinaModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("primarina")
    override val head = getPart("head")

    override val portraitScale = 2.3F
    override val portraitTranslation = Vec3d(-0.9, 0.6, 0.0)

    override val profileScale = 0.55F
    override val profileTranslation = Vec3d(0.0, 0.8, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var float: PokemonPose
    lateinit var swim: PokemonPose

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("primarina", "blink").setPreventsIdle(false) }

        standing = registerPose(
                poseName = "standing",
                poseTypes = UI_POSES + PoseType.STAND,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("primarina", "ground_idle")
                )
        )

        walk = registerPose(
                poseName = "walk",
                poseType = PoseType.WALK,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("primarina", "ground_idle")
                )
        )

        float = registerPose(
                poseName = "float",
                poseType = PoseType.FLOAT,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("primarina", "ground_idle")
                )
        )

        swim = registerPose(
                poseName = "swim",
                poseType = PoseType.SWIM,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("primarina", "ground_idle")
                )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("primarina", "faint") else null
}