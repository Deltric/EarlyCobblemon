/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.permission

import com.cablemc.pokemod.common.Pokemod
import net.minecraft.command.CommandSource
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Responsible for evaluating permissions for a given player or command source.
 * To register your implementation replace the instance in [Pokemod.permissionValidator].
 *
 * @author Licious
 * @since September 23rd, 2022
 */
interface PermissionValidator {

    /**
     * Invoked when the validator replaces the existing one in [Pokemod.permissionValidator].
     *
     */
    fun initialize()

    /**
     * Validates a permission for [ServerPlayerEntity].
     *
     * @param player The target [ServerPlayerEntity].
     * @param permission The literal permission being queried.
     * @return If the [player] has the [permission].
     */
    fun hasPermission(player: ServerPlayerEntity, permission: String): Boolean

    /**
     * Validates a permission for [CommandSource].
     *
     * @param source The target [CommandSource].
     * @param permission The literal permission being queried.
     * @return If the [source] has the [permission].
     */
    fun hasPermission(source: CommandSource, permission: String): Boolean

}