/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen6

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class GourgeistModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("gourgeist")
    override val head = getPart("head")

    override val portraitScale = 4.0F
    override val portraitTranslation = Vec3d(-0.1, 0.6, 0.0)

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.55, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hovering: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var sleep: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("gourgeist", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("gourgeist", "blink").setPreventsIdle(false) }
        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STAND,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("gourgeist", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseType = PoseType.WALK,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("gourgeist", "ground_walk")
            )
        )

        hovering = registerPose(
            poseName = "hovering",
            poseType = PoseType.HOVER,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("gourgeist", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("gourgeist", "air_fly")
            )
        )

        sleep = registerPose(
            poseName = "sleep",
            poseType = PoseType.SLEEP,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("gourgeist", "sleep")
            )
        )
    }
}