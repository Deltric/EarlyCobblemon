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

class VenipedeModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("venipede")

    override val portraitScale = 2.0F
    override val portraitTranslation = Vec3d(-0.5, -1.4, 0.0)

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("venipede", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("venipede", "blink").setPreventsIdle(false) }
        val twitch = quirk("twitch") { bedrockStateful("venipede", "twitch").setPreventsIdle(false) }

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            quirks = arrayOf(blink, twitch),
            idleAnimations = arrayOf(
                bedrock("venipede", "ground_idle")
            )
        )

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            quirks = arrayOf(twitch),
            idleAnimations = arrayOf(
                bedrock("venipede", "sleep"),
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink, twitch),
            idleAnimations = arrayOf(
                bedrock("venipede", "ground_walk")
            )
        )
    }

//    override fun getFaintAnimation(
//            pokemonEntity: PokemonEntity,
//            state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("venipede", "faint") else null
}