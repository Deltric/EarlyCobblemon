/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.capabilities

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.api.riding.properties.MountLocation
import net.minecraft.util.Identifier

interface RidingCapability {

    val attributes: Map<Identifier, RidingAttribute>

    /**
     * Indicates whether this particular capability permits the ability for the entity to
     * interact with this particular environment. For instance, there might be a mount which
     * is capable of interacting with the rider on land and in the air, but should not allow
     * interaction on the water.
     *
     * @since 1.6.0
     */
    fun supports(location: MountLocation) : Boolean

    fun tick()

    fun attribute(identifier: Identifier): RidingAttribute? = this.attributes[identifier]

}