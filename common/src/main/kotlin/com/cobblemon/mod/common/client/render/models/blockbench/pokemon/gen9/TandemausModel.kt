/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class TandemausModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("tandemaus")
    override val head = getPart("head")

    override val portraitScale = 1.0F
    override val portraitTranslation = Vec3d(0.1, 0.0, 0.0)

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.4, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("tandemaus", "cry").setPreventsIdle(false) }

    override fun registerPoses() {

        val blink1 = quirk("blink1") { bedrockStateful("tandemaus", "blink1").setPreventsIdle(false)}
        val blink2 = quirk("blink2") { bedrockStateful("tandemaus", "blink2").setPreventsIdle(false)}

        val head2 = object : HeadedFrame {
            override val rootPart = this@TandemausModel.rootPart
            override val head: ModelPart = getPart("head2")
        }

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2),
            idleAnimations = arrayOf(
                bedrock("tandemaus", "ground_idle"),
                singleBoneLook(),
                SingleBoneLookAnimation(head2, false, false, disableX = false, disableY = false),
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2),
            idleAnimations = arrayOf(
                bedrock("tandemaus", "ground_walk"),
                singleBoneLook(),
                SingleBoneLookAnimation(head2, false, false, disableX = false, disableY = false),
            )
        )

        sleep = registerPose(
            poseName = "sleep",
            poseType = PoseType.SLEEP,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2),
            idleAnimations = arrayOf(
                bedrock("tandemaus", "ground_idle")
                //bedrock("tandemaus", "sleep")
            )
        )
    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("tandemaus", "faint") else null
}