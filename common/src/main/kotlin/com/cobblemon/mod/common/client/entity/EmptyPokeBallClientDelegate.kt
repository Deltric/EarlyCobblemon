/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.api.entity.EntitySideDelegate
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.client.render.pokeball.PokeBallPoseableState
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity.CaptureState.NOT

class EmptyPokeBallClientDelegate : PokeBallPoseableState(), EntitySideDelegate<EmptyPokeBallEntity> {
    override val stateEmitter: SettableObservable<CaptureState> = SettableObservable(NOT)
    override val shakeEmitter = SimpleObservable<Unit>()

    lateinit var currentEntity: EmptyPokeBallEntity

    override fun getEntity() = currentEntity

    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks = partialTicks
    }

    override fun initialize(entity: EmptyPokeBallEntity) {
        this.currentEntity = entity
        age = entity.age
        initSubscriptions()
        entity.captureState.subscribe {
            stateEmitter.set(CaptureState.values()[it.toInt()])
        }
        entity.shakeEmitter.subscribe { shakeEmitter.emit(Unit) }
        this.runtime.environment.getQueryStruct().addFunctions(mapOf(
            "pokeball_type" to java.util.function.Function {
                return@Function DoubleValue(currentEntity.pokeBall.name.toString())
            }
        ))
    }

    override fun tick(entity: EmptyPokeBallEntity) {
        super.tick(entity)
        updateLocatorPosition(entity.pos)
        incrementAge(entity)
    }
}