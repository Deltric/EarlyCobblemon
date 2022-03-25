package com.cablemc.pokemoncobbled.common.worldgen.placement

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.PlacementModifierType

object CobbledPlacementTypes {
    lateinit var IS_BIOME_TAG_FILTER: PlacementModifierType<IsBiomeTagFilter>

    private fun <P : PlacementModifier> registerType(id: String, codec: Codec<P>): PlacementModifierType<P> {
        return Registry.register(Registry.PLACEMENT_MODIFIERS, cobbledResource(id), PlacementModifierType { codec })
    }

    fun register() {
        IS_BIOME_TAG_FILTER = registerType("is_biome_tag_filter", IsBiomeTagFilter.CODEC)
    }
}