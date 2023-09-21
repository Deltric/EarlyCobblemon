/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import net.minecraft.predicate.NumberRange

/**
 * A type adapter for [NumberRange.DoubleRange].
 *
 * @author Licious
 * @since November 28th, 2022
 */
object DoubleNumberRangeAdapter : JsonDeserializer<NumberRange.DoubleRange>, JsonSerializer<NumberRange.DoubleRange> {
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): NumberRange.DoubleRange = NumberRange.DoubleRange.fromJson(element)
    override fun serialize(range: NumberRange.DoubleRange, type: Type, context: JsonSerializationContext): JsonElement = range.toJson()
}