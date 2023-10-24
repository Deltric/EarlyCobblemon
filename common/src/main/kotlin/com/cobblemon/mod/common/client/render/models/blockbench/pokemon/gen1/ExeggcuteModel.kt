/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ExeggcuteModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("exeggcute")

    override val portraitScale = 2.1F
    override val portraitTranslation = Vec3d(0.0, -1.9, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(-0.15, 0.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var uiPortrait: PokemonPose

    override fun registerPoses() {
        val blink1 = quirk("blink") { bedrockStateful("exeggcute", "blink").setPreventsIdle(false) }
        val blink2 = quirk("blink") { bedrockStateful("exeggcute", "blink2").setPreventsIdle(false) }
        val blink3 = quirk("blink") { bedrockStateful("exeggcute", "blink3").setPreventsIdle(false) }
        val blink4 = quirk("blink") { bedrockStateful("exeggcute", "blink4").setPreventsIdle(false) }
        val blink5 = quirk("blink") { bedrockStateful("exeggcute", "blink5").setPreventsIdle(false) }
        val blink6 = quirk("blink") { bedrockStateful("exeggcute", "blink6").setPreventsIdle(false) }
        uiPortrait = registerPose(
            poseName = "portrait",
            poseType = PoseType.PORTRAIT,
            idleAnimations = arrayOf(
                bedrock("exeggcute", "portrait")
            )
        )

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("exeggcute", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + PoseType.PROFILE,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2, blink3, blink4, blink5, blink6),
            idleAnimations = arrayOf(
                bedrock("exeggcute", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("exeggcute", "ground_idle"),
                bedrock("exeggcute", "ground_walk")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = bedrockStateful("exeggcute", "faint")
}