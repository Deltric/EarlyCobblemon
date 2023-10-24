/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation

import com.bedrockk.molang.Expression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.MoParams
import com.bedrockk.molang.runtime.MoScope
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.MoValue
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.particle.ParticleStorm
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.getString
import com.cobblemon.mod.common.util.math.geometry.toRadians
import com.cobblemon.mod.common.util.resolveDouble
import java.util.SortedMap
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.crash.CrashException
import net.minecraft.util.crash.CrashReport
import net.minecraft.util.math.Vec3d

data class BedrockAnimationGroup(
    val formatVersion: String,
    val animations: Map<String, BedrockAnimation>
)

abstract class BedrockEffectKeyframe(val seconds: Float) {
    abstract fun <T : Entity> run(entity: T, state: PoseableEntityState<T>)
}

class BedrockParticleKeyframe(
    seconds: Float,
    val effect: BedrockParticleEffect,
    val locator: String,
    val scripts: List<Expression>
) : BedrockEffectKeyframe(seconds) {
    fun isSameAs(other: BedrockParticleKeyframe): Boolean {
        return if (seconds != other.seconds) {
            false
        } else if (effect != other.effect) {
            false
        } else if (locator != other.locator) {
            false
        } else if (scripts.map { it.getString() }.toSet() != other.scripts.map { it.getString() }.toSet()) {
            false
        } else {
            true
        }
    }

    override fun <T : Entity> run(entity: T, state: PoseableEntityState<T>) {
        val world = entity.world as? ClientWorld ?: return
        val matrixWrapper = state.locatorStates[locator] ?: state.locatorStates["root"]!!
        val effect = effect

        if (this in state.poseParticles) {
            return
        }

        state.poseParticles.add(this)

        val storm = ParticleStorm(
            effect = effect,
            matrixWrapper = matrixWrapper,
            world = world,
            sourceVelocity = { entity.velocity },
            sourceAlive = { !entity.isRemoved && this in state.poseParticles },
            sourceVisible = { !entity.isInvisible },
            onDespawn = { state.poseParticles.remove(this) }
        )

        storm.runtime.execute(this.scripts)
        storm.spawn()
    }
}

class BedrockSoundKeyframe(
    seconds: Float,
    val sound: Identifier
): BedrockEffectKeyframe(seconds) {
    override fun <T : Entity> run(entity: T, state: PoseableEntityState<T>) {
        val soundEvent = SoundEvent.of(sound) // Means we don't need to setup a sound registry entry for every single thing
        if (soundEvent != null) {
            MinecraftClient.getInstance().soundManager.play(
                PositionedSoundInstance(
                    soundEvent,
                    SoundCategory.NEUTRAL,
                    1F,
                    1F,
                    entity.world.random,
                    entity.x,
                    entity.y,
                    entity.z
                )
            )
        }
    }
}

class BedrockInstructionKeyframe(
    seconds: Float,
    val expressions: List<Expression>
): BedrockEffectKeyframe(seconds) {
    override fun <T : Entity> run(entity: T, state: PoseableEntityState<T>) {
        expressions.forEach { expression -> BedrockAnimation.runInstruction(entity, state, expression) }
    }
}

