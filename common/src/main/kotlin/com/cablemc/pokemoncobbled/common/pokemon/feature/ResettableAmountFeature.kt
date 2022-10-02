package com.cablemc.pokemoncobbled.common.pokemon.feature

import com.cablemc.pokemoncobbled.common.api.pokemon.feature.SpeciesFeature
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound

/**
 * A basic feature that serves as a counter starting at 0.
 *
 * @author Licious
 * @since October 2nd, 2022
 */
abstract class ResettableAmountFeature : SpeciesFeature {

    /**
     * The current amount of damage taken since the last faint or healed.
     */
    var currentValue = 0
        set(value) {
            if (value < DEFAULT_VALUE) {
                throw IllegalArgumentException("You cannot set the ${this.name} feature value below 0")
            }
            field = value
        }

    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        pokemonNBT.putInt(this.name, this.currentValue)
        return pokemonNBT
    }

    override fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature = this.createInstance(pokemonNBT.getInt(this.name))

    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        pokemonJSON.addProperty(this.name, this.currentValue)
        return pokemonJSON
    }

    override fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature = this.createInstance(pokemonJSON.get(this.name).asInt)

    /**
     * Resets the [currentValue] to 0.
     */
    fun reset() {
        this.currentValue = DEFAULT_VALUE
    }

    protected abstract fun createInstance(value: Int): SpeciesFeature

    companion object {
        private const val DEFAULT_VALUE = 0
    }

}