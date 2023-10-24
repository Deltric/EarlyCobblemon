/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.EVs
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.status.PersistentStatus
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.isInt
import com.cobblemon.mod.common.util.splitMap
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlin.random.Random
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import net.minecraft.world.World

/**
 * A grouping of typical, selectable properties for a Pokémon. This is serializable
 * on its own and can be used to apply properties to a [Pokemon] or [PokemonEntity],
 * as well as check that a [Pokemon] or [PokemonEntity] matches all the defined
 * properties.
 *
 * Custom property types can be registered using [CustomPokemonProperty.register].
 *
 * Parsing a [PokemonProperties] from text is done using [PokemonProperties.parse]
 *
 * @author Hiroku
 * @since November 23rd, 2021
 */
open class PokemonProperties {
    companion object {
        fun parse(string: String, delimiter: String = " ", assigner: String = "="): PokemonProperties {
            val props = PokemonProperties()
            props.originalString = string
            val keyPairs = string.splitMap(delimiter, assigner)
            props.customProperties = CustomPokemonProperty.properties.mapNotNull { property ->
                val matchedKeyPair = keyPairs.find { it.first.lowercase() in property.keys }
                if (matchedKeyPair == null) {
                    if (!property.needsKey) {
                        var savedProperty: CustomPokemonProperty? = null
                        val keyPair = keyPairs.find { keyPair ->
                            savedProperty = property.fromString(keyPair.second)
                            return@find savedProperty != null
                        }
                        if (keyPair != null) {
                            keyPairs.remove(keyPair)
                        }
                        return@mapNotNull savedProperty
                    } else {
                        return@mapNotNull null
                    }
                } else {
                    keyPairs.remove(matchedKeyPair)
                    return@mapNotNull property.fromString(matchedKeyPair.second)
                }
            }.toMutableList()
            props.gender = Gender.values().toList().parsePropertyOfCollection(keyPairs, listOf("gender"), labelsOptional = true) { it.name.lowercase() }
            props.level = parseIntProperty(keyPairs, listOf("level", "lvl", "l"))?.coerceIn(1, Cobblemon.config.maxPokemonLevel)
            props.shiny = parseBooleanProperty(keyPairs, listOf("shiny", "s"))
            props.species = parseSpeciesIdentifier(keyPairs)
            props.form = parseForm(keyPairs)
            props.friendship = parseIntProperty(keyPairs, listOf("friendship"))?.coerceIn(0, Cobblemon.config.maxPokemonFriendship)
            props.pokeball = parseIdentifierOfRegistry(keyPairs, listOf("pokeball")) { identifier -> PokeBalls.getPokeBall(identifier)?.name?.toString() }
            props.nature = parseIdentifierOfRegistry(keyPairs, listOf("nature")) { identifier -> Natures.getNature(identifier)?.name?.toString() }
            props.ability = parseStringOfRegistry(keyPairs, listOf("ability")) { Abilities.get(it)?.name }
            props.status = parseStringOfRegistry(keyPairs, listOf("status")) { (Statuses.getStatus(it) ?: Statuses.getStatus(it.asIdentifierDefaultingNamespace()))?.showdownName }
            props.nickname = parseText(keyPairs, listOf("nickname", "nick"))
            props.teraType = parseStringOfRegistry(keyPairs, listOf("tera_type", "tera")) { ElementalTypes.get(it)?.name }
            props.dmaxLevel = parseIntProperty(keyPairs, listOf("dmax_level", "dmax"))?.coerceIn(0, Cobblemon.config.maxDynamaxLevel)
            props.gmaxFactor = parseBooleanProperty(keyPairs, listOf("gmax_factor", "gmax"))
            props.tradeable = parseBooleanProperty(keyPairs, listOf("tradeable", "tradable"))

            val maybeIVs = IVs()
            val maybeEVs = EVs()
            Stats.PERMANENT.forEach{ stat ->
                val statName = stat.toString().lowercase()
                parseIntProperty(keyPairs, listOf("${statName}_iv"))?.coerceIn(0, IVs.MAX_VALUE)?.let { maybeIVs[stat] = it }
                parseIntProperty(keyPairs, listOf("${statName}_ev"))?.coerceIn(0, EVs.MAX_STAT_VALUE)?.let { maybeEVs[stat] = it }
            }
            props.ivs = maybeIVs
            props.evs = maybeEVs

            props.updateAspects()
            return props
        }

        private fun getMatchedKeyPair(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): Pair<String, String?>? {
            return keyPairs.findLast { it.first in labels }
        }

        private fun parseText(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): MutableText? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels) ?: return null
            val value = matchingKeyPair.second
            return if (value.isNullOrBlank()) {
                null
            } else {
                Text.translatable(value)
            }
        }

        private fun parseIntProperty(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): Int? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels) ?: return null
            val value = matchingKeyPair.second
            return if (value == null || !value.isInt()) {
                null
            } else {
                value.toInt()
            }
        }

        private fun parseIdentifierOfRegistry(keyPairs: MutableList<Pair<String, String?>>, validKeys: List<String>, valueFetcher: (Identifier) -> String?): String? {
            val matched = getMatchedKeyPair(keyPairs, validKeys) ?: return null
            val value = matched.second?.lowercase() ?: return null
            return try {
                val identifier = value.asIdentifierDefaultingNamespace()
                valueFetcher(identifier)
            } catch (_: InvalidIdentifierException) {
                null
            }
        }

        private fun parseStringOfRegistry(keyPairs: MutableList<Pair<String, String?>>, validKeys: List<String>, valueFetcher: (String) -> String?): String? {
            val matched = getMatchedKeyPair(keyPairs, validKeys) ?: return null
            val value = matched.second?.lowercase() ?: return null
            return try {
                valueFetcher(value)
            } catch (_: InvalidIdentifierException) {
                null
            }
        }

        private fun parseSpeciesIdentifier(keyPairs: MutableList<Pair<String, String?>>): String? {
            fun cleanSpeciesName(string: String) = string.lowercase().replace("[^a-z0-9_:]".toRegex(), "")
            val matched = getMatchedKeyPair(keyPairs, listOf("species"))
            if (matched != null) {
                val value = matched.second?.let(::cleanSpeciesName) ?: return null
                return if (value.lowercase() == "random") {
                    "random"
                } else {
                    try {
                        val species = PokemonSpecies.getByIdentifier(value.asIdentifierDefaultingNamespace()) ?: return null
                        return if (species.resourceIdentifier.namespace == Cobblemon.MODID) species.resourceIdentifier.path else species.resourceIdentifier.toString()
                    } catch (e: InvalidIdentifierException) {
                        return null
                    }
                }
            } else {
                var species: String? = null

                val keyPair = keyPairs.find { pair ->
                    species = if (pair.second == null && pair.first.lowercase() == "random") {
                        "random"
                    } else {
                        try {
                            val identifier = cleanSpeciesName(pair.first).asIdentifierDefaultingNamespace()
                            val found = PokemonSpecies.getByIdentifier(identifier) ?: return@find false
                            if (found.resourceIdentifier.namespace == Cobblemon.MODID) found.resourceIdentifier.path else found.resourceIdentifier.toString()
                        } catch (e: InvalidIdentifierException) {
                            return@find false
                        }
                    }
                    return@find species != null
                }

                if (keyPair != null) {
                    keyPairs.remove(keyPair)
                }

                return species
            }
        }

        private fun parseForm(keyPairs: MutableList<Pair<String, String?>>): String? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, listOf("form")) ?: return null
            keyPairs.remove(matchingKeyPair)
            return matchingKeyPair.second
        }

        private fun parseBooleanProperty(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): Boolean? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels) ?: return null
            keyPairs.remove(matchingKeyPair)
            return when (matchingKeyPair.second?.lowercase()) {
                null -> true
                "true", "yes" -> true
                "false", "no" -> false
                else -> null
            }
        }

        private fun <T> Iterable<T>.parsePropertyOfCollection(
            keyPairs: MutableList<Pair<String, String?>>,
            labels: Iterable<String>,
            labelsOptional: Boolean = false,
            stringer: (T) -> String
        ): T? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels)
            if (matchingKeyPair != null) {
                val value = matchingKeyPair.second?.lowercase()
                return if (value == null) {
                    null
                } else {
                    val matched = find { stringer(it).lowercase() == value }
                    if (matched != null) {
                        keyPairs.remove(matchingKeyPair)
                    }
                    matched
                }
            } else if (labelsOptional) {
                val keyPair = keyPairs.firstOrNull { pair -> any { stringer(it).lowercase() == pair.first } }
                if (keyPair != null) {
                    keyPairs.remove(keyPair)
                    return first { stringer(it).lowercase() == keyPair.first }
                }
            }
            return null
        }
    }

    var originalString: String = ""

    var species: String? = null
    var nickname: MutableText? = null
    var form: String? = null
    var shiny: Boolean? = null
    var gender: Gender? = null
    var level: Int? = null
    var friendship: Int? = null
    var pokeball: String? = null
    var nature: String? = null
    var ability: String? = null
    var aspects: Set<String> = emptySet()
    var status: String? = null
    var teraType: String? = null
    var dmaxLevel: Int? = null
    var gmaxFactor: Boolean? = null
    var tradeable: Boolean? = null

    var ivs: IVs? = null
    var evs: EVs? = null
    var customProperties = mutableListOf<CustomPokemonProperty>()

    fun asRenderablePokemon() = RenderablePokemon(
        species = species?.let {
            return@let try {
                PokemonSpecies.getByIdentifier(it.asIdentifierDefaultingNamespace())
            } catch (e: InvalidIdentifierException) {
                PokemonSpecies.random()
            }
        } ?: PokemonSpecies.random(),
        aspects = aspects
    )

    fun apply(pokemon: Pokemon) {
        species?.let {
            return@let try {
                if (it == "random") {
                    PokemonSpecies.implemented.random()
                } else {
                    PokemonSpecies.getByIdentifier(it.asIdentifierDefaultingNamespace())
                }
            } catch (e: InvalidIdentifierException) {
                null
            }
        }?.let { pokemon.species = it }
        nickname?.let { pokemon.nickname = it }
        form?.let { formID -> pokemon.species.forms.firstOrNull { it.formOnlyShowdownId().equals(formID, true) } }?.let { form -> pokemon.form = form }
        shiny?.let { pokemon.shiny = it }
        gender?.let { pokemon.gender = it }
        level?.let { pokemon.level = it }
        friendship?.let { pokemon.setFriendship(it) }
        pokeball?.let { PokeBalls.getPokeBall(it.asIdentifierDefaultingNamespace())?.let { pokeball -> pokemon.caughtBall = pokeball } }
        nature?.let  { Natures.getNature(it.asIdentifierDefaultingNamespace())?.let { nature -> pokemon.nature = nature } }
        ability?.let { Abilities.getOrException(it).create(true).let { ability -> pokemon.ability = ability } }
        status?.let { Statuses.getStatus(it)?.let { pokemon.applyStatus(it as PersistentStatus) } }
        customProperties.forEach { it.apply(pokemon) }
        ivs?.let { ivs ->
            ivs.forEach { stat ->
                pokemon.setIV(stat.key, stat.value)
            }
        }
        evs?.let { evs ->
            evs.forEach { stat ->
                pokemon.setEV(stat.key, stat.value)
            }
        }
        teraType?.let { ElementalTypes.get(it)?.let { type -> pokemon.teraType = type } }
        dmaxLevel?.let { pokemon.dmaxLevel = it }
        gmaxFactor?.let { pokemon.gmaxFactor = it }
        tradeable?.let { pokemon.tradeable = it }
        pokemon.updateAspects()
    }

    fun apply(pokemonEntity: PokemonEntity) {
        species?.let {
            return@let try {
                if (it == "random") {
                    PokemonSpecies.species.random()
                } else {
                    PokemonSpecies.getByIdentifier(it.asIdentifierDefaultingNamespace())
                }
            } catch (e: InvalidIdentifierException) {
                null
            }
        }?.let { pokemonEntity.pokemon.species = it }
        nickname?.let { pokemonEntity.pokemon.nickname = it }
        form?.let { formID -> pokemonEntity.pokemon.species.forms.firstOrNull { it.formOnlyShowdownId().equals(formID, true) } }?.let { form -> pokemonEntity.pokemon.form = form }
        level?.let { pokemonEntity.pokemon.level = it }
        shiny?.let { pokemonEntity.pokemon.shiny = it }
        gender?.let { pokemonEntity.pokemon.gender = it }
        friendship?.let { pokemonEntity.pokemon.setFriendship(it) }
        pokeball?.let { PokeBalls.getPokeBall(it.asIdentifierDefaultingNamespace())?.let { pokeball -> pokemonEntity.pokemon.caughtBall = pokeball } }
        nature?.let { Natures.getNature(it.asIdentifierDefaultingNamespace())?.let { nature -> pokemonEntity.pokemon.nature = nature } }
        ability?.let { Abilities.getOrException(it).create(true).let { ability -> pokemonEntity.pokemon.ability = ability } }
        status?.let { Statuses.getStatus(it)?.let { pokemonEntity.pokemon.applyStatus(it as PersistentStatus) } }
        customProperties.forEach { it.apply(pokemonEntity) }
        ivs?.let { ivs ->
            ivs.forEach { stat ->
                pokemonEntity.pokemon.setIV(stat.key, stat.value)
            }
        }
        evs?.let { evs ->
            evs.forEach { stat ->
                pokemonEntity.pokemon.setEV(stat.key, stat.value)
            }
        }
        teraType?.let { ElementalTypes.get(it)?.let { type -> pokemonEntity.pokemon.teraType = type } }
        dmaxLevel?.let { pokemonEntity.pokemon.dmaxLevel = it }
        gmaxFactor?.let { pokemonEntity.pokemon.gmaxFactor = it }
        tradeable?.let { pokemonEntity.pokemon.tradeable = it }
        pokemonEntity.pokemon.updateAspects()
    }

    fun matches(pokemon: Pokemon): Boolean {
        level?.takeIf { it != pokemon.level }?.let { return false }
        shiny?.takeIf { it != pokemon.shiny }?.let { return false }
        gender?.takeIf { it != pokemon.gender }?.let { return false }
        species?.run {
            try {
                val species = if (this == "random") {
                    PokemonSpecies.species.random()
                } else {
                    PokemonSpecies.getByIdentifier(this.asIdentifierDefaultingNamespace()) ?: return@run
                }
                if (pokemon.species != species) {
                    return false
                }
            } catch (e: InvalidIdentifierException) {
                return false
            }
        }
        nickname?.takeIf { it != pokemon.nickname }?.let { return false }
        form?.takeIf { !it.equals(pokemon.form.name, true) }?.let { return false }
        friendship?.takeIf { it != pokemon.friendship }?.let { return false }
        pokeball?.takeIf { it != pokemon.caughtBall.name.toString() }?.let { return false }
        nature?.takeIf { it != pokemon.nature.name.toString() }?.let { return false }
        ability?.takeIf { it != pokemon.ability.name }?.let { return false }
        status?.takeIf { it != pokemon.status?.status?.showdownName }?.let { return false }
        ivs?.forEach{ stat ->
            if (stat.value != pokemon.ivs[stat.key]) { return false }
        }
        evs?.forEach{ stat ->
            if (stat.value != pokemon.evs[stat.key]) { return false }
        }
        teraType?.takeIf { it != pokemon.teraType.name }?.let { return false }
        dmaxLevel?.takeIf { it != pokemon.dmaxLevel }?.let { return false }
        gmaxFactor?.takeIf { it != pokemon.gmaxFactor }?.let { return false }
        tradeable?.takeIf { it != pokemon.tradeable }?.let { return false }
        return customProperties.none { !it.matches(pokemon) }
    }

    fun matches(pokemonEntity: PokemonEntity): Boolean {
        level?.takeIf { it != pokemonEntity.pokemon.level }?.let { return false }
        shiny?.takeIf { it != ("shiny" in pokemonEntity.pokemon.aspects) }?.let { return false }
        gender?.takeIf { it != pokemonEntity.pokemon.gender }?.let { return false }
        species?.run {
            try {
                val species = if (this == "random") {
                    PokemonSpecies.species.random()
                } else {
                    PokemonSpecies.getByIdentifier(this.asIdentifierDefaultingNamespace()) ?: return@run
                }
                if (pokemonEntity.pokemon.species != species) {
                    return false
                }
            } catch (e: InvalidIdentifierException) {}
        }
        nickname?.takeIf { it != pokemonEntity.pokemon.nickname }?.let { return false }
        form?.takeIf { !it.equals(pokemonEntity.pokemon.form.name, true) }?.let { return false }
        friendship?.takeIf { it != pokemonEntity.pokemon.friendship }?.let { return false }
        pokeball?.takeIf { it != pokemonEntity.pokemon.caughtBall.name.toString() }?.let { return false }
        nature?.takeIf { it != pokemonEntity.pokemon.nature.name.toString() }?.let { return false }
        ability?.takeIf { it != pokemonEntity.pokemon.ability.name }?.let { return false }
        status?.takeIf { it != pokemonEntity.pokemon.status?.status?.showdownName }?.let { return false }
        ivs?.forEach{ stat ->
            if (stat.value != pokemonEntity.pokemon.ivs[stat.key]) { return false }
        }
        evs?.forEach{ stat ->
            if (stat.value != pokemonEntity.pokemon.evs[stat.key]) { return false }
        }
        teraType?.takeIf { it != pokemonEntity.pokemon.teraType.name }?.let { return false }
        dmaxLevel?.takeIf { it != pokemonEntity.pokemon.dmaxLevel }?.let { return false }
        gmaxFactor?.takeIf { it != pokemonEntity.pokemon.gmaxFactor }?.let { return false }
        tradeable?.takeIf { it != pokemonEntity.pokemon.tradeable }?.let { return false }
        return customProperties.none { !it.matches(pokemonEntity) }
    }

    fun isSubSetOf(properties: PokemonProperties): Boolean {
        level?.takeIf { it != properties.level }?.let { return false }
        shiny?.takeIf { it != ("shiny" in properties.aspects) }?.let { return false }
        gender?.takeIf { it != properties.gender }?.let { return false }
        species?.run {
            try {
                val species = if (this == "random") {
                    PokemonSpecies.species.random()
                } else {
                    PokemonSpecies.getByIdentifier(this.asIdentifierDefaultingNamespace()) ?: return@run
                }
                if (properties.species != species.toString()) {
                    return false
                }
            } catch (e: InvalidIdentifierException) {}
        }
        nickname?.takeIf { it != properties.nickname }?.let { return false }
        form?.takeIf { !it.equals(properties.form, true) }?.let { return false }
        friendship?.takeIf { it != properties.friendship }?.let { return false }
        pokeball?.takeIf { it != properties.pokeball }?.let { return false }
        nature?.takeIf { it != properties.nature }?.let { return false }
        ability?.takeIf { it != properties.ability }?.let { return false }
        status?.takeIf { it != properties.status }?.let { return false }
        ivs?.let{ ivs ->
            ivs.forEach{ stat ->
                //If the potential subset has IV and the main set does not then it cant be a subset
                val propertiesIVs = properties.ivs ?: return false
                if (stat.value != propertiesIVs[stat.key]) { return false }
            }
        }
        evs?.let{ evs ->
            evs.forEach{ stat ->
                //If the potential subset has EV and the main set does not then it cant be a subset
                val propertiesEVs = properties.evs ?: return false
                if (stat.value != propertiesEVs[stat.key]) { return false }
            }
        }
        teraType?.takeIf { it != properties.teraType }?.let { return false }
        dmaxLevel?.takeIf { it != properties.dmaxLevel }?.let { return false }
        gmaxFactor?.takeIf { it != properties.gmaxFactor }?.let { return false }
        tradeable?.takeIf { it != properties.tradeable }?.let { return false }
        return true
    }

    fun create(): Pokemon {
        val pokemon = Pokemon()
        apply(pokemon)
        pokemon.initialize()
        roll(pokemon)
        return pokemon
    }

    // TEST YOUR LUCK!
    fun roll(pokemon: Pokemon) {
        val baseTypes = pokemon.species.types.toList()
        if (this.shiny == null) pokemon.shiny = Cobblemon.config.shinyRate.checkRate()
        if (this.teraType == null) pokemon.teraType =
            if (Cobblemon.config.teraTypeRate.checkRate()) ElementalTypes.all().filter { !baseTypes.contains(it) }.random()
            else baseTypes.random()
    }

    // If the config value is at least 1, then do 1/x and use that as the property chance
    private fun Float.checkRate(): Boolean = this >= 1 && (Random.Default.nextFloat() < 1 / this)

    fun createEntity(world: World): PokemonEntity {
        return PokemonEntity(world, create())
    }

    fun saveToNBT(): NbtCompound {
        val nbt = NbtCompound()
        originalString.let { nbt.putString(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT, it) }
        level?.let { nbt.putInt(DataKeys.POKEMON_LEVEL, it) }
        shiny?.let { nbt.putBoolean(DataKeys.POKEMON_SHINY, it) }
        gender?.let { nbt.putString(DataKeys.POKEMON_GENDER, it.name) }
        species?.let { nbt.putString(DataKeys.POKEMON_SPECIES_TEXT, it) }
        nickname?.let { nbt.putString(DataKeys.POKEMON_NICKNAME, Text.Serializer.toJson(it)) }
        form?.let { nbt.putString(DataKeys.POKEMON_FORM_ID, it) }
        friendship?.let { nbt.putInt(DataKeys.POKEMON_FRIENDSHIP, it) }
        pokeball?.let { nbt.putString(DataKeys.POKEMON_CAUGHT_BALL, it) }
        nature?.let { nbt.putString(DataKeys.POKEMON_NATURE, it) }
        ability?.let { nbt.putString(DataKeys.POKEMON_ABILITY, it) }
        status?.let { nbt.putString(DataKeys.POKEMON_STATUS_NAME, it) }
        ivs?.let { nbt.put(DataKeys.POKEMON_IVS, it.saveToNBT(NbtCompound())) }
        evs?.let { nbt.put(DataKeys.POKEMON_EVS, it.saveToNBT(NbtCompound())) }
        teraType?.let { nbt.putString(DataKeys.POKEMON_TERA_TYPE, it) }
        dmaxLevel?.let { nbt.putInt(DataKeys.POKEMON_DMAX_LEVEL, it) }
        gmaxFactor?.let { nbt.putBoolean(DataKeys.POKEMON_GMAX_FACTOR, it) }
        tradeable?.let { nbt.putBoolean(DataKeys.POKEMON_TRADEABLE, it) }
        val custom = NbtList()
        customProperties.map { NbtString.of(it.asString()) }.forEach { custom.add(it) }
        nbt.put(DataKeys.POKEMON_PROPERTIES_CUSTOM, custom)
        return nbt
    }

    fun loadFromNBT(tag: NbtCompound): PokemonProperties {
        originalString = tag.getString(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT)
        level = if (tag.contains(DataKeys.POKEMON_LEVEL)) tag.getInt(DataKeys.POKEMON_LEVEL) else null
        shiny = if (tag.contains(DataKeys.POKEMON_SHINY)) tag.getBoolean(DataKeys.POKEMON_SHINY) else null
        gender = if (tag.contains(DataKeys.POKEMON_GENDER)) Gender.valueOf(tag.getString(DataKeys.POKEMON_GENDER)) else null
        species = if (tag.contains(DataKeys.POKEMON_SPECIES_TEXT)) tag.getString(DataKeys.POKEMON_SPECIES_TEXT) else null
        nickname = if (tag.contains(DataKeys.POKEMON_NICKNAME)) Text.Serializer.fromJson(tag.getString(DataKeys.POKEMON_NICKNAME)) else null
        form = if (tag.contains(DataKeys.POKEMON_FORM_ID)) tag.getString(DataKeys.POKEMON_FORM_ID) else null
        friendship = if (tag.contains(DataKeys.POKEMON_FRIENDSHIP)) tag.getInt(DataKeys.POKEMON_FRIENDSHIP) else null
        pokeball = if (tag.contains(DataKeys.POKEMON_CAUGHT_BALL)) tag.getString(DataKeys.POKEMON_CAUGHT_BALL) else null
        nature = if (tag.contains(DataKeys.POKEMON_NATURE)) tag.getString(DataKeys.POKEMON_NATURE) else null
        ability = if (tag.contains(DataKeys.POKEMON_ABILITY)) tag.getString(DataKeys.POKEMON_ABILITY) else null
        status = if (tag.contains(DataKeys.POKEMON_STATUS_NAME)) tag.getString(DataKeys.POKEMON_STATUS_NAME) else null
        ivs = if (tag.contains(DataKeys.POKEMON_IVS)) ivs?.loadFromNBT(tag.getCompound(DataKeys.POKEMON_IVS)) as IVs? else null
        evs = if (tag.contains(DataKeys.POKEMON_EVS)) evs?.loadFromNBT(tag.getCompound(DataKeys.POKEMON_EVS)) as EVs? else null
        teraType = if (tag.contains(DataKeys.POKEMON_TERA_TYPE)) tag.getString(DataKeys.POKEMON_TERA_TYPE) else null
        dmaxLevel = if (tag.contains(DataKeys.POKEMON_DMAX_LEVEL)) tag.getInt(DataKeys.POKEMON_DMAX_LEVEL) else null
        gmaxFactor = if (tag.contains(DataKeys.POKEMON_GMAX_FACTOR)) tag.getBoolean(DataKeys.POKEMON_GMAX_FACTOR) else null
        tradeable = if (tag.contains(DataKeys.POKEMON_TRADEABLE)) tag.getBoolean(DataKeys.POKEMON_TRADEABLE) else null
        val custom = tag.getList(DataKeys.POKEMON_PROPERTIES_CUSTOM, NbtElement.STRING_TYPE.toInt())
        // This is kinda gross
        custom.forEach { customProperties.addAll(parse(it.asString()).customProperties) }
        updateAspects()
        return this
    }

    fun saveToJSON(): JsonObject {
        val json = JsonObject()
        originalString.let { json.addProperty(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT, it) }
        level?.let { json.addProperty(DataKeys.POKEMON_LEVEL, it) }
        shiny?.let { json.addProperty(DataKeys.POKEMON_SHINY, it) }
        gender?.let { json.addProperty(DataKeys.POKEMON_GENDER, it.name) }
        species?.let { json.addProperty(DataKeys.POKEMON_SPECIES_TEXT, it) }
        nickname?.let { json.addProperty(DataKeys.POKEMON_NICKNAME, Text.Serializer.toJson(it)) }
        form?.let { json.addProperty(DataKeys.POKEMON_FORM_ID, it) }
        friendship?.let { json.addProperty(DataKeys.POKEMON_FRIENDSHIP, it) }
        pokeball?.let { json.addProperty(DataKeys.POKEMON_CAUGHT_BALL, it) }
        nature?.let { json.addProperty(DataKeys.POKEMON_NATURE, it) }
        ability?.let { json.addProperty(DataKeys.POKEMON_ABILITY, it) }
        status?.let { json.addProperty(DataKeys.POKEMON_STATUS_NAME, it) }
        ivs?.let { json.add(DataKeys.POKEMON_IVS, it.saveToJSON(JsonObject())) }
        evs?.let { json.add(DataKeys.POKEMON_EVS, it.saveToJSON(JsonObject())) }
        teraType?.let { json.addProperty(DataKeys.POKEMON_TERA_TYPE, it) }
        dmaxLevel?.let { json.addProperty(DataKeys.POKEMON_DMAX_LEVEL, it) }
        gmaxFactor?.let { json.addProperty(DataKeys.POKEMON_GMAX_FACTOR, it) }
        tradeable?.let { json.addProperty(DataKeys.POKEMON_TRADEABLE, it) }
        val custom = JsonArray()
        customProperties.map { it.asString() }.forEach { custom.add(it) }
        json.add(DataKeys.POKEMON_PROPERTIES_CUSTOM, custom)

        return json
    }

    fun loadFromJSON(json: JsonObject): PokemonProperties {
        originalString = json.get(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT)?.asString ?: ""
        level = json.get(DataKeys.POKEMON_LEVEL)?.asInt
        shiny = json.get(DataKeys.POKEMON_SHINY)?.asBoolean
        gender = json.get(DataKeys.POKEMON_GENDER)?.asString?.let { Gender.valueOf(it) }
        species = json.get(DataKeys.POKEMON_SPECIES_TEXT)?.asString
        nickname = json.get(DataKeys.POKEMON_NICKNAME)?.asString?.let { Text.Serializer.fromJson(it) }
        form = json.get(DataKeys.POKEMON_FORM_ID)?.asString
        friendship = json.get(DataKeys.POKEMON_FRIENDSHIP)?.asInt
        pokeball = json.get(DataKeys.POKEMON_CAUGHT_BALL)?.asString
        nature = json.get(DataKeys.POKEMON_NATURE)?.asString
        ability = json.get(DataKeys.POKEMON_ABILITY)?.asString
        status = json.get(DataKeys.POKEMON_STATUS_NAME)?.asString
        ivs?.loadFromJSON(json.getAsJsonObject(DataKeys.POKEMON_IVS))
        evs?.loadFromJSON(json.getAsJsonObject(DataKeys.POKEMON_EVS))
        teraType = json.get(DataKeys.POKEMON_TERA_TYPE)?.asString
        dmaxLevel = json.get(DataKeys.POKEMON_DMAX_LEVEL)?.asInt
        gmaxFactor = json.get(DataKeys.POKEMON_GMAX_FACTOR)?.asBoolean
        tradeable = json.get(DataKeys.POKEMON_TRADEABLE)?.asBoolean
        val custom = json.get(DataKeys.POKEMON_PROPERTIES_CUSTOM)?.asJsonArray
        // This is still kinda gross
        custom?.forEach { customProperties.addAll(parse(it.asString).customProperties) }
        updateAspects()
        return this
    }

    fun asString(separator: String = " "): String {
        val pieces = mutableListOf<String>()
        species?.let { pieces.add(it) }
        nickname?.let { pieces.add("nickname=$${it.string}") }
        form?.let { pieces.add("form=$it") }
        level?.let { pieces.add("level=$it") }
        shiny?.let { pieces.add("shiny=$it") }
        gender?.let { pieces.add("gender=$it")}
        friendship?.let { pieces.add("friendship=$it") }
        pokeball?.let { pieces.add("pokeball=$it") }
        nature?.let { pieces.add("nature=$it") }
        ability?.let { pieces.add("ability=$it") }
        status?.let { pieces.add("status=$it") }
        ivs?.forEach{ stat ->
            pieces.add("${stat.key}_iv=${stat.value}")
        }
        evs?.forEach{ stat ->
            pieces.add("${stat.key}_ev=${stat.value}")
        }
        teraType?.let { pieces.add("tera_type=$it") }
        dmaxLevel?.let { pieces.add("dmax_level=$it") }
        gmaxFactor?.let { pieces.add("gmax_factor=$it") }
        tradeable?.let { pieces.add("tradeable=$it") }
        customProperties.forEach { pieces.add(it.asString()) }
        return pieces.joinToString(separator)
    }

    fun updateAspects() {
        val aspects = mutableSetOf<String>()
        AspectProvider.providers.forEach { aspects.addAll(it.provide(this)) }
        this.aspects = aspects.toSet()
    }

    fun copy(): PokemonProperties {
        return PokemonProperties().loadFromJSON(saveToJSON())
    }
}