data class BedrockAnimation(
    val shouldLoop: Boolean,
    val animationLength: Double,
    val effects: List<BedrockEffectKeyframe>,
    val boneTimelines: Map<String, BedrockBoneTimeline>
) {
    companion object {
        val functionMappings = mutableMapOf<String, java.util.function.Function<MoParams, Any>>()
        val sharedRuntime = MoLangRuntime().also {
            it.environment.structs["query"] = it.environment.structs["variable"]
            it.environment.structs["script"] = QueryStruct(functionMappings)
        }

        var context: InstructionContext? = null

        inline fun <reified T : Entity> registerInstruction(name: String, crossinline function: (entity: T, state: PoseableEntityState<T>, params: MoParams) -> Any) {
            functionMappings[name] = java.util.function.Function { args ->
                val ctx = context ?: return@Function Unit
                if (ctx.entity is T) {
                    val ret = function(ctx.entity, ctx.state as PoseableEntityState<T>, args)
                    if (ret is Unit) {
                        return@Function 0.0
                    } else {
                        return@Function ret
                    }
                }
                Unit
            }
        }

        class InstructionContext(val entity: Entity, val state: PoseableEntityState<*>)

        fun <T : Entity> runInstruction(entity: T, state: PoseableEntityState<T>, expression: Expression) {
            val ctx = InstructionContext(entity, state)
            context = ctx
            expression.evaluate(MoScope(), sharedRuntime.environment)
            context = null
        }

        init {
            registerInstruction<Entity>("say") { entity, _, params ->
                MinecraftClient.getInstance().player?.sendMessage(params.getString(0).text())
                Unit
            }
            registerInstruction<Entity>("sound") { entity, _, params ->
                // Means we don't need to setup a sound registry entry for every single thing
                val soundEvent = SoundEvent.of(params.getString(0).asIdentifierDefaultingNamespace())
                if (soundEvent != null) {
                    val volume = if (params.contains(1)) params.getDouble(1).toFloat() else 1F
                    val pitch = if (params.contains(2)) params.getDouble(2).toFloat() else 1F
                    MinecraftClient.getInstance().soundManager.play(
                        PositionedSoundInstance(soundEvent, SoundCategory.NEUTRAL, volume, pitch, entity.world.random, entity.x, entity.y, entity.z)
                    )
                }
                Unit
            }
            registerInstruction<Entity>("random") { entity, _, params ->
                val options = mutableListOf<MoValue>()
                var index = 0
                while (params.contains(index)) {
                    options.add(params.get(index))
                    index++
                }
                return@registerInstruction options.random() // Can throw an exception if they specified no args. They'd be idiots though.
            }
        }
    }

    fun run(model: PoseableEntityModel<*>, state: PoseableEntityState<*>?, animationSeconds: Float): Boolean {
        var animationSeconds = animationSeconds
        if (shouldLoop) {
            animationSeconds %= animationLength.toFloat()
        } else if (animationSeconds > animationLength && animationLength > 0) {
            return false
        }

        boneTimelines.forEach { (boneName, timeline) ->
            val part = model.relevantPartsByName[boneName]
            if (part != null) {
                if (!timeline.position.isEmpty()) {
                    val position = timeline.position.resolve(animationSeconds.toDouble(), state?.runtime ?: sharedRuntime).multiply(model.getChangeFactor(part.modelPart).toDouble())
                    part.modelPart.apply {
                        pivotX += position.x.toFloat()
                        pivotY += position.y.toFloat()
                        pivotZ += position.z.toFloat()
                    }
                }

                if (!timeline.rotation.isEmpty()) {
                    try {
                        val rotation = timeline.rotation.resolve(animationSeconds.toDouble(), state?.runtime ?: sharedRuntime).multiply(model.getChangeFactor(part.modelPart).toDouble())
                        part.modelPart.apply {
                            pitch += rotation.x.toFloat().toRadians()
                            yaw += rotation.y.toFloat().toRadians()
                            roll += rotation.z.toFloat().toRadians()
                        }
                    } catch (e: Exception) {
                        val exception = IllegalStateException("Bad animation for species: ${((model.context.request(RenderContext.ENTITY))!! as PokemonEntity).pokemon.species.name}", e)
                        val crash = CrashReport("Cobblemon encountered an unexpected crash", exception)
                        val section = crash.addElement("Animation Details")
                        state?.let {
                            section.add("Pose", state.currentPose!!)
                        }
                        section.add("Bone", boneName)

                        throw CrashException(crash)
                    }
                }

                if (!timeline.scale.isEmpty()) {
                    var scale = timeline.scale.resolve(animationSeconds.toDouble(), state?.runtime ?: sharedRuntime)
                    val deviation = scale.multiply(-1.0).add(1.0, 1.0, 1.0).multiply(model.getChangeFactor(part.modelPart).toDouble())
                    scale = deviation.subtract(1.0, 1.0, 1.0).multiply(-1.0)
                    val mp = part.modelPart
                    mp.xScale *= scale.x.toFloat()
                    mp.yScale *= scale.y.toFloat()
                    mp.zScale *= scale.z.toFloat()
                }
            }
        }
        return true
    }

    fun <T : Entity> applyEffects(entity: T, state: PoseableEntityState<T>, previousSeconds: Float, newSeconds: Float) {
        val effectCondition: (effectKeyframe: BedrockEffectKeyframe) -> Boolean =
            if (previousSeconds > newSeconds) {
                { it.seconds >= previousSeconds || it.seconds <= newSeconds }
            } else {
                { it.seconds in previousSeconds..newSeconds }
            }

        effects.filter(effectCondition).forEach { it.run(entity, state) }
    }
}

interface BedrockBoneValue {
    fun resolve(time: Double, runtime: MoLangRuntime): Vec3d
    fun isEmpty(): Boolean
}

object EmptyBoneValue : BedrockBoneValue {
    override fun resolve(time: Double, runtime: MoLangRuntime) = Vec3d.ZERO
    override fun isEmpty() = true
}

data class BedrockBoneTimeline (
    val position: BedrockBoneValue,
    val rotation: BedrockBoneValue,
    val scale: BedrockBoneValue
)
class MolangBoneValue(
    val x: Expression,
    val y: Expression,
    val z: Expression,
    transformation: Transformation
) : BedrockBoneValue {
    val yMul = if (transformation == Transformation.POSITION) -1 else 1
    override fun isEmpty() = false
    override fun resolve(time: Double, runtime: MoLangRuntime): Vec3d {
        val environment = runtime.environment
        environment.setSimpleVariable("anim_time", DoubleValue(time))
        environment.setSimpleVariable("camera_rotation_x", DoubleValue(MinecraftClient.getInstance().gameRenderer.camera.rotation.x.toDouble()))
        environment.setSimpleVariable("camera_rotation_y", DoubleValue(MinecraftClient.getInstance().gameRenderer.camera.rotation.y.toDouble()))
        return Vec3d(
            runtime.resolveDouble(x),
            runtime.resolveDouble(y) * yMul,
            runtime.resolveDouble(z)
        )
    }

}
class BedrockKeyFrameBoneValue : HashMap<Double, BedrockAnimationKeyFrame>(), BedrockBoneValue {
    fun SortedMap<Double, BedrockAnimationKeyFrame>.getAtIndex(index: Int?): BedrockAnimationKeyFrame? {
        if (index == null) return null
        val key = this.keys.elementAtOrNull(index)
        return if (key != null) this[key] else null
    }

    override fun resolve(time: Double, runtime: MoLangRuntime): Vec3d {
        val sortedTimeline = toSortedMap()

        var afterIndex : Int? = sortedTimeline.keys.indexOfFirst { it > time }
        if (afterIndex == -1) afterIndex = null
        val beforeIndex = when (afterIndex) {
            null -> sortedTimeline.size - 1
            0 -> null
            else -> afterIndex - 1
        }
        val after = sortedTimeline.getAtIndex(afterIndex)
        val before = sortedTimeline.getAtIndex(beforeIndex)

        val afterData = after?.pre?.resolve(time, runtime) ?: Vec3d.ZERO
        val beforeData = before?.post?.resolve(time, runtime) ?: Vec3d.ZERO

        if (before != null || after != null) {
            if (before != null && before.interpolationType == InterpolationType.SMOOTH || after != null && after.interpolationType == InterpolationType.SMOOTH) {
                when {
                    before != null && after != null -> {
                        val beforePlusIndex = if (beforeIndex == null || beforeIndex == 0) null else beforeIndex - 1
                        val beforePlus = sortedTimeline.getAtIndex(beforePlusIndex)
                        val afterPlusIndex = if (afterIndex == null || afterIndex == size - 1) null else afterIndex + 1
                        val afterPlus = sortedTimeline.getAtIndex(afterPlusIndex)
                        return catmullromLerp(beforePlus, before, after, afterPlus, time, runtime)
                    }
                    before != null -> return beforeData
                    else -> return afterData
                }
            }
            else {
                when {
                    before != null && after != null -> {
                        return Vec3d(
                            beforeData.x + (afterData.x - beforeData.x) * linearLerpAlpha(before.time, after.time, time),
                            beforeData.y + (afterData.y - beforeData.y) * linearLerpAlpha(before.time, after.time, time),
                            beforeData.z + (afterData.z - beforeData.z) * linearLerpAlpha(before.time, after.time, time)
                        )
                    }
                    before != null -> return beforeData
                    else -> return afterData
                }
            }
        }
        else {
            return Vec3d(0.0, 0.0, 0.0)
        }
    }

}

abstract class BedrockAnimationKeyFrame(
    val time: Double,
    val transformation: Transformation,
    val interpolationType: InterpolationType
) {
    abstract val pre: MolangBoneValue
    abstract val post: MolangBoneValue
}

class SimpleBedrockAnimationKeyFrame(
    time: Double,
    transformation: Transformation,
    interpolationType: InterpolationType,
    val data: MolangBoneValue
): BedrockAnimationKeyFrame(time, transformation, interpolationType) {
    override val pre = data
    override val post = data
}

class JumpBedrockAnimationKeyFrame(
    time: Double,
    transformation: Transformation,
    interpolationType: InterpolationType,
    override val pre: MolangBoneValue,
    override val post: MolangBoneValue
): BedrockAnimationKeyFrame(time, transformation, interpolationType)

enum class InterpolationType {
    SMOOTH, LINEAR
}

enum class Transformation {
    POSITION, ROTATION, SCALE
}