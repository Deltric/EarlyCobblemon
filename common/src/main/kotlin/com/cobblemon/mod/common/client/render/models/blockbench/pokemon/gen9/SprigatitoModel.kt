/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class SprigatitoModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("sprigatito")
    override val head = getPart("head")

    override val portraitScale = 2.1F
    override val portraitTranslation = Vec3d(-0.35, -0.7, 0.0)

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.53, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("sprigatito", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("sprigatito", "blink").setPreventsIdle(false) }
        val earTwitchRight = quirk("ear_twitch_right", secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("sprigatito", "quirk_ear-twitch-left").setPreventsIdle(false) }
        val earTwitchLeft = quirk("ear_twitch_left", secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("sprigatito", "quirk_ear-twitch-right").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            quirks = arrayOf(earTwitchRight, earTwitchLeft),
            idleAnimations = arrayOf(bedrock("sprigatito", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            condition = { !it.isBattling },
            quirks = arrayOf(blink, earTwitchRight, earTwitchLeft),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("sprigatito", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, earTwitchRight, earTwitchLeft),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("sprigatito", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink, earTwitchRight, earTwitchLeft),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("sprigatito", "battle_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("sprigatito", "faint") else null
}