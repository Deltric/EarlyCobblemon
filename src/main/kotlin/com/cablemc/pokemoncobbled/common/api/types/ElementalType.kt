package com.cablemc.pokemoncobbled.common.api.types

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class ElementalType(
    val name: String,
    val displayName: Component,
    val textureXMultiplier: Int,
    val resourceLocation: ResourceLocation = ResourceLocation(PokemonCobbled.MODID, "ui/types.png")
) {
}