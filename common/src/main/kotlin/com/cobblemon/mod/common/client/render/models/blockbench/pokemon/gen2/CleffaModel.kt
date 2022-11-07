/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d
class CleffaModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("cleffa")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.1, -0.45, 0.0)

    override val profileScale = 0.6F
    override val profileTranslation = Vec3d(0.0, 0.75, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var leftShoulder: PokemonPose
    lateinit var rightShoulder: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("0173_cleffa/cleffa", "ground_idle")
            )
        )

        val shoulderDisplacement = 4.0

        leftShoulder = registerPose(
            poseName = "left_shoulder",
            poseTypes = setOf(PoseType.SHOULDER_LEFT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("0173_cleffa/cleffa", "ground_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(X_AXIS, shoulderDisplacement)
            )
        )

        rightShoulder = registerPose(
            poseName = "right_shoulder",
            poseTypes = setOf(PoseType.SHOULDER_RIGHT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("0173_cleffa/cleffa", "ground_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(X_AXIS, -shoulderDisplacement)
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("0173_cleffa/cleffa", "ground_idle"),
                bedrock("0173_cleffa/cleffa", "ground_walk")
            )
        )
    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("0173_cleffa/cleffa", "faint") else null
}