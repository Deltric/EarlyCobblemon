/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.command.argument

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.spawning.SpawnBucket
import com.cablemc.pokemod.common.util.lang
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.command.CommandSource

class SpawnBucketArgumentType: ArgumentType<SpawnBucket> {

    companion object {
        val EXAMPLES: List<String> = listOf(Pokemod.bestSpawner.config.buckets.first().name)
        val INVALID_BUCKET = lang("command.checkspawns.invalid-bucket")

        fun spawnBucket() = SpawnBucketArgumentType()

        fun <S> getSpawnBucket(context: CommandContext<S>, name: String): SpawnBucket {
            return context.getArgument(name, SpawnBucket::class.java)
        }
    }

    override fun parse(reader: StringReader): SpawnBucket {
        val name = reader.readString()
        return Pokemod.bestSpawner.config.buckets.find { it.name.equals(name, ignoreCase = true) }
            ?: throw SimpleCommandExceptionType(INVALID_BUCKET).createWithContext(reader)
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(Pokemod.bestSpawner.config.buckets.map { it.name }, builder)
    }

    override fun getExamples() = EXAMPLES
}