/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.google.gson.*
import com.mojang.serialization.JsonOps
import net.minecraft.predicate.NumberRange
import java.lang.reflect.Type

/**
 * A type adapter for [NumberRange.DoubleRange].
 *
 * @author Licious
 * @since November 28th, 2022
 */
object FloatNumberRangeAdapter : JsonDeserializer<NumberRange.DoubleRange>, JsonSerializer<NumberRange.DoubleRange> {
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): NumberRange.DoubleRange {
        return NumberRange.DoubleRange.CODEC.decode(JsonOps.INSTANCE, element).result().get().first
    }
    override fun serialize(range: NumberRange.DoubleRange, type: Type, context: JsonSerializationContext): JsonElement {
        return NumberRange.DoubleRange.CODEC.encode(range, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).result().get()
    }
}