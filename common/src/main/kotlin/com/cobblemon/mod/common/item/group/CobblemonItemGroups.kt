/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.group

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroup.*
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@Suppress("unused", "UNUSED_PARAMETER")
object CobblemonItemGroups {

    // See https://docs.google.com/spreadsheets/d/1QgaIlW-S9A-Blqhc-5G7OO3igaQdEAiYQqVEPnEBFmc/edit#gid=978365418 for what goes what

    private val ALL = arrayListOf<ItemGroupHolder>()
    private val INJECTORS = arrayListOf<ItemGroupInjector>()

    @JvmStatic val BLOCKS_KEY = this.create("blocks", this::blockEntries) { ItemStack(CobblemonItems.PC) }
    @JvmStatic val POKEBALLS_KEY = this.create("pokeball", this::pokeballentries) { ItemStack(CobblemonItems.POKE_BALL) }
    @JvmStatic val AGRICULTURE_KEY = this.create("agriculture", this::agricultureEntries) { ItemStack(CobblemonItems.MEDICINAL_LEEK) }
    @JvmStatic val CONSUMABLES_KEY = this.create("consumables", this::consumableEntries) { ItemStack(CobblemonItems.ROASTED_LEEK) }
    @JvmStatic val HELD_ITEMS_KEY = this.create("held_item", this::heldItemEntries) { ItemStack(CobblemonItems.EXP_SHARE) }
    @JvmStatic val EVOLUTION_ITEMS_KEY = this.create("evolution_item", this::evolutionItemEntries) { ItemStack(CobblemonItems.BLACK_AUGURITE) }

    @JvmStatic val BLOCKS get() = Registries.ITEM_GROUP.get(BLOCKS_KEY)
    @JvmStatic val POKEBALLS get() = Registries.ITEM_GROUP.get(POKEBALLS_KEY)
    @JvmStatic val AGRICULTURE get() = Registries.ITEM_GROUP.get(AGRICULTURE_KEY)
    @JvmStatic val CONSUMABLES get() = Registries.ITEM_GROUP.get(CONSUMABLES_KEY)
    @JvmStatic val HELD_ITEMS get() = Registries.ITEM_GROUP.get(HELD_ITEMS_KEY)
    @JvmStatic val EVOLUTION_ITEMS get() = Registries.ITEM_GROUP.get(EVOLUTION_ITEMS_KEY)

    @JvmStatic val FOOD_INJECTIONS = this.inject(RegistryKey.of(Registries.ITEM_GROUP.key, Identifier("food_and_drinks")), this::foodInjections)

    fun register(consumer: (holder: ItemGroupHolder) -> ItemGroup) {
        ALL.forEach(consumer::invoke)
    }

    fun inject(consumer: (injector: ItemGroupInjector) -> Unit) {
        INJECTORS.forEach(consumer)
    }

    data class ItemGroupHolder(
        val key: RegistryKey<ItemGroup>,
        val displayIconProvider: () -> ItemStack,
        val entryCollector: EntryCollector,
        val displayName: Text = Text.translatable("itemGroup.${key.value.namespace}.${key.value.path}")
    )

    data class ItemGroupInjector(
        val key: RegistryKey<ItemGroup>,
        val entryInjector: (displayContext: DisplayContext) -> List<Item>,
    )

    private fun create(name: String, entryCollector: EntryCollector, displayIconProvider: () -> ItemStack): RegistryKey<ItemGroup> {
        val key = RegistryKey.of(Registries.ITEM_GROUP.key, cobblemonResource(name))
        this.ALL += ItemGroupHolder(key, displayIconProvider, entryCollector)
        return key
    }

    private fun inject(key: RegistryKey<ItemGroup>, entryInjector: (displayContext: DisplayContext) -> List<Item>): ItemGroupInjector {
        val injector = ItemGroupInjector(key, entryInjector)
        this.INJECTORS += injector
        return injector
    }
    
