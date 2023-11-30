/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.grower

import com.cobblemon.mod.common.api.apricorn.Apricorn
import net.minecraft.block.SaplingGenerator
import java.util.Optional

class ApricornTreeGrower(private val apricorn: Apricorn) : SaplingGenerator(
    apricorn.asString(),
    Optional.empty(),
    Optional.of(apricorn.configuredFeature()),
    Optional.empty()
)