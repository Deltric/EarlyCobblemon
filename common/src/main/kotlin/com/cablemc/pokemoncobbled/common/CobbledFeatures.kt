package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.registry.CompletableRegistry
import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.SingleStateFeatureConfig

object CobbledFeatures : CompletableRegistry<Feature<*>>(Registry.FEATURE_KEY) {
    private fun <T : Feature<*>> register(name: String, feature: Supplier<T>) : RegistrySupplier<T> {
        return queue(name, feature)
    }

    val APRICORN_TREE_FEATURE = register("apricorn_tree_feature") { ApricornTreeFeature(SingleStateFeatureConfig.CODEC) }
}