    private fun agricultureEntries(displayContext: DisplayContext, entries: Entries) {
        entries.add(CobblemonItems.MEDICINAL_LEEK)
        entries.add(CobblemonItems.BIG_ROOT)
        entries.add(CobblemonItems.ENERGY_ROOT)
        entries.add(CobblemonItems.REVIVAL_HERB)
        entries.add(CobblemonItems.PEP_UP_FLOWER)
        entries.add(CobblemonItems.MENTAL_HERB)
        entries.add(CobblemonItems.POWER_HERB)
        entries.add(CobblemonItems.WHITE_HERB)
        entries.add(CobblemonItems.MIRROR_HERB)
        entries.add(CobblemonItems.VIVICHOKE)
        entries.add(CobblemonItems.VIVICHOKE_SEEDS)

        entries.add(CobblemonItems.RED_APRICORN)
        entries.add(CobblemonItems.YELLOW_APRICORN)
        entries.add(CobblemonItems.GREEN_APRICORN)
        entries.add(CobblemonItems.BLUE_APRICORN)
        entries.add(CobblemonItems.PINK_APRICORN)
        entries.add(CobblemonItems.BLACK_APRICORN)
        entries.add(CobblemonItems.WHITE_APRICORN)
        entries.add(CobblemonItems.RED_APRICORN_SEED)
        entries.add(CobblemonItems.YELLOW_APRICORN_SEED)
        entries.add(CobblemonItems.GREEN_APRICORN_SEED)
        entries.add(CobblemonItems.BLUE_APRICORN_SEED)
        entries.add(CobblemonItems.PINK_APRICORN_SEED)
        entries.add(CobblemonItems.BLACK_APRICORN_SEED)
        entries.add(CobblemonItems.WHITE_APRICORN_SEED)

        entries.add(CobblemonItems.RED_MINT_SEEDS)
        entries.add(CobblemonItems.RED_MINT_LEAF)
        entries.add(CobblemonItems.BLUE_MINT_SEEDS)
        entries.add(CobblemonItems.BLUE_MINT_LEAF)
        entries.add(CobblemonItems.CYAN_MINT_SEEDS)
        entries.add(CobblemonItems.CYAN_MINT_LEAF)
        entries.add(CobblemonItems.PINK_MINT_SEEDS)
        entries.add(CobblemonItems.PINK_MINT_LEAF)
        entries.add(CobblemonItems.GREEN_MINT_SEEDS)
        entries.add(CobblemonItems.GREEN_MINT_LEAF)
        entries.add(CobblemonItems.WHITE_MINT_SEEDS)
        entries.add(CobblemonItems.WHITE_MINT_LEAF)

        entries.add(CobblemonItems.GROWTH_MULCH)
        entries.add(CobblemonItems.RICH_MULCH)
        entries.add(CobblemonItems.SURPRISE_MULCH)
        entries.add(CobblemonItems.LOAMY_MULCH)
        entries.add(CobblemonItems.COARSE_MULCH)
        entries.add(CobblemonItems.PEAT_MULCH)
        entries.add(CobblemonItems.HUMID_MULCH)
        entries.add(CobblemonItems.SANDY_MULCH)
        entries.add(CobblemonItems.MULCH_BASE)

        CobblemonItems.berries().values.forEach(entries::add)
    }

