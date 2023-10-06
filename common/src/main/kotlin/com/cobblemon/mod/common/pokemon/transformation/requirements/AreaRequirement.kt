/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.transformation.requirements

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.transformation.requirements.template.EntityQueryRequirement
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain area.
 *
 * @property box The [Box] expected to be in.
 *
 * @author Licious
 * @since March 21st, 2022
 */
class AreaRequirement(val box: Box = Box.from(Vec3d.ZERO)) : EntityQueryRequirement {
    companion object {
        const val ADAPTER_VARIANT = "area"
    }

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = box.contains(queriedEntity.pos)
}