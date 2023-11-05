/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.animation

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import java.lang.Float.min
import net.minecraft.entity.Entity

/**
 * An animation that gradually moves any [ModelFrame] from one pose to another.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
class PoseTransitionAnimation<T : Entity>(
    val beforePose: Pose<T, *>,
    val afterPose: Pose<T, *>,
    val durationTicks: Int = 20,
    val curve: WaveFunction = sineFunction(amplitude = 0.5F, period = 2F, phaseShift = 0.5F, verticalShift = 0.5F)
) : StatefulAnimation<T, ModelFrame> {
    override val isTransform = true
    override val isPosePauser = false

    var initialized = false
    var startTime = 0F
    var endTime = 0F// startTime + durationTicks * 50L

    fun initialize(state: PoseableEntityState<T>) {
        startTime = state.animationSeconds
        endTime = startTime + durationTicks / 20F
        initialized = true
        println("Beginning pose transition from ${beforePose.poseName} to ${afterPose.poseName}")
        System.out.flush()
    }

    override fun preventsIdle(entity: T?, state: PoseableEntityState<T>, idleAnimation: StatelessAnimation<T, *>) = false
    override fun run(
        entity: T?,
        model: PoseableEntityModel<T>,
        state: PoseableEntityState<T>,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ): Boolean {
        if (state.allStatefulAnimations.any { it.isPosePauser }) {
            state.poseTransitionPortion = 1F
            return false
        } else if (!initialized) {
            initialize(state)
        }

        val now = state.animationSeconds
        val durationSeconds = (endTime - startTime)
        val passedSeconds = (now - startTime)
        val ratio = min(passedSeconds / durationSeconds, 1F)
        val newIntensity = curve(ratio).coerceIn(0F..1F)
        state.poseTransitionPortion = 1 - newIntensity
        if (state.allStatefulAnimations.any { it.isPosePauser }) {
            state.poseTransitionPortion = 1F
            // There is a pose pauser, so don't show pose transitioning (we are still gonna transition)
            return true
        }

        if (ratio < 1F) {
            model.applyPose(afterPose.poseName, newIntensity * state.statefulOverridePortion)
            afterPose.idleAnimations.forEach {
                it.apply(entity, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, newIntensity)
            }
        } else {
            // We finished transitioning, set the pose and apply the new one at full strength. The idle animations run after this so we don't need to call it here.
            state.poseTransitionPortion = 1F
            state.setPose(afterPose.poseName)
            model.applyPose(afterPose.poseName, state.statefulOverridePortion)
            println("Finished transition from ${beforePose.poseName} to ${afterPose.poseName}, start time was $startTime and end time $endTime")
            System.out.flush()
        }

        return ratio < 1F
    }
}