    private fun blockEntries(displayContext: DisplayContext, entries: Entries) {
        entries.add(CobblemonItems.PC)
        entries.add(CobblemonItems.HEALING_MACHINE)
        entries.add(CobblemonItems.PASTURE)
        entries.add(CobblemonItems.APRICORN_LOG)
        entries.add(CobblemonItems.APRICORN_WOOD)
        entries.add(CobblemonItems.STRIPPED_APRICORN_LOG)
        entries.add(CobblemonItems.STRIPPED_APRICORN_WOOD)
        entries.add(CobblemonItems.APRICORN_PLANKS)
        entries.add(CobblemonItems.APRICORN_STAIRS)
        entries.add(CobblemonItems.APRICORN_SLAB)
        entries.add(CobblemonItems.APRICORN_FENCE)
        entries.add(CobblemonItems.APRICORN_FENCE_GATE)
        entries.add(CobblemonItems.APRICORN_DOOR)
        entries.add(CobblemonItems.APRICORN_TRAPDOOR)
        entries.add(CobblemonItems.APRICORN_BUTTON)
        entries.add(CobblemonItems.APRICORN_PRESSURE_PLATE)
        entries.add(CobblemonItems.APRICORN_LEAVES)
        entries.add(CobblemonItems.DAWN_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_DAWN_STONE_ORE)
        entries.add(CobblemonItems.DUSK_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_DUSK_STONE_ORE)
        entries.add(CobblemonItems.FIRE_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_FIRE_STONE_ORE)
        entries.add(CobblemonItems.ICE_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_ICE_STONE_ORE)
        entries.add(CobblemonItems.LEAF_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_LEAF_STONE_ORE)
        entries.add(CobblemonItems.MOON_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_MOON_STONE_ORE)
        entries.add(CobblemonItems.DRIPSTONE_MOON_STONE_ORE)
        entries.add(CobblemonItems.SHINY_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_SHINY_STONE_ORE)
        entries.add(CobblemonItems.SUN_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_SUN_STONE_ORE)
        entries.add(CobblemonItems.THUNDER_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_THUNDER_STONE_ORE)
        entries.add(CobblemonItems.WATER_STONE_ORE)
        entries.add(CobblemonItems.DEEPSLATE_WATER_STONE_ORE)
    }
    
    private fun consumableEntries(displayContext: DisplayContext, entries: Entries) {
        entries.add(CobblemonItems.ROASTED_LEEK)
        entries.add(CobblemonItems.LEEK_AND_POTATO_STEW)
        entries.add(CobblemonItems.BRAISED_VIVICHOKE)
        entries.add(CobblemonItems.VIVICHOKE_DIP)
        entries.add(CobblemonItems.BERRY_JUICE)
        entries.add(CobblemonItems.REMEDY)
        entries.add(CobblemonItems.FINE_REMEDY)
        entries.add(CobblemonItems.SUPERB_REMEDY)
        entries.add(CobblemonItems.HEAL_POWDER)
        entries.add(CobblemonItems.MEDICINAL_BREW)

        entries.add(CobblemonItems.POTION)
        entries.add(CobblemonItems.SUPER_POTION)
        entries.add(CobblemonItems.HYPER_POTION)
        entries.add(CobblemonItems.MAX_POTION)
        entries.add(CobblemonItems.FULL_RESTORE)

        entries.add(CobblemonItems.ANTIDOTE)
        entries.add(CobblemonItems.AWAKENING)
        entries.add(CobblemonItems.BURN_HEAL)
        entries.add(CobblemonItems.ICE_HEAL)
        entries.add(CobblemonItems.PARALYZE_HEAL)

        entries.add(CobblemonItems.FULL_HEAL)

        entries.add(CobblemonItems.ETHER)
        entries.add(CobblemonItems.MAX_ETHER)
        entries.add(CobblemonItems.ELIXIR)
        entries.add(CobblemonItems.MAX_ELIXIR)

        entries.add(CobblemonItems.REVIVE)
        entries.add(CobblemonItems.MAX_REVIVE)

        entries.add(CobblemonItems.X_ATTACK)
        entries.add(CobblemonItems.X_DEFENSE)
        entries.add(CobblemonItems.X_SP_ATK)
        entries.add(CobblemonItems.X_SP_DEF)
        entries.add(CobblemonItems.X_SPEED)
        entries.add(CobblemonItems.X_ACCURACY)

        entries.add(CobblemonItems.DIRE_HIT)
        entries.add(CobblemonItems.GUARD_SPEC)

        entries.add(CobblemonItems.HP_UP)
        entries.add(CobblemonItems.PROTEIN)
        entries.add(CobblemonItems.IRON)
        entries.add(CobblemonItems.CALCIUM)
        entries.add(CobblemonItems.ZINC)
        entries.add(CobblemonItems.CARBOS)
        entries.add(CobblemonItems.PP_UP)
        entries.add(CobblemonItems.PP_MAX)
        entries.add(CobblemonItems.EXPERIENCE_CANDY_XS)
        entries.add(CobblemonItems.EXPERIENCE_CANDY_S)
        entries.add(CobblemonItems.EXPERIENCE_CANDY_M)
        entries.add(CobblemonItems.EXPERIENCE_CANDY_L)
        entries.add(CobblemonItems.EXPERIENCE_CANDY_XL)
        entries.add(CobblemonItems.RARE_CANDY)

        entries.add(CobblemonItems.LONELY_MINT)
        entries.add(CobblemonItems.ADAMANT_MINT)
        entries.add(CobblemonItems.NAUGHTY_MINT)
        entries.add(CobblemonItems.BRAVE_MINT)
        entries.add(CobblemonItems.BOLD_MINT)
        entries.add(CobblemonItems.IMPISH_MINT)
        entries.add(CobblemonItems.LAX_MINT)
        entries.add(CobblemonItems.RELAXED_MINT)
        entries.add(CobblemonItems.MODEST_MINT)
        entries.add(CobblemonItems.MILD_MINT)
        entries.add(CobblemonItems.RASH_MINT)
        entries.add(CobblemonItems.QUIET_MINT)
        entries.add(CobblemonItems.CALM_MINT)
        entries.add(CobblemonItems.GENTLE_MINT)
        entries.add(CobblemonItems.CAREFUL_MINT)
        entries.add(CobblemonItems.SASSY_MINT)
        entries.add(CobblemonItems.TIMID_MINT)
        entries.add(CobblemonItems.HASTY_MINT)
        entries.add(CobblemonItems.JOLLY_MINT)
        entries.add(CobblemonItems.NAIVE_MINT)
        entries.add(CobblemonItems.SERIOUS_MINT)
    }

