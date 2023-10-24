/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class MeowscaradaModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("meowscarada")
    override val head = getPart("head")

    override val portraitScale = 1.9F
    override val portraitTranslation = Vec3d(-0.3, 3.0, 0.0)

    override val profileScale = 0.46F
    override val profileTranslation = Vec3d(0.0, 1.1, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("meowscarada", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("meowscarada", "blink").setPreventsIdle(false) }
        val sleep1 = quirk("sleep1", secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("meowscarada", "sleep_quirk").setPreventsIdle(false) }
        val sleep2 = quirk("sleep2", secondsBetweenOccurrences = 30F to 120F) { bedrockStateful("meowscarada", "sleep_quirk2").setPreventsIdle(false) }
        val sleep3 = quirk("sleep3", secondsBetweenOccurrences = 20F to 60F) { bedrockStateful("meowscarada", "sleep_quirk3").setPreventsIdle(false) }
        val sleep4 = quirk("sleep4", secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("meowscarada", "sleep_quirk4").setPreventsIdle(false) }

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            quirks = arrayOf(blink, sleep1, sleep2, sleep3, sleep4),
            idleAnimations = arrayOf(bedrock("meowscarada", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            condition = { !it.isBattling },
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("meowscarada", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("meowscarada", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("meowscarada", "battle_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("meowscarada", "faint") else null
}