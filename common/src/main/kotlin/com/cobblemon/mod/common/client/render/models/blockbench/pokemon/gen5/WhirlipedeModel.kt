/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class WhirlipedeModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("whirlipede")

    override val portraitScale = 1.1F
    override val portraitTranslation = Vec3d(-0.05, 0.2, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.7, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("whirlipede", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("whirlipede", "blink").setPreventsIdle(false) }

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            quirks = arrayOf(blink),
            condition = { !it.isBattling },
            idleAnimations = arrayOf(
                bedrock("whirlipede", "ground_idle")
            )
        )

        sleep = registerPose(
                poseType = PoseType.SLEEP,
                idleAnimations = arrayOf(
                    bedrock("whirlipede", "sleep")
                )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("whirlipede", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                bedrock("whirlipede", "battle_idle")
            )

        )
    }

//    override fun getFaintAnimation(
//            pokemonEntity: PokemonEntity,
//            state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("whirlipede", "faint") else null
}