    private fun evolutionItemEntries(displayContext: DisplayContext, entries: Entries) {
        entries.add(CobblemonItems.FIRE_STONE)
        entries.add(CobblemonItems.WATER_STONE)
        entries.add(CobblemonItems.THUNDER_STONE)
        entries.add(CobblemonItems.LEAF_STONE)
        entries.add(CobblemonItems.MOON_STONE)
        entries.add(CobblemonItems.SUN_STONE)
        entries.add(CobblemonItems.SHINY_STONE)
        entries.add(CobblemonItems.DUSK_STONE)
        entries.add(CobblemonItems.DAWN_STONE)
        entries.add(CobblemonItems.ICE_STONE)
        entries.add(CobblemonItems.LINK_CABLE)
        entries.add(CobblemonItems.KINGS_ROCK)
        entries.add(CobblemonItems.GALARICA_CUFF)
        entries.add(CobblemonItems.GALARICA_WREATH)
        entries.add(CobblemonItems.METAL_COAT)
        entries.add(CobblemonItems.BLACK_AUGURITE)
        entries.add(CobblemonItems.PROTECTOR)
        entries.add(CobblemonItems.OVAL_STONE)
        entries.add(CobblemonItems.DRAGON_SCALE)
        entries.add(CobblemonItems.ELECTIRIZER)
        entries.add(CobblemonItems.MAGMARIZER)
        entries.add(CobblemonItems.UPGRADE)
        entries.add(CobblemonItems.DUBIOUS_DISC)
        entries.add(CobblemonItems.RAZOR_FANG)
        entries.add(CobblemonItems.RAZOR_CLAW)
        entries.add(CobblemonItems.PEAT_BLOCK)
        entries.add(CobblemonItems.PRISM_SCALE)
        entries.add(CobblemonItems.REAPER_CLOTH)
        entries.add(CobblemonItems.DEEP_SEA_TOOTH)
        entries.add(CobblemonItems.DEEP_SEA_SCALE)
        entries.add(CobblemonItems.SACHET)
        entries.add(CobblemonItems.WHIPPED_DREAM)
        entries.add(CobblemonItems.TART_APPLE)
        entries.add(CobblemonItems.SWEET_APPLE)
        entries.add(CobblemonItems.CRACKED_POT)
        entries.add(CobblemonItems.CHIPPED_POT)
        entries.add(CobblemonItems.STRAWBERRY_SWEET)
        entries.add(CobblemonItems.LOVE_SWEET)
        entries.add(CobblemonItems.BERRY_SWEET)
        entries.add(CobblemonItems.CLOVER_SWEET)
        entries.add(CobblemonItems.FLOWER_SWEET)
        entries.add(CobblemonItems.STAR_SWEET)
        entries.add(CobblemonItems.RIBBON_SWEET)
        entries.add(CobblemonItems.AUSPICIOUS_ARMOR)
        entries.add(CobblemonItems.MALICIOUS_ARMOR)
    }

    private fun heldItemEntries(displayContext: DisplayContext, entries: Entries) {
        entries.add(CobblemonItems.ASSAULT_VEST)
        entries.add(CobblemonItems.BIG_ROOT)
        entries.add(CobblemonItems.BLACK_BELT)
        entries.add(CobblemonItems.BLACK_GLASSES)
        entries.add(CobblemonItems.BLACK_SLUDGE)
        entries.add(CobblemonItems.BRIGHT_POWDER)
        entries.add(CobblemonItems.CHARCOAL)
        entries.add(CobblemonItems.CHOICE_BAND)
        entries.add(CobblemonItems.CHOICE_SCARF)
        entries.add(CobblemonItems.CHOICE_SPECS)
        entries.add(CobblemonItems.DEEP_SEA_SCALE)
        entries.add(CobblemonItems.DEEP_SEA_TOOTH)
        entries.add(CobblemonItems.DESTINY_KNOT)
        entries.add(CobblemonItems.DRAGON_FANG)
        entries.add(CobblemonItems.EVERSTONE)
        entries.add(CobblemonItems.EXP_SHARE)
        entries.add(CobblemonItems.FOCUS_BAND)
        entries.add(CobblemonItems.HARD_STONE)
        entries.add(CobblemonItems.HEAVY_DUTY_BOOTS)
        entries.add(CobblemonItems.KINGS_ROCK)
        entries.add(CobblemonItems.LEFTOVERS)
        entries.add(CobblemonItems.LIGHT_CLAY)
        entries.add(CobblemonItems.LUCKY_EGG)
        entries.add(CobblemonItems.MAGNET)
        entries.add(CobblemonItems.MENTAL_HERB)
        entries.add(CobblemonItems.METAL_POWDER)
        entries.add(CobblemonItems.MIRACLE_SEED)
        entries.add(CobblemonItems.MIRROR_HERB)
        entries.add(CobblemonItems.MUSCLE_BAND)
        entries.add(CobblemonItems.MYSTIC_WATER)
        entries.add(CobblemonItems.NEVER_MELT_ICE)
        entries.add(CobblemonItems.POISON_BARB)
        entries.add(CobblemonItems.POWER_HERB)
        entries.add(CobblemonItems.QUICK_CLAW)
        entries.add(CobblemonItems.QUICK_POWDER)
        entries.add(CobblemonItems.RAZOR_CLAW)
        entries.add(CobblemonItems.RAZOR_FANG)
        entries.add(CobblemonItems.ROCKY_HELMET)
        entries.add(CobblemonItems.SAFETY_GOGGLES)
        entries.add(CobblemonItems.SHARP_BEAK)
        entries.add(CobblemonItems.SILK_SCARF)
        entries.add(CobblemonItems.SILVER_POWDER)
        entries.add(CobblemonItems.SOFT_SAND)
        entries.add(CobblemonItems.SPELL_TAG)
        entries.add(CobblemonItems.TWISTED_SPOON)
        entries.add(CobblemonItems.WHITE_HERB)
        entries.add(CobblemonItems.WISE_GLASSES)
        entries.add(CobblemonItems.POWER_ANKLET)
        entries.add(CobblemonItems.POWER_BAND)
        entries.add(CobblemonItems.POWER_BELT)
        entries.add(CobblemonItems.POWER_BRACER)
        entries.add(CobblemonItems.POWER_LENS)
        entries.add(CobblemonItems.POWER_WEIGHT)
    }

    private fun pokeballentries(displayContext: DisplayContext, entries: Entries) {
        CobblemonItems.pokeBalls.forEach(entries::add)
    }

    private fun foodInjections(displayContext: DisplayContext): List<Item> = arrayListOf(
        CobblemonItems.ROASTED_LEEK,
        CobblemonItems.LEEK_AND_POTATO_STEW,
        CobblemonItems.BRAISED_VIVICHOKE,
        CobblemonItems.VIVICHOKE_DIP
    )

}
