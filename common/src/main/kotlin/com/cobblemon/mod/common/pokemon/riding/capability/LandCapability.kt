/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.riding.capability

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.api.riding.capabilities.RidingCapability
import com.cobblemon.mod.common.api.riding.properties.MountLocation
import net.minecraft.util.Identifier

class LandCapability(override val attributes: Map<Identifier, RidingAttribute>) : RidingCapability {
    override fun supports(location: MountLocation): Boolean {
        TODO("Not yet implemented")
    }

    override fun tick() {
        TODO("Not yet implemented")
    }
}