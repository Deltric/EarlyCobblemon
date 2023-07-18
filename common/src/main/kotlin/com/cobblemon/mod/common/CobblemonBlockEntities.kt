/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity
import com.cobblemon.mod.common.block.entity.PCBlockEntity
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.platform.PlatformRegistry
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.HangingSignBlockEntity
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object CobblemonBlockEntities : PlatformRegistry<Registry<BlockEntityType<*>>, RegistryKey<Registry<BlockEntityType<*>>>, BlockEntityType<*>>() {

    override val registry: Registry<BlockEntityType<*>> = Registries.BLOCK_ENTITY_TYPE
    override val registryKey: RegistryKey<Registry<BlockEntityType<*>>> = RegistryKeys.BLOCK_ENTITY_TYPE

    @JvmField
    val HEALING_MACHINE: BlockEntityType<HealingMachineBlockEntity> = this.create("healing_machine", BlockEntityType.Builder.create(::HealingMachineBlockEntity, CobblemonBlocks.HEALING_MACHINE).build(null))
    @JvmField
    val PC: BlockEntityType<PCBlockEntity> = this.create("pc", BlockEntityType.Builder.create(::PCBlockEntity, CobblemonBlocks.PC).build(null))
    @JvmField
    val PASTURE: BlockEntityType<PokemonPastureBlockEntity> = this.create("pasture", BlockEntityType.Builder.create(::PokemonPastureBlockEntity, CobblemonBlocks.PASTURE).build(null))
    @JvmField
    val APRICORN_SIGN: BlockEntityType<SignBlockEntity> = this.create("apricorn_sign", BlockEntityType.Builder.create(::SignBlockEntity, CobblemonBlocks.APRICORN_SIGN).build(null))
    @JvmField
    val APRICORN_HANGING_SIGN: BlockEntityType<HangingSignBlockEntity> = this.create("apricorn_hanging_sign", BlockEntityType.Builder.create(::HangingSignBlockEntity, CobblemonBlocks.APRICORN_HANGING_SIGN).build(null))

}