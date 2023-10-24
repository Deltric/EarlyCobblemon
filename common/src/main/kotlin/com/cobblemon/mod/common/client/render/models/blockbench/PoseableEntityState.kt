/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.client.render.MatrixWrapper
import com.cobblemon.mod.common.client.render.models.blockbench.additives.PosedAdditiveAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockParticleKeyframe
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.QuirkData
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import java.util.concurrent.ConcurrentLinkedQueue
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

/**
 * Represents the entity-specific state for a poseable model. The implementation is responsible for
 * handling all the state for an entity's model, and needs to be conscious of the fact that the
 * model may change without this state changing.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PoseableEntityState<T : Entity> {
    var currentModel: PoseableEntityModel<T>? = null
    var currentPose: String? = null
    val statefulAnimations: MutableList<StatefulAnimation<T, *>> = mutableListOf()
    val quirks = mutableMapOf<ModelQuirk<T, *>, QuirkData<T>>()
    val additives: MutableList<PosedAdditiveAnimation<T>> = mutableListOf()
    val poseParticles = mutableListOf<BedrockParticleKeyframe>()
    val runtime = MoLangRuntime().also {
        it.environment.structs["query"] = it.environment.structs["variable"]
    }

    val allStatefulAnimations: List<StatefulAnimation<T, *>> get() = statefulAnimations + quirks.flatMap { it.value.animations }

    protected var age = 0
    protected var currentPartialTicks = 0F

    abstract fun getEntity(): T?
    fun getPartialTicks() = currentPartialTicks
    open fun updateAge(age: Int) {
        this.age = age
    }

    open fun incrementAge(entity: T) {
        val previousAge = age
        updateAge(age + 1)
        runEffects(entity, previousAge, age)
    }

    abstract fun updatePartialTicks(partialTicks: Float)
    open fun reset() {
        updateAge(0)
    }

    val animationSeconds: Float get() = (age + getPartialTicks()) / 20F

    var timeEnteredPose = 0F

    val locatorStates = mutableMapOf<String, MatrixWrapper>()

    val renderQueue = ConcurrentLinkedQueue<() -> Unit>()

    fun isPosedIn(vararg poses: Pose<T, in ModelFrame>) = poses.any { it.poseName == currentPose }
    fun isNotPosedIn(vararg poses: Pose<T, in ModelFrame>) = poses.none { it.poseName == currentPose }

    fun preRender() {
        while (renderQueue.peek() != null) {
            val action = renderQueue.poll()
            action()
        }
    }

    fun doLater(action: () -> Unit) {
        renderQueue.offer(action)
    }

    fun getPose(): String? {
        return currentPose
    }

    fun setPose(pose: String) {
        currentPose = pose
        val model = currentModel
        if (model != null) {
            val poseImpl = model.getPose(pose) ?: return
            poseParticles.removeIf { particle -> poseImpl.idleAnimations.filterIsInstance<BedrockStatelessAnimation<*>>().flatMap { it.particleKeyFrames }.none(particle::isSameAs) }
            poseImpl.onTransitionedInto(this)
            val entity = getEntity()
            if (entity != null) {
                poseImpl.idleAnimations
                    .filterIsInstance<BedrockStatelessAnimation<*>>()
                    .flatMap { it.particleKeyFrames }
                    .filter { particle -> particle.seconds == 0F && poseParticles.none(particle::isSameAs) }
                    .forEach { it.run(entity, this) }
            }
        }
    }

    fun applyAdditives(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?) {
        additives.removeIf { !it.run(entity, model, state) }
    }

    fun setStatefulAnimations(vararg animations: StatefulAnimation<T, out ModelFrame>) {
        statefulAnimations.clear()
        statefulAnimations.addAll(animations)
    }

    fun updateLocatorPosition(position: Vec3d) {
        locatorStates.values.toList().forEach { it.updatePosition(position) }
    }

    fun runEffects(entity: T, previousAge: Int, newAge: Int) {
        val previousSeconds = previousAge / 20F
        val currentSeconds = newAge / 20F

        currentModel?.let { model ->
            val pose = currentPose?.let(model::getPose)
            allStatefulAnimations.forEach { it.applyEffects(entity, this, previousSeconds, currentSeconds) }
            pose?.getApplicableIdleAnimations(entity, this)?.forEach { it.applyEffects(entity, this, previousSeconds, currentSeconds) }
        }
    }
}