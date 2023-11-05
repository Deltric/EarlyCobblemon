/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.EarJoint
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.RangeOfMotion
import com.cobblemon.mod.common.client.render.models.blockbench.frame.EaredFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation.Companion.Z_AXIS
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class RattataAlolanModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame, EaredFrame {
    override val rootPart = root.registerChildWithAllChildren("rattata_alolan")
    override val foreLeftLeg= getPart("leg_front_left")
    override val foreRightLeg = getPart("leg_front_right")
    override val hindLeftLeg = getPart("leg_back_left")
    override val hindRightLeg = getPart("leg_back_right")
    override val head = getPart("head")
    override val leftEarJoint: EarJoint = EarJoint(getPart("ear_left"), Z_AXIS, RangeOfMotion(0F.toRadians(), -20F.toRadians()))
    override val rightEarJoint: EarJoint = EarJoint(getPart("ear_right"), Z_AXIS, RangeOfMotion(0F.toRadians(), 20F.toRadians()))

    override val portraitScale = 2.2F
    override val portraitTranslation = Vec3d(-0.2, -1.4, 0.0)

    override val profileScale = 1.1F
    override val profileTranslation = Vec3d(0.0, 0.1, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("rattata_alolan", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("rattata_alolan", "blink").setPreventsIdle(false)}
        sleep = registerPose(
                poseType = PoseType.SLEEP,
                idleAnimations = arrayOf(bedrock("rattata_alolan", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.STAND, PoseType.FLOAT, PoseType.PORTRAIT, PoseType.SHOULDER_LEFT, PoseType.SHOULDER_RIGHT),
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { !it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("rattata_alolan", "ground_idle")
            )
        )

        walk = registerPose(
            poseType = PoseType.WALK,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("rattata_alolan", "ground_walk")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = bedrockStateful("rattata_alolan", "faint")
}
