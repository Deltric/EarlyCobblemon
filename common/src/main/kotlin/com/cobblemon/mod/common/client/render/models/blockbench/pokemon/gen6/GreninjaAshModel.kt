/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen6

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class GreninjaAshModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("greninja_ash")
    override val head = getPart("head")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")
    val shurikenHand = getPart("shuriken_hand")
    val shurikenChest = getPart("shuriken_chest")

    override val portraitScale = 2.2F
    override val portraitTranslation = Vec3d(-0.1, 1.34, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.7, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var float: PokemonPose
    lateinit var swim: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("greninja_ash", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("greninja_ash", "blink").setPreventsIdle(false)}
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            transformTicks = 10,
            transformedParts = arrayOf(
                shurikenChest.asTransformed().withVisibility(visibility = true),
                shurikenHand.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(bedrock("greninja_ash", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES - PoseType.FLOAT,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.isBattling },
            transformedParts = arrayOf(
                shurikenChest.asTransformed().withVisibility(visibility = true),
                shurikenHand.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("greninja_ash", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseType = PoseType.WALK,
            transformTicks = 10,
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                shurikenChest.asTransformed().withVisibility(visibility = true),
                shurikenHand.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("greninja_ash", "ground_walk")
            )
        )

        float = registerPose(
            poseName = "swim_idle",
            poseTypes = setOf(PoseType.FLOAT, PoseType.HOVER),
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.isBattling },
            transformedParts = arrayOf(
                shurikenChest.asTransformed().withVisibility(visibility = true),
                shurikenHand.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("greninja_ash", "water_idle")
            )
        )

        swim = registerPose(
            poseName = "swim",
            poseTypes = setOf(PoseType.SWIM, PoseType.FLY),
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.isBattling },
            transformedParts = arrayOf(
                shurikenChest.asTransformed().withVisibility(visibility = true),
                shurikenHand.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("greninja_ash", "water_swim")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isBattling },
            transformedParts = arrayOf(
                shurikenChest.asTransformed().withVisibility(visibility = false),
                shurikenHand.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("greninja_ash", "battle_idle")
            )

        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk, battleidle, swim, float, sleep)) bedrockStateful("greninja_ash", "faint") else null
}