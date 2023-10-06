/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.transformation.progress

import com.cobblemon.mod.common.pokemon.transformation.progress.RecoilTransformationProgress
import com.cobblemon.mod.common.pokemon.transformation.progress.UseMoveTransformationProgress

object TransformationProgressFactory {

    private val variants = hashMapOf<String, () -> TransformationProgress<*>>()

    init {
        registerVariant(RecoilTransformationProgress.ID.toString()) { RecoilTransformationProgress() }
        registerVariant(UseMoveTransformationProgress.ID.toString()) { UseMoveTransformationProgress() }
    }

    fun registerVariant(variant: String, factory: () -> TransformationProgress<*>) {
        variants[variant] = factory
    }

    fun create(variant: String): TransformationProgress<*>? {
        val factory = variants[variant] ?: return null
        return factory.invoke()
    }

}