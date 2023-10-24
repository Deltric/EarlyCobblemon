/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.item.PokemonSelectingItem
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.battle.BagItem
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.item.Items

class BerryJuiceItem : CobblemonItem(Settings()), PokemonSelectingItem {
    override val bagItem = object : BagItem {
        override val itemName = "item.cobblemon.berry_juice"
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "potion 20"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) =  target.health < target.maxHealth && target.health > 0
    }

    override fun canUseOnPokemon(pokemon: Pokemon) = !pokemon.isFullHealth() && pokemon.currentHealth > 0
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (user is ServerPlayerEntity) {
            return use(user, user.getStackInHand(hand))
        }
        return TypedActionResult.success(user.getStackInHand(hand))
    }

    override fun applyToPokemon(
        player: ServerPlayerEntity,
        stack: ItemStack,
        pokemon: Pokemon
    ): TypedActionResult<ItemStack>? {
        if (pokemon.isFullHealth()) {
            return TypedActionResult.fail(stack)
        }
        pokemon.currentHealth = Integer.min(pokemon.currentHealth + 20, pokemon.hp)
        player.playSound(CobblemonSounds.BERRY_EAT, SoundCategory.PLAYERS, 1F, 1F)
        if (!player.isCreative)  {
            stack.decrement(1)
            val woodenBowlItemStack = ItemStack(Items.BOWL)
            if (!player.inventory.insertStack(woodenBowlItemStack)) {
                // Drop the item into the world if the inventory is full
                player.dropItem(woodenBowlItemStack, false)
            }
        }
        return TypedActionResult.success(stack)
    }

    override fun applyToBattlePokemon(player: ServerPlayerEntity, stack: ItemStack, battlePokemon: BattlePokemon) {
        super.applyToBattlePokemon(player, stack, battlePokemon)
        player.playSound(CobblemonSounds.BERRY_EAT, SoundCategory.PLAYERS, 1F, 1F)
        if (!player.isCreative)  {
            val woodenBowlItemStack = ItemStack(Items.BOWL)
            if (!player.inventory.insertStack(woodenBowlItemStack)) {
                // Drop the item into the world if the inventory is full
                player.dropItem(woodenBowlItemStack, false)
            }
        }
    }
}
