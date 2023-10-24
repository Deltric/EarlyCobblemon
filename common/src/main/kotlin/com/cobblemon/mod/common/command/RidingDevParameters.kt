/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object RidingDevParameters {

    var speed = 2F
    var acceleration = 3F
    var weight = 50F

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("riding")
            .requires { it.hasPermissionLevel(4) }
            .then(CommandManager.literal("speed").then(CommandManager.argument("value", FloatArgumentType.floatArg()).executes { updateSpeed(it) }))
            .then(CommandManager.literal("acceleration").then(CommandManager.argument("value", FloatArgumentType.floatArg()).executes { updateAcceleration(it) }))
            .then(CommandManager.literal("weight").then(CommandManager.argument("value", FloatArgumentType.floatArg()).executes { updateWeight(it) }))

        dispatcher.register(command)
    }

    private fun updateSpeed(context: CommandContext<ServerCommandSource>) : Int {
        this.speed = FloatArgumentType.getFloat(context, "value")
        return Command.SINGLE_SUCCESS
    }

    private fun updateAcceleration(context: CommandContext<ServerCommandSource>) : Int {
        this.acceleration = FloatArgumentType.getFloat(context, "value")
        return Command.SINGLE_SUCCESS
    }

    private fun updateWeight(context: CommandContext<ServerCommandSource>) : Int {
        this.weight = FloatArgumentType.getFloat(context, "value")
        return Command.SINGLE_SUCCESS
    }

}