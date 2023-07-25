/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1.PikachuModel
import net.minecraft.entity.Entity

/**
 * Animation that analyzes a [BedrockAnimation] and applies transformations to the model based on
 * the given animation time.
 *
 * @param frame The model frame to apply the animation to
 * @param animation The [BedrockAnimation] to be played
 *
 * @author landonjw
 * @since January 5th, 2022
 */
class BedrockStatelessAnimation<T: Entity>(frame: ModelFrame, val animation: BedrockAnimation) : StatelessAnimation<T, ModelFrame>(frame) {
    override val targetFrame: Class<ModelFrame> = ModelFrame::class.java
    val particleKeyFrames = animation.effects.filterIsInstance<BedrockParticleKeyframe>()

    override fun setAngles(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        animation.run(model, state, state?.animationSeconds ?: 0F)
    }

    override fun applyEffects(entity: T, state: PoseableEntityState<T>, previousSeconds: Float, newSeconds: Float) {
        animation.applyEffects(entity, state, (previousSeconds % animation.animationLength).toFloat(), (newSeconds % animation.animationLength).toFloat())
    }
}