package com.cobblemon.mod.common.client.render.generic

import com.cobblemon.mod.common.client.entity.GenericBedrockClientDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.repository.GenericBedrockModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.entity.generic.GenericBedrockEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis

class GenericBedrockRenderer(context: EntityRendererFactory.Context) : EntityRenderer<GenericBedrockEntity>(context) {
    override fun getTexture(entity: GenericBedrockEntity) = GenericBedrockModelRepository.getTexture(entity.category, entity.aspects, entity.delegate as GenericBedrockClientDelegate)
    override fun render(entity: GenericBedrockEntity, yaw: Float, partialTicks: Float, poseStack: MatrixStack, buffer: VertexConsumerProvider, packedLight: Int) {
        if (entity.isInvisible) {
            return
        }

        val model = GenericBedrockModelRepository.getPoser(entity.category, entity.aspects)
        poseStack.push()
        poseStack.translate(0.0, 1.5, 0.0)
        poseStack.scale(-1.0F, -1.0F, 1.0F)
        poseStack.scale(entity.scale, entity.scale, entity.scale)
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw))
        val vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(buffer, model.getLayer(getTexture(entity)), false, false)

        val state = entity.delegate as GenericBedrockClientDelegate
        model.setLayerContext(buffer, state, PokemonModelRepository.getLayers(entity.category, entity.aspects))
        model.setAngles(entity, 0f, 0f, entity.age + partialTicks, 0F, 0F)
        model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)

        model.green = 1F
        model.blue = 1F
        model.red = 1F

        model.resetLayerContext()

        poseStack.pop()
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight)
    }
}