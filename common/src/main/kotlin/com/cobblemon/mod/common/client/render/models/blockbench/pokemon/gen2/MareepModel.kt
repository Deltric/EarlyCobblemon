/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2

import com.cobblemon.mod.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class MareepModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("mareep")
    override val head = getPart("head")

    val wool = getPart("wool")

    override val portraitScale = 2.3F
    override val portraitTranslation = Vec3d(-0.5, -1.2, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.4, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var shearedstanding: PokemonPose
    lateinit var shearedwalk: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose
    lateinit var shearedsleep: PokemonPose
    lateinit var shearedbattleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("mareep", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("mareep", "blink").setPreventsIdle(false) }
        sleep = registerPose(
            poseName = "unsheared_sleep",
            poseType = PoseType.SLEEP,
            condition = { DataKeys.HAS_BEEN_SHEARED !in it.aspects.get() },
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(bedrock("mareep", "sleep"))
        )

        shearedsleep = registerPose(
            poseName = "sheared_sleep",
            poseType = PoseType.SLEEP,
            condition = { DataKeys.HAS_BEEN_SHEARED in it.aspects.get() },
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(bedrock("mareep", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { DataKeys.HAS_BEEN_SHEARED !in it.aspects.get() && !it.isBattling },
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { DataKeys.HAS_BEEN_SHEARED !in it.aspects.get() },
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_walk")
            )
        )

        shearedstanding = registerPose(
            poseName = "shearedstanding",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 0,
            quirks = arrayOf(blink),
            condition = { DataKeys.HAS_BEEN_SHEARED in it.aspects.get() && !it.isBattling},
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_idle")
            )
        )
        shearedwalk = registerPose(
            poseName = "shearedwalking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 0,
            quirks = arrayOf(blink),
            condition = { DataKeys.HAS_BEEN_SHEARED in it.aspects.get() },
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isBattling },
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "battle_idle")
            )
        )

        shearedbattleidle = registerPose(
            poseName = "battle_idle_sheared",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { DataKeys.HAS_BEEN_SHEARED in it.aspects.get() && it.isBattling },
            transformedParts = arrayOf(
                wool.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mareep", "battle_idle")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("mareep", "faint") else null
}