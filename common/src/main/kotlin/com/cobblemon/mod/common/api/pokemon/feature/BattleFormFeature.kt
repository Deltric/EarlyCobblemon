package com.cobblemon.mod.common.api.pokemon.feature

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound

class BattleFormFeature(formName: String, enabled: Boolean): FlagSpeciesFeature(formName, enabled) {

    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        return pokemonJSON
    }

    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        return pokemonNBT
    }

}

class BattleFlagSpeciesFeatureProvider : SpeciesFeatureProvider<BattleFormFeature>,
    CustomPokemonPropertyType<BattleFormFeature>, AspectProvider {
    override val keys: List<String>
    override val needsKey get() = true
    var default: String? = null
    val isAspect = true

    override fun examples() = setOf("true", "false")

    internal constructor() {
        this.keys = emptyList()
    }

    constructor(keys: List<String>) {
        this.keys = keys
    }

    constructor(keys: List<String>, default: Boolean) {
        this.keys = keys
        this.default = default.toString()
    }

    constructor(vararg keys: String) : this(keys.toList())

    override fun invoke(pokemon: Pokemon): BattleFormFeature? {
        return pokemon.getFeature(keys.first())
            ?: when (default) {
                in setOf("true", "false") -> BattleFormFeature(keys.first(), default.toBoolean())
                else -> null
            }
    }

    override fun invoke(nbt: NbtCompound): BattleFormFeature? {
        return if (nbt.contains(keys.first())) {
            BattleFormFeature(keys.first(), false).also { it.loadFromNBT(nbt) }
        } else null
    }

    override fun invoke(json: JsonObject): BattleFormFeature? {
        return if (json.has(keys.first())) {
            BattleFormFeature(keys.first(), false).also { it.loadFromJSON(json) }
        } else null
    }

    override fun fromString(value: String?): BattleFormFeature? {
        val isWeirdValue = value != null && value !in examples()

        if (isWeirdValue) {
            return null
        }

        return if (value == null) {
            BattleFormFeature(keys.first(), true)
        } else {
            BattleFormFeature(keys.first(), value.toBoolean())
        }
    }

    override fun provide(pokemon: Pokemon): Set<String> {
        return if (isAspect && pokemon.getFeature<FlagSpeciesFeature>(keys.first())?.enabled == true) {
            setOf(keys.first())
        } else {
            emptySet()
        }
    }

    override fun provide(properties: PokemonProperties): Set<String> {
        return if (isAspect && properties.customProperties.filterIsInstance<FlagSpeciesFeature>().find { it.name == keys.first() }?.enabled == true) {
            setOf(keys.first())
        } else {
            emptySet()
        }
    }
}