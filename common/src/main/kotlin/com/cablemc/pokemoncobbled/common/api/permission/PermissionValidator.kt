/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.permission

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.command.CommandSource
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Responsible for evaluating permissions for a given player or command source.
 * To register your implementation replace the instance in [PokemonCobbled.permissionValidator].
 *
 * @author Licious
 * @since September 23rd, 2022
 */
interface PermissionValidator {

    /**
     * Invoked when the validator replaces the existing one in [PokemonCobbled.permissionValidator].
     *
     */
    fun initiate()

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