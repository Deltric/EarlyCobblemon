/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

// import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BimanualFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
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

class CeruledgeModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("ceruledge")
    override val head = getPart("head")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")
    val bladeright = getPart("blade_right")
    val bladeleft = getPart("blade_left")
    val passivebladeright = getPart("passive_blade_right")
    val passivebladeleft = getPart("passive_blade_left")

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(-0.2, 1.9, 0.0)

    override val profileScale = 0.5F
    override val profileTranslation = Vec3d(0.0, 1.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("ceruledge", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("ceruledge", "blink").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            transformedParts = arrayOf(
                bladeright.asTransformed().withVisibility(visibility = false),
                bladeleft.asTransformed().withVisibility(visibility = false),
                passivebladeright.asTransformed().withVisibility(visibility = true),
                passivebladeleft.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(bedrock("ceruledge", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.isBattling },
            transformedParts = arrayOf(
                bladeright.asTransformed().withVisibility(visibility = false),
                bladeleft.asTransformed().withVisibility(visibility = false),
                passivebladeright.asTransformed().withVisibility(visibility = true),
                passivebladeleft.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("ceruledge", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            transformedParts = arrayOf(
                bladeright.asTransformed().withVisibility(visibility = false),
                bladeleft.asTransformed().withVisibility(visibility = false),
                passivebladeright.asTransformed().withVisibility(visibility = true),
                passivebladeleft.asTransformed().withVisibility(visibility = true)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("ceruledge", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isBattling },
            transformedParts = arrayOf(
                bladeright.asTransformed().withVisibility(visibility = true),
                bladeleft.asTransformed().withVisibility(visibility = true),
                passivebladeright.asTransformed().withVisibility(visibility = false),
                passivebladeleft.asTransformed().withVisibility(visibility = false)
            ),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("ceruledge", "battle_idle")
            )
        )
    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("ceruledge", "faint2") else null
}