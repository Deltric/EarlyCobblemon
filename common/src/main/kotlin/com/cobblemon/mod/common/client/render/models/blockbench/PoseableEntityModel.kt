/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.client.render.ModelLayer
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PoseTransitionAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.RotationFunctionStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.TranslationFunctionStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.SimpleQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

/**
 * A model that can be posed and animated using [StatelessAnimation]s and [StatefulAnimation]s. This
 * requires poses to be registered and should implement any [ModelFrame] interfaces that apply to this
 * model. Implementing the render functions is possible but not necessary.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PoseableEntityModel<T : Entity>(
    renderTypeFunc: (Identifier) -> RenderLayer = RenderLayer::getEntityCutout
) : EntityModel<T>(renderTypeFunc), ModelFrame {
    val context: RenderContext = RenderContext()

    /** Whether the renderer that will process this is going to do the weird -1.5 Y offset bullshit that the living entity renderer does. */
    abstract val isForLivingEntityRenderer: Boolean

    val poses = mutableMapOf<String, Pose<T, out ModelFrame>>()
    lateinit var locatorAccess: LocatorAccess

    var red = 1F
    var green = 1F
    var blue = 1F
    var alpha = 1F

    @Transient
    var currentLayers: Iterable<ModelLayer> = listOf()

    @Transient
    var bufferProvider: VertexConsumerProvider? = null

    @Transient
    var currentState: PoseableEntityState<T>? = null

    /**
     * A list of [TransformedModelPart] that are relevant to any frame or animation.
     * This allows the original rotations to be reset.
     */
    val relevantParts = mutableListOf<TransformedModelPart>()
    val relevantPartsByName = mutableMapOf<String, TransformedModelPart>()

    /** Registers the different poses this model is capable of ahead of time. Should use [registerPose] religiously. */
    abstract fun registerPoses()

    /** Gets the [PoseableEntityState] for an entity. */
    abstract fun getState(entity: T): PoseableEntityState<T>

    fun getChangeFactor(part: Bone) = relevantParts.find { it.modelPart === part }?.changeFactor ?: 1F
    fun scaleForPart(part: ModelPart, value: Float) = getChangeFactor(part) * value

    fun withLayerContext(
        buffer: VertexConsumerProvider,
        state: PoseableEntityState<T>?,
        layers: Iterable<ModelLayer>,
        action: () -> Unit
    ) {
        setLayerContext(buffer, state, layers)
        action()
        resetLayerContext()
    }

    fun setLayerContext(buffer: VertexConsumerProvider, state: PoseableEntityState<T>?, layers: Iterable<ModelLayer>) {
        currentLayers = layers
        bufferProvider = buffer
        currentState = state
    }

    fun resetLayerContext() {
        currentLayers = emptyList()
        bufferProvider = null
        currentState = null
    }

    open fun getOverlayTexture(entity: Entity?): Int? {
        return if (entity is LivingEntity) {
            OverlayTexture.packUv(
                OverlayTexture.getU(0F),
                OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0)
            )
        } else if (entity != null) {
            OverlayTexture.DEFAULT_UV
        } else {
            null
        }
    }

    /**
     * Registers a pose for this model.
     *
     * @param poseType The type of pose it is, as a [PoseType]
     * @param condition The condition for this pose to apply
     * @param idleAnimations The stateless animations to use as idles unless a [StatefulAnimation] prevents it.
     * @param transformedParts All the transformed forms of parts of the body that define this pose.
     */
    fun <F : ModelFrame> registerPose(
        poseType: PoseType,
        condition: (T) -> Boolean = { true },
        transformTicks: Int = 10,
        onTransitionedInto: (PoseableEntityState<T>?) -> Unit = {},
        idleAnimations: Array<StatelessAnimation<T, out F>> = emptyArray(),
        transformedParts: Array<TransformedModelPart> = emptyArray(),
        quirks: Array<ModelQuirk<T, *>> = emptyArray()
    ): Pose<T, F> {
        return Pose(
            poseType.name,
            setOf(poseType),
            condition,
            onTransitionedInto,
            transformTicks,
            idleAnimations,
            transformedParts,
            quirks
        ).also {
            poses[poseType.name] = it
        }
    }

    fun <F : ModelFrame> registerPose(
        poseName: String,
        poseTypes: Set<PoseType>,
        condition: (T) -> Boolean = { true },
        transformTicks: Int = 10,
        onTransitionedInto: (PoseableEntityState<T>?) -> Unit = {},
        idleAnimations: Array<StatelessAnimation<T, out F>> = emptyArray(),
        transformedParts: Array<TransformedModelPart> = emptyArray(),
        quirks: Array<ModelQuirk<T, *>> = emptyArray()
    ): Pose<T, F> {
        return Pose(
            poseName,
            poseTypes,
            condition,
            onTransitionedInto,
            transformTicks,
            idleAnimations,
            transformedParts,
            quirks
        ).also {
            poses[poseName] = it
        }
    }

    fun <F : ModelFrame> registerPose(
        poseName: String,
        poseType: PoseType,
        condition: (T) -> Boolean = { true },
        transformTicks: Int = 10,
        onTransitionedInto: (PoseableEntityState<T>?) -> Unit = {},
        idleAnimations: Array<StatelessAnimation<T, out F>> = emptyArray(),
        transformedParts: Array<TransformedModelPart> = emptyArray(),
        quirks: Array<ModelQuirk<T, *>> = emptyArray()
    ): Pose<T, F> {
        return Pose(
            poseName,
            setOf(poseType),
            condition,
            onTransitionedInto,
            transformTicks,
            idleAnimations,
            transformedParts,
            quirks
        ).also {
            poses[poseName] = it
        }
    }

    fun ModelPart.registerChildWithAllChildren(name: String): ModelPart {
        val child = this.getChild(name)!!
        registerRelevantPart(name to child)
        loadAllNamedChildren(child)
        return child
    }

    fun ModelPart.registerChildWithSpecificChildren(name: String, nameList: Iterable<String>): ModelPart {
        val child = getChild(name)
        registerRelevantPart(name to child)
        loadSpecificNamedChildren(child, nameList)
        return child
    }

    fun initializeLocatorAccess() {
        locatorAccess = LocatorAccess.resolve(rootPart) ?: LocatorAccess(rootPart)
    }

    fun getPart(name: String) = relevantPartsByName[name]!!.modelPart

    private fun loadSpecificNamedChildren(modelPart: ModelPart, nameList: Iterable<String>) {
        for ((name, child) in modelPart.children.entries) {
            if (name in nameList) {
                val transformed = child.asTransformed()
                relevantParts.add(transformed)
                relevantPartsByName[name] = transformed
                loadAllNamedChildren(child)
            }
        }
    }

    fun loadAllNamedChildren(bone: Bone) {
        if (bone is ModelPart) loadAllNamedChildren(bone)
    }

    fun loadAllNamedChildren(modelPart: ModelPart) {
        for ((name, child) in modelPart.children.entries) {
            val transformed = child.asTransformed()
            relevantParts.add(transformed)
            relevantPartsByName[name] = transformed
            loadAllNamedChildren(child)
        }
    }

    fun registerRelevantPart(name: String, part: ModelPart): ModelPart {
        val transformedPart = part.asTransformed()
        relevantParts.add(transformedPart)
        relevantPartsByName[name] = transformedPart
        return part
    }

    fun registerRelevantPart(pairing: Pair<String, ModelPart>) = registerRelevantPart(pairing.first, pairing.second)

    override fun render(
        stack: MatrixStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        render(context, stack, buffer, packedLight, packedOverlay, r, g, b, a)
    }

    fun render(
        context: RenderContext,
        stack: MatrixStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        rootPart.render(
            context,
            stack,
            buffer,
            packedLight,
            getOverlayTexture(context.request(RenderContext.ENTITY)) ?: packedOverlay,
            red * r,
            green * g,
            blue * b,
            alpha * a
        )

        val provider = bufferProvider
        if (provider != null) {
            for (layer in currentLayers) {
                val texture = layer.texture?.invoke(currentState?.animationSeconds ?: 0F) ?: continue
                val renderLayer = getLayer(texture, layer.emissive, layer.translucent)
                val consumer = provider.getBuffer(renderLayer)
                stack.push()
                rootPart.render(
                    context,
                    stack,
                    consumer,
                    packedLight,
                    getOverlayTexture(context.request(RenderContext.ENTITY)) ?: packedOverlay,
                    layer.tint.x,
                    layer.tint.y,
                    layer.tint.z,
                    layer.tint.w
                )
                stack.pop()
            }
        }
    }

    fun makeLayer(texture: Identifier, emissive: Boolean, translucent: Boolean): RenderLayer {
        val multiPhaseParameters: RenderLayer.MultiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .program(
                when {
                    emissive && translucent -> RenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM
                    !emissive && translucent -> RenderPhase.ENTITY_TRANSLUCENT_PROGRAM
                    !emissive && !translucent -> RenderPhase.ENTITY_CUTOUT_PROGRAM
                    else -> RenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM // This one should be changed to maybe a custom shader? Translucent stuffs with things
                }
            )
            .texture(RenderPhase.Texture(texture, false, false))
            .transparency(if (translucent) RenderPhase.TRANSLUCENT_TRANSPARENCY else RenderPhase.NO_TRANSPARENCY)
            .cull(RenderPhase.ENABLE_CULLING)
            .writeMaskState(RenderPhase.ALL_MASK)
            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
            .build(false)

        return RenderLayer.of(
            "cobblemon_entity_layer",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            256,
            true,
            translucent,
            multiPhaseParameters
        )
    }

    fun getLayer(texture: Identifier, emissive: Boolean, translucent: Boolean): RenderLayer {
        return if (!emissive && !translucent) {
            RenderLayer.getEntityCutout(texture)
        } else if (!emissive) {
            RenderLayer.getEntityTranslucent(texture)
        } else {
            makeLayer(texture, emissive = emissive, translucent = translucent)
        }
    }


    /** Applies the given pose type to the model, if there is a matching pose. */
    fun applyPose(pose: String) = getPose(pose)?.transformedParts?.forEach { it.apply() }
    fun getPose(pose: PoseType) = poses.values.firstOrNull { pose in it.poseTypes }
    fun getPose(name: String) = poses[name]

    /** Puts the model back to its original location and rotations. */
    fun setDefault() = relevantParts.forEach { it.applyDefaults() }

    val quirks = mutableListOf<ModelQuirk<T, *>>()

    /**
     * Sets up the angles and positions for the model knowing that there is no state. Is given a pose type to use,
     * and optionally things like limb swinging and head rotations.
     */
    fun setupAnimStateless(
        poseType: PoseType,
        limbSwing: Float = 0F,
        limbSwingAmount: Float = 0F,
        headYaw: Float = 0F,
        headPitch: Float = 0F,
        ageInTicks: Float = 0F
    ) {
        setupAnimStateless(setOf(poseType), limbSwing, limbSwingAmount, headYaw, headPitch, ageInTicks)
    }

    /**
     * Sets up the angles and positions for the model knowing that there is no state. Is given a list of pose types,
     * and it will use the first of these that is defined for the model.
     */
    fun setupAnimStateless(
        poseTypes: Set<PoseType>,
        limbSwing: Float = 0F,
        limbSwingAmount: Float = 0F,
        headYaw: Float = 0F,
        headPitch: Float = 0F,
        ageInTicks: Float = 0F
    ) {
        this.context.pop(RenderContext.ENTITY)
        setDefault()
        val pose = poseTypes.firstNotNullOfOrNull { getPose(it) } ?: poses.values.first()
        pose.transformedParts.forEach { it.apply() }
        pose.idleStateless(this, null, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
    }

    fun setupAnimStateful(
        entity: T?,
        state: PoseableEntityState<T>,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        context.put(RenderContext.ENTITY, entity)
        setupEntityTypeContext(entity)
        state.currentModel = this
        setDefault()
        state.preRender()
        updateLocators(state)
        var poseName = state.getPose()
        var pose = poseName?.let { getPose(it) }
        val entityPoseType = if (entity is Poseable) entity.getPoseType() else null

        if (entity != null && (poseName == null || pose == null || !pose.condition(entity) || entityPoseType !in pose.poseTypes)) {
            val desirablePose = poses.values.firstOrNull {
                (entityPoseType == null || entityPoseType in it.poseTypes) && it.condition(entity)
            }
                ?: Pose("none", setOf(PoseType.NONE), { true }, {}, 0, emptyArray(), emptyArray(), emptyArray())

            // If this condition matches then it just no longer fits this pose
            if (pose != null && poseName != null) {
                moveToPose(entity, state, desirablePose)
            } else {
                pose = desirablePose
                poseName = desirablePose.poseName
                getState(entity).setPose(poseName)
            }
        } else {
            poseName = poseName ?: poses.values.first().poseName
        }

        val currentPose = getPose(poseName)
        applyPose(poseName)
        if (currentPose != null) {
            // Remove any quirk animations that don't exist in our current pose
            state.quirks.keys.filterNot(currentPose.quirks::contains).forEach(state.quirks::remove)
            // Tick all the quirks
            currentPose.quirks.forEach {
                it.tick(
                    entity,
                    this,
                    state,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    headYaw,
                    headPitch
                )
            }
        }

        val removedStatefuls = state.statefulAnimations.toList()
            .filterNot { it.run(entity, this, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch) }
        state.statefulAnimations.removeAll(removedStatefuls)
        state.currentPose?.let { getPose(it) }
            ?.idleStateful(entity, this, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
        state.applyAdditives(entity, this, state)
        relevantParts.forEach { it.changeFactor = 1F }
        updateLocators(state)
    }

    //This is used to set additional entity type specific context
    open fun setupEntityTypeContext(entity: T?) {}

    override fun setAngles(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        setupAnimStateful(entity, getState(entity), limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
    }

    fun moveToPose(entity: T, state: PoseableEntityState<T>, desirablePose: Pose<T, out ModelFrame>) {
        val previousPose = state.getPose()?.let { getPose(it) } ?: run {
            return state.setPose(desirablePose.poseName)
        }

        val desirablePoseType = desirablePose.poseTypes.first()

        if (state.statefulAnimations.none { it.isTransform }) {
            val transition = previousPose.transitions[desirablePose]
            if (transition == null && previousPose.transformTicks > 0) {
                state.statefulAnimations.add(
                    PoseTransitionAnimation(
                        beforePose = previousPose,
                        afterPose = desirablePose,
                        durationTicks = previousPose.transformTicks
                    )
                )
            } else if (transition != null) {
                state.statefulAnimations.add(transition(previousPose, desirablePose))
            } else {
                getState(entity).setPose(poses.values.first { desirablePoseType in it.poseTypes && it.condition(entity) }.poseName)
            }
        }
    }

    /**
     * Figures out where all of this model's locators are in real space, so that they can be
     * found and used from other client-side systems.
     */
    fun updateLocators(state: PoseableEntityState<T>) {
        val entity = context.request(RenderContext.ENTITY) ?: return
        val matrixStack = MatrixStack()
        // We could improve this to be generalized for other entities
        if (entity is PokemonEntity) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - entity.bodyYaw))
            matrixStack.push()
            matrixStack.scale(-1F, -1F, 1F)
            val scale =
                entity.pokemon.form.baseScale * entity.pokemon.scaleModifier * (entity.delegate as PokemonClientDelegate).entityScaleModifier
            matrixStack.scale(scale, scale, scale)
        } else if (entity is EmptyPokeBallEntity) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.yaw))
            matrixStack.push()
            matrixStack.scale(1F, -1F, -1F)
            matrixStack.scale(0.7F, 0.7F, 0.7F)
        }

        if (isForLivingEntityRenderer) {
            // Standard living entity offset, only God knows why Mojang did this.
            matrixStack.translate(0.0, -1.5, 0.0)
        }

        locatorAccess.update(matrixStack, state.locatorStates)
    }

    fun ModelPart.translation(
        function: WaveFunction,
        axis: Int,
        timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?
    ) = TranslationFunctionStatelessAnimation<T>(
        part = this,
        function = function,
        axis = axis,
        timeVariable = timeVariable,
        frame = this@PoseableEntityModel
    )

    fun ModelPart.rotation(
        function: WaveFunction,
        axis: Int,
        timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?
    ) = RotationFunctionStatelessAnimation(
        part = this,
        function = function,
        axis = axis,
        timeVariable = timeVariable,
        frame = this@PoseableEntityModel
    )

    fun bedrock(
        animationGroup: String,
        animation: String,
        animationPrefix: String = "animation.$animationGroup"
    ) = BedrockStatelessAnimation<T>(
        this,
        BedrockAnimationRepository.getAnimation(animationGroup, "$animationPrefix.$animation")
    )

    fun bedrockStateful(
        animationGroup: String,
        animation: String,
        animationPrefix: String = "animation.$animationGroup",
        preventsIdleCheck: (T?, PoseableEntityState<T>, StatelessAnimation<T, *>) -> Boolean = { _, _, _ -> true }
    ) = BedrockStatefulAnimation(
        BedrockAnimationRepository.getAnimation(animationGroup, "$animationPrefix.$animation"),
        preventsIdleCheck
    )

    fun quirk(
        name: String,
        secondsBetweenOccurrences: Pair<Float, Float> = 8F to 30F,
        loopTimes: IntRange = 1..1,
        condition: (state: PoseableEntityState<T>) -> Boolean = { true },
        animation: (state: PoseableEntityState<T>) -> StatefulAnimation<T, *>
    ) = SimpleQuirk(
        name = name,
        secondsBetweenOccurrences = secondsBetweenOccurrences,
        loopTimes = loopTimes,
        condition = condition,
        animations = { listOf(animation(it)) }
    )

    fun quirkMultiple(
        name: String,
        secondsBetweenOccurrences: Pair<Float, Float> = 8F to 30F,
        loopTimes: IntRange = 1..1,
        condition: (state: PoseableEntityState<T>) -> Boolean = { true },
        animations: (state: PoseableEntityState<T>) -> List<StatefulAnimation<T, *>>
    ) = SimpleQuirk(
        name = name,
        secondsBetweenOccurrences = secondsBetweenOccurrences,
        loopTimes = loopTimes,
        condition = condition,
        animations = { animations(it) }
    )
}