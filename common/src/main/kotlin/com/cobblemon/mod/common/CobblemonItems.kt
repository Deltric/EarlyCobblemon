/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.item.*
import com.cobblemon.mod.common.item.ApricornItem
import com.cobblemon.mod.common.item.ApricornSeedItem
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.MintLeafItem
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.common.item.interactive.CandyItem
import com.cobblemon.mod.common.item.interactive.EnergyRoot
import com.cobblemon.mod.common.item.interactive.LinkCableItem
import com.cobblemon.mod.common.item.interactive.MintItem
import com.cobblemon.mod.common.item.interactive.RemedyItem
import com.cobblemon.mod.common.item.interactive.VitaminItem
import com.cobblemon.mod.common.mint.MintType
import com.cobblemon.mod.common.platform.PlatformRegistry
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager
import net.minecraft.block.Block
import net.minecraft.block.ComposterBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.item.StewItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object CobblemonItems : PlatformRegistry<Registry<Item>, RegistryKey<Registry<Item>>, Item>() {
    override val registry: Registry<Item> = Registries.ITEM
    override val registryKey: RegistryKey<Registry<Item>> = RegistryKeys.ITEM

    @JvmField
    val pokeBalls = mutableListOf<PokeBallItem>()
    @JvmField
    val POKE_BALL = pokeBallItem(PokeBalls.POKE_BALL)
    @JvmField
    val CITRINE_BALL = pokeBallItem(PokeBalls.CITRINE_BALL)
    @JvmField
    val VERDANT_BALL = pokeBallItem(PokeBalls.VERDANT_BALL)
    @JvmField
    val AZURE_BALL = pokeBallItem(PokeBalls.AZURE_BALL)
    @JvmField
    val ROSEATE_BALL = pokeBallItem(PokeBalls.ROSEATE_BALL)
    @JvmField
    val SLATE_BALL = pokeBallItem(PokeBalls.SLATE_BALL)
    @JvmField
    val PREMIER_BALL = pokeBallItem(PokeBalls.PREMIER_BALL)
    @JvmField
    val GREAT_BALL = pokeBallItem(PokeBalls.GREAT_BALL)
    @JvmField
    val ULTRA_BALL = pokeBallItem(PokeBalls.ULTRA_BALL)
    @JvmField
    val SAFARI_BALL = pokeBallItem(PokeBalls.SAFARI_BALL)
    @JvmField
    val FAST_BALL = pokeBallItem(PokeBalls.FAST_BALL)
    @JvmField
    val LEVEL_BALL = pokeBallItem(PokeBalls.LEVEL_BALL)
    @JvmField
    val LURE_BALL = pokeBallItem(PokeBalls.LURE_BALL)
    @JvmField
    val HEAVY_BALL = pokeBallItem(PokeBalls.HEAVY_BALL)
    @JvmField
    val LOVE_BALL = pokeBallItem(PokeBalls.LOVE_BALL)
    @JvmField
    val FRIEND_BALL = pokeBallItem(PokeBalls.FRIEND_BALL)
    @JvmField
    val MOON_BALL = pokeBallItem(PokeBalls.MOON_BALL)
    @JvmField
    val SPORT_BALL = pokeBallItem(PokeBalls.SPORT_BALL)
    @JvmField
    val PARK_BALL = pokeBallItem(PokeBalls.PARK_BALL)
    @JvmField
    val NET_BALL = pokeBallItem(PokeBalls.NET_BALL)
    @JvmField
    val DIVE_BALL = pokeBallItem(PokeBalls.DIVE_BALL)
    @JvmField
    val NEST_BALL = pokeBallItem(PokeBalls.NEST_BALL)
    @JvmField
    val REPEAT_BALL = pokeBallItem(PokeBalls.REPEAT_BALL)
    @JvmField
    val TIMER_BALL = pokeBallItem(PokeBalls.TIMER_BALL)
    @JvmField
    val LUXURY_BALL = pokeBallItem(PokeBalls.LUXURY_BALL)
    @JvmField
    val DUSK_BALL = pokeBallItem(PokeBalls.DUSK_BALL)
    @JvmField
    val HEAL_BALL = pokeBallItem(PokeBalls.HEAL_BALL)
    @JvmField
    val QUICK_BALL = pokeBallItem(PokeBalls.QUICK_BALL)
    @JvmField
    val DREAM_BALL = pokeBallItem(PokeBalls.DREAM_BALL)
    @JvmField
    val BEAST_BALL = pokeBallItem(PokeBalls.BEAST_BALL)
    @JvmField
    val MASTER_BALL = pokeBallItem(PokeBalls.MASTER_BALL)
    @JvmField
    val CHERISH_BALL = pokeBallItem(PokeBalls.CHERISH_BALL)

    @JvmField
    val RED_APRICORN = create("red_apricorn", ApricornItem(CobblemonBlocks.RED_APRICORN))
    @JvmField
    val YELLOW_APRICORN = create("yellow_apricorn", ApricornItem(CobblemonBlocks.YELLOW_APRICORN))
    @JvmField
    val GREEN_APRICORN = create("green_apricorn", ApricornItem(CobblemonBlocks.GREEN_APRICORN))
    @JvmField
    val BLUE_APRICORN = create("blue_apricorn", ApricornItem(CobblemonBlocks.BLUE_APRICORN))
    @JvmField
    val PINK_APRICORN = create("pink_apricorn", ApricornItem(CobblemonBlocks.PINK_APRICORN))
    @JvmField
    val BLACK_APRICORN = create("black_apricorn", ApricornItem(CobblemonBlocks.BLACK_APRICORN))
    @JvmField
    val WHITE_APRICORN = create("white_apricorn", ApricornItem(CobblemonBlocks.WHITE_APRICORN))

    @JvmField
    val RED_APRICORN_SEED = create("red_apricorn_seed", ApricornSeedItem(CobblemonBlocks.RED_APRICORN_SAPLING))
    @JvmField
    val YELLOW_APRICORN_SEED = create("yellow_apricorn_seed", ApricornSeedItem(CobblemonBlocks.YELLOW_APRICORN_SAPLING))
    @JvmField
    val GREEN_APRICORN_SEED = create("green_apricorn_seed", ApricornSeedItem(CobblemonBlocks.GREEN_APRICORN_SAPLING))
    @JvmField
    val BLUE_APRICORN_SEED = create("blue_apricorn_seed", ApricornSeedItem(CobblemonBlocks.BLUE_APRICORN_SAPLING))
    @JvmField
    val PINK_APRICORN_SEED = create("pink_apricorn_seed", ApricornSeedItem(CobblemonBlocks.PINK_APRICORN_SAPLING))
    @JvmField
    val BLACK_APRICORN_SEED = create("black_apricorn_seed", ApricornSeedItem(CobblemonBlocks.BLACK_APRICORN_SAPLING))
    @JvmField
    val WHITE_APRICORN_SEED = create("white_apricorn_seed", ApricornSeedItem(CobblemonBlocks.WHITE_APRICORN_SAPLING))

    @JvmField
    val APRICORN_LOG = blockItem("apricorn_log", CobblemonBlocks.APRICORN_LOG)
    @JvmField
    val STRIPPED_APRICORN_LOG = blockItem("stripped_apricorn_log", CobblemonBlocks.STRIPPED_APRICORN_LOG)
    @JvmField
    val APRICORN_WOOD = blockItem("apricorn_wood", CobblemonBlocks.APRICORN_WOOD)
    @JvmField
    val STRIPPED_APRICORN_WOOD = blockItem("stripped_apricorn_wood", CobblemonBlocks.STRIPPED_APRICORN_WOOD)
    @JvmField
    val APRICORN_PLANKS = blockItem("apricorn_planks", CobblemonBlocks.APRICORN_PLANKS)
    @JvmField
    val APRICORN_LEAVES = blockItem("apricorn_leaves", CobblemonBlocks.APRICORN_LEAVES).also {
        val compostChance = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(Items.OAK_LEAVES)
        // Should always pass unless Mojang reworks leaves to no longer work in the Composter, in that case we already updated w.o doing anything
        if (compostChance != ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue()) {
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE[it] = compostChance
        }
    }

    @JvmField
    val APRICORN_DOOR = blockItem("apricorn_door", CobblemonBlocks.APRICORN_DOOR)
    @JvmField
    val APRICORN_TRAPDOOR = blockItem("apricorn_trapdoor", CobblemonBlocks.APRICORN_TRAPDOOR)
    @JvmField
    val APRICORN_FENCE = blockItem("apricorn_fence", CobblemonBlocks.APRICORN_FENCE)
    @JvmField
    val APRICORN_FENCE_GATE = blockItem("apricorn_fence_gate", CobblemonBlocks.APRICORN_FENCE_GATE)
    @JvmField
    val APRICORN_BUTTON = blockItem("apricorn_button", CobblemonBlocks.APRICORN_BUTTON)
    @JvmField
    val APRICORN_PRESSURE_PLATE = blockItem("apricorn_pressure_plate", CobblemonBlocks.APRICORN_PRESSURE_PLATE)
    //@JvmField
//    val APRICORN_SIGN = create("apricorn_sign", SignItem(Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), CobblemonBlocks.APRICORN_SIGN, CobblemonBlocks.APRICORN_WALL_SIGN))
    @JvmField
    val APRICORN_SLAB = blockItem("apricorn_slab", CobblemonBlocks.APRICORN_SLAB)
    @JvmField
    val APRICORN_STAIRS = blockItem("apricorn_stairs", CobblemonBlocks.APRICORN_STAIRS)

    @JvmField
    val HEALING_MACHINE = blockItem("healing_machine", CobblemonBlocks.HEALING_MACHINE)
    @JvmField
    val PC = blockItem("pc", CobblemonBlocks.PC)

    // Evolution items
    @JvmField val LINK_CABLE = create("link_cable", LinkCableItem())
    @JvmField val DRAGON_SCALE = evolutionItem("dragon_scale")
    @JvmField val KINGS_ROCK = evolutionItem("kings_rock")
    @JvmField val METAL_COAT = evolutionItem("metal_coat")
    @JvmField val UPGRADE = evolutionItem("upgrade")
    @JvmField val DUBIOUS_DISC = evolutionItem("dubious_disc")
    @JvmField val DEEP_SEA_SCALE = evolutionItem("deep_sea_scale")
    @JvmField val DEEP_SEA_TOOTH = evolutionItem("deep_sea_tooth")
    @JvmField val ELECTIRIZER = evolutionItem("electirizer")
    @JvmField val MAGMARIZER = evolutionItem("magmarizer")
    @JvmField val OVAL_STONE = evolutionItem("oval_stone")
    @JvmField val PROTECTOR = evolutionItem("protector")
    @JvmField val REAPER_CLOTH = evolutionItem("reaper_cloth")
    @JvmField val PRISM_SCALE = evolutionItem("prism_scale")
    @JvmField val SACHET = evolutionItem("sachet")
    @JvmField val WHIPPED_DREAM = evolutionItem("whipped_dream")
    @JvmField val STRAWBERRY_SWEET = evolutionItem("strawberry_sweet")
    @JvmField val LOVE_SWEET = evolutionItem("love_sweet")
    @JvmField val BERRY_SWEET = evolutionItem("berry_sweet")
    @JvmField val CLOVER_SWEET = evolutionItem("clover_sweet")
    @JvmField val FLOWER_SWEET = evolutionItem("flower_sweet")
    @JvmField val STAR_SWEET = evolutionItem("star_sweet")
    @JvmField val RIBBON_SWEET = evolutionItem("ribbon_sweet")
    @JvmField val CHIPPED_POT = evolutionItem("chipped_pot")
    @JvmField val CRACKED_POT = evolutionItem("cracked_pot")
    @JvmField val SWEET_APPLE = evolutionItem("sweet_apple")
    @JvmField val TART_APPLE = evolutionItem("tart_apple")
    @JvmField val GALARICA_CUFF = evolutionItem("galarica_cuff")
    @JvmField val GALARICA_WREATH = evolutionItem("galarica_wreath")
    @JvmField val BLACK_AUGURITE = evolutionItem("black_augurite")
    @JvmField val PEAT_BLOCK = evolutionItem("peat_block")
    @JvmField val RAZOR_CLAW = evolutionItem("razor_claw")
    @JvmField val RAZOR_FANG = evolutionItem("razor_fang")
    // ToDo enable me when malicious armor goes in the game
    //@JvmField val AUSPICIOUS_ARMOR = heldItem("auspicious_armor")

    // Medicine
    @JvmField
    val RARE_CANDY = candyItem("rare_candy") { _, pokemon -> pokemon.getExperienceToNextLevel() }
    @JvmField
    val EXPERIENCE_CANDY_XS = candyItem("exp_candy_xs") { _, _ -> CandyItem.DEFAULT_XS_CANDY_YIELD }
    @JvmField
    val EXPERIENCE_CANDY_S = candyItem("exp_candy_s") { _, _ -> CandyItem.DEFAULT_S_CANDY_YIELD }
    @JvmField
    val EXPERIENCE_CANDY_M = candyItem("exp_candy_m") { _, _ -> CandyItem.DEFAULT_M_CANDY_YIELD }
    @JvmField
    val EXPERIENCE_CANDY_L = candyItem("exp_candy_l") { _, _ -> CandyItem.DEFAULT_L_CANDY_YIELD }
    @JvmField
    val EXPERIENCE_CANDY_XL = candyItem("exp_candy_xl") { _, _ -> CandyItem.DEFAULT_XL_CANDY_YIELD }
    @JvmField
    val CALCIUM = create("calcium", VitaminItem(Stats.SPECIAL_ATTACK))
    @JvmField
    val CARBOS = create("carbos", VitaminItem(Stats.SPEED))
    @JvmField
    val HP_UP = create("hp_up", VitaminItem(Stats.HP))
    @JvmField
    val IRON = create("iron", VitaminItem(Stats.DEFENCE))
    @JvmField
    val PROTEIN = create("protein", VitaminItem(Stats.ATTACK))
    @JvmField
    val ZINC = create("zinc", VitaminItem(Stats.SPECIAL_DEFENCE))
    @JvmField
    val MEDICINAL_LEEK = create("medicinal_leek", MedicinalLeekItem(CobblemonBlocks.MEDICINAL_LEEK_CROP))
    @JvmField
    val ROASTED_LEEK = create("roasted_leek", Item(Item.Settings().food(FoodComponent.Builder().snack().hunger(2).build())))
    @JvmField
    val ENERGY_ROOT = create("energy_root", EnergyRoot(CobblemonBlocks.ENERGY_ROOT))
    @JvmField
    val REVIVAL_HERB = create("revival_herb", RevivalHerbItem(CobblemonBlocks.REVIVAL_HERB))
    @JvmField
    val PEP_UP_FLOWER = create("pep_up_flower", PepUpFlowerItem(Item.Settings()))
    @JvmField
    val MEDICINAL_BREW = create("medicinal_brew", Item(Item.Settings()))
    @JvmField
    val REMEDY = create("remedy", RemedyItem(RemedyItem.NORMAL))
    @JvmField
    val FINE_REMEDY = create("fine_remedy", RemedyItem(RemedyItem.FINE))
    @JvmField
    val SUPERB_REMEDY = create("superb_remedy", RemedyItem(RemedyItem.SUPERB))


    @JvmField
    val HEAL_POWDER = create("heal_powder", Item(Item.Settings()))

    // Remove me when Momo's vivichoke work is done
    @JvmField
    val VIVICHOKE = create("vivichoke", Item(Item.Settings()))

    @JvmField
    val LEEK_AND_POTATO_STEW = create("leek_and_potato_stew", StewItem(Item.Settings().food(FoodComponent.Builder().hunger(4).build())))
    @JvmField
    val REVIVE = create("revive", Item(Item.Settings()))
    @JvmField
    val MAX_REVIVE = create("max_revive", Item(Item.Settings()))
    @JvmField
    val PP_UP = create("pp_up", Item(Item.Settings()))
    @JvmField
    val PP_MAX = create("pp_max", Item(Item.Settings()))


    @JvmField
    val RED_MINT_SEEDS = blockItem("red_mint_seeds", MintType.RED.getCropBlock())
    @JvmField
    val RED_MINT_LEAF = create("red_mint_leaf", MintLeafItem(MintType.RED))
    @JvmField
    val BLUE_MINT_SEEDS = blockItem("blue_mint_seeds", MintType.BLUE.getCropBlock())
    @JvmField
    val BLUE_MINT_LEAF = create("blue_mint_leaf", MintLeafItem(MintType.BLUE))
    @JvmField
    val CYAN_MINT_SEEDS = blockItem("cyan_mint_seeds", MintType.CYAN.getCropBlock())
    @JvmField
    val CYAN_MINT_LEAF = create("cyan_mint_leaf", MintLeafItem(MintType.CYAN))
    @JvmField
    val PINK_MINT_SEEDS = blockItem("pink_mint_seeds", MintType.PINK.getCropBlock())
    @JvmField
    val PINK_MINT_LEAF = create("pink_mint_leaf", MintLeafItem(MintType.PINK))
    @JvmField
    val GREEN_MINT_SEEDS = blockItem("green_mint_seeds", MintType.GREEN.getCropBlock())
    @JvmField
    val GREEN_MINT_LEAF = create("green_mint_leaf", MintLeafItem(MintType.GREEN))
    @JvmField
    val WHITE_MINT_SEEDS = blockItem("white_mint_seeds", MintType.WHITE.getCropBlock())
    @JvmField
    val WHITE_MINT_LEAF = create("white_mint_leaf", MintLeafItem(MintType.WHITE))

    @JvmField
    val LONELY_MINT = create("lonely_mint", MintItem(Natures.LONELY))
    @JvmField
    val ADAMANT_MINT = create("adamant_mint", MintItem(Natures.ADAMANT))
    @JvmField
    val NAUGHTY_MINT = create("naughty_mint", MintItem(Natures.NAUGHTY))
    @JvmField
    val BRAVE_MINT = create("brave_mint", MintItem(Natures.BRAVE))
    @JvmField
    val BOLD_MINT = create("bold_mint", MintItem(Natures.BOLD))
    @JvmField
    val IMPISH_MINT = create("impish_mint", MintItem(Natures.IMPISH))
    @JvmField
    val LAX_MINT = create("lax_mint", MintItem(Natures.LAX))
    @JvmField
    val RELAXED_MINT = create("relaxed_mint", MintItem(Natures.RELAXED))
    @JvmField
    val MODEST_MINT = create("modest_mint", MintItem(Natures.MODEST))
    @JvmField
    val MILD_MINT = create("mild_mint", MintItem(Natures.MILD))
    @JvmField
    val RASH_MINT = create("rash_mint", MintItem(Natures.RASH))
    @JvmField
    val QUIET_MINT = create("quiet_mint", MintItem(Natures.QUIET))
    @JvmField
    val CALM_MINT = create("calm_mint", MintItem(Natures.CALM))
    @JvmField
    val GENTLE_MINT = create("gentle_mint", MintItem(Natures.GENTLE))
    @JvmField
    val CAREFUL_MINT = create("careful_mint", MintItem(Natures.CAREFUL))
    @JvmField
    val SASSY_MINT = create("sassy_mint", MintItem(Natures.SASSY))
    @JvmField
    val TIMID_MINT = create("timid_mint", MintItem(Natures.TIMID))
    @JvmField
    val HASTY_MINT = create("hasty_mint", MintItem(Natures.HASTY))
    @JvmField
    val JOLLY_MINT = create("jolly_mint", MintItem(Natures.JOLLY))
    @JvmField
    val NAIVE_MINT = create("naive_mint", MintItem(Natures.NAIVE))
    @JvmField
    val SERIOUS_MINT = create("serious_mint", MintItem(Natures.SERIOUS))

    @JvmField val X_ACCURACY = create("x_accuracy", CobblemonItem(Item.Settings()))
    @JvmField val X_ATTACK = create("x_attack", CobblemonItem(Item.Settings()))
    @JvmField val X_DEFENSE = create("x_defense", CobblemonItem(Item.Settings()))
    @JvmField val X_SP_ATK = create("x_sp_atk", CobblemonItem(Item.Settings()))
    @JvmField val X_SP_DEF = create("x_sp_def", CobblemonItem(Item.Settings()))
    @JvmField val X_SPEED = create("x_speed", CobblemonItem(Item.Settings()))

    @JvmField val ABILITY_PATCH = create("ability_patch", CobblemonItem(Item.Settings()))
    @JvmField val BIG_MALASADA = create("big_malasada", CobblemonItem(Item.Settings()))
    @JvmField val CASTELIACONE = create("casteliacone", CobblemonItem(Item.Settings()))
    @JvmField val JUBILIFE_MUFFIN = create("jubilife_muffin", CobblemonItem(Item.Settings()))
    @JvmField val LAVA_COOKIE = create("lava_cookie", CobblemonItem(Item.Settings()))
    @JvmField val LUMIOSE_GALETTE = create("lumiose_galette", CobblemonItem(Item.Settings()))
    @JvmField val OLD_GATEAU = create("old_gateau", CobblemonItem(Item.Settings()))
    @JvmField val PEWTER_CRUNCHIES = create("pewter_crunchies", CobblemonItem(Item.Settings()))
    @JvmField val RAGE_CANDY_BAR = create("rage_candy_bar", CobblemonItem(Item.Settings()))
    @JvmField val SHALOUR_SABLE = create("shalour_sable", CobblemonItem(Item.Settings()))

    /**
     * Evolution Ores and Stones
     */
    @JvmField
    val DAWN_STONE_ORE = blockItem("dawn_stone_ore", CobblemonBlocks.DAWN_STONE_ORE)
    @JvmField
    val DUSK_STONE_ORE = blockItem("dusk_stone_ore", CobblemonBlocks.DUSK_STONE_ORE)
    @JvmField
    val FIRE_STONE_ORE = blockItem("fire_stone_ore", CobblemonBlocks.FIRE_STONE_ORE)
    @JvmField
    val ICE_STONE_ORE = blockItem("ice_stone_ore", CobblemonBlocks.ICE_STONE_ORE)
    @JvmField
    val LEAF_STONE_ORE = blockItem("leaf_stone_ore", CobblemonBlocks.LEAF_STONE_ORE)
    @JvmField
    val MOON_STONE_ORE = blockItem("moon_stone_ore", CobblemonBlocks.MOON_STONE_ORE)
    @JvmField
    val SHINY_STONE_ORE = blockItem("shiny_stone_ore", CobblemonBlocks.SHINY_STONE_ORE)
    @JvmField
    val SUN_STONE_ORE = blockItem("sun_stone_ore", CobblemonBlocks.SUN_STONE_ORE)
    @JvmField
    val THUNDER_STONE_ORE = blockItem("thunder_stone_ore", CobblemonBlocks.THUNDER_STONE_ORE)
    @JvmField
    val WATER_STONE_ORE = blockItem("water_stone_ore", CobblemonBlocks.WATER_STONE_ORE)
    @JvmField
    val DEEPSLATE_DAWN_STONE_ORE = blockItem("deepslate_dawn_stone_ore", CobblemonBlocks.DEEPSLATE_DAWN_STONE_ORE)
    @JvmField
    val DEEPSLATE_DUSK_STONE_ORE = blockItem("deepslate_dusk_stone_ore", CobblemonBlocks.DEEPSLATE_DUSK_STONE_ORE)
    @JvmField
    val DEEPSLATE_FIRE_STONE_ORE = blockItem("deepslate_fire_stone_ore", CobblemonBlocks.DEEPSLATE_FIRE_STONE_ORE)
    @JvmField
    val DEEPSLATE_ICE_STONE_ORE = blockItem("deepslate_ice_stone_ore", CobblemonBlocks.DEEPSLATE_ICE_STONE_ORE)
    @JvmField
    val DEEPSLATE_LEAF_STONE_ORE = blockItem("deepslate_leaf_stone_ore", CobblemonBlocks.DEEPSLATE_LEAF_STONE_ORE)
    @JvmField
    val DEEPSLATE_MOON_STONE_ORE = blockItem("deepslate_moon_stone_ore", CobblemonBlocks.DEEPSLATE_MOON_STONE_ORE)
    @JvmField
    val DEEPSLATE_SHINY_STONE_ORE = blockItem("deepslate_shiny_stone_ore", CobblemonBlocks.DEEPSLATE_SHINY_STONE_ORE)
    @JvmField
    val DEEPSLATE_SUN_STONE_ORE = blockItem("deepslate_sun_stone_ore", CobblemonBlocks.DEEPSLATE_SUN_STONE_ORE)
    @JvmField
    val DEEPSLATE_THUNDER_STONE_ORE = blockItem("deepslate_thunder_stone_ore", CobblemonBlocks.DEEPSLATE_THUNDER_STONE_ORE)
    @JvmField
    val DEEPSLATE_WATER_STONE_ORE = blockItem("deepslate_water_stone_ore", CobblemonBlocks.DEEPSLATE_WATER_STONE_ORE)
    @JvmField
    val DRIPSTONE_MOON_STONE_ORE = blockItem("dripstone_moon_stone_ore", CobblemonBlocks.DRIPSTONE_MOON_STONE_ORE)
    @JvmField
    val DAWN_STONE = evolutionItem("dawn_stone")
    @JvmField
    val DUSK_STONE = evolutionItem("dusk_stone")
    @JvmField
    val FIRE_STONE = evolutionItem("fire_stone")
    @JvmField
    val ICE_STONE = evolutionItem("ice_stone")
    @JvmField
    val LEAF_STONE = evolutionItem("leaf_stone")
    @JvmField
    val MOON_STONE = evolutionItem("moon_stone")
    @JvmField
    val SHINY_STONE = evolutionItem("shiny_stone")
    @JvmField
    val SUN_STONE = evolutionItem("sun_stone")
    @JvmField
    val THUNDER_STONE = evolutionItem("thunder_stone")
    @JvmField
    val WATER_STONE = evolutionItem("water_stone")

    // Held Items
    @JvmField
    val ASSAULT_VEST = heldItem("assault_vest")
    @JvmField
    val BIG_ROOT = create("big_root", BigRoot(CobblemonBlocks.BIG_ROOT))
    @JvmField
    val BLACK_BELT = heldItem("black_belt")
    @JvmField
    val BLACK_GLASSES = heldItem("black_glasses")
    @JvmField
    val BLACK_SLUDGE = heldItem("black_sludge")
    @JvmField
    val CHARCOAL = heldItem("charcoal_stick", remappedName = "charcoal")
    @JvmField
    val CHOICE_BAND = heldItem("choice_band")
    @JvmField
    val CHOICE_SCARF = heldItem("choice_scarf")
    @JvmField
    val CHOICE_SPECS = heldItem("choice_specs")
    @JvmField
    val DRAGON_FANG = heldItem("dragon_fang")
    @JvmField
    val EXP_SHARE = heldItem("exp_share")
    @JvmField
    val FOCUS_BAND = heldItem("focus_band")
    @JvmField
    val HARD_STONE = heldItem("hard_stone")
    @JvmField
    val HEAVY_DUTY_BOOTS = heldItem("heavy_duty_boots")
    @JvmField
    val LEFTOVERS = heldItem("leftovers")
    @JvmField
    val LIGHT_CLAY = heldItem("light_clay")
    @JvmField
    val LUCKY_EGG = heldItem("lucky_egg")
    @JvmField
    val MAGNET = heldItem("magnet")
    @JvmField
    val MIRACLE_SEED = heldItem("miracle_seed")
    @JvmField
    val MUSCLE_BAND = heldItem("muscle_band")
    @JvmField
    val MYSTIC_WATER = heldItem("mystic_water")
    @JvmField
    val NEVER_MELT_ICE = heldItem("never_melt_ice")
    @JvmField
    val POISON_BARB = heldItem("poison_barb")
    @JvmField
    val QUICK_CLAW = heldItem("quick_claw")
    @JvmField
    val ROCKY_HELMET = heldItem("rocky_helmet")
    @JvmField
    val SAFETY_GOGGLES = heldItem("safety_goggles")
    @JvmField
    val SHARP_BEAK = heldItem("sharp_beak")
    @JvmField
    val SILK_SCARF = heldItem("silk_scarf")
    @JvmField
    val SILVER_POWDER = heldItem("silver_powder")
    @JvmField
    val SOFT_SAND = heldItem("soft_sand")
    @JvmField
    val SPELL_TAG = heldItem("spell_tag")
    @JvmField
    val TWISTED_SPOON = heldItem("twisted_spoon")
    @JvmField
    val WISE_GLASSES = heldItem("wise_glasses")

    // Mulch
    @JvmField
    val MULCH_BASE = mulchItem("mulch_base")
    @JvmField
    val COARSE_MULCH = mulchItem("coarse_mulch")
    @JvmField
    val GROWTH_MULCH = mulchItem("growth_mulch")
    @JvmField
    val HUMID_MULCH = mulchItem("humid_mulch")
    @JvmField
    val LOAMY_MULCH = mulchItem("loamy_mulch")
    @JvmField
    val PEAT_MULCH = mulchItem("peat_mulch")
    @JvmField
    val RICH_MULCH = mulchItem("rich_mulch")
    @JvmField
    val SANDY_MULCH = mulchItem("sandy_mulch")
    @JvmField
    val SURPRISE_MULCH = mulchItem("surprise_mulch")

    // Misc
    @JvmField
    val POKEMON_MODEL = this.create("pokemon_model", PokemonItem())

    private fun blockItem(name: String, block: Block): BlockItem = this.create(name, BlockItem(block, Item.Settings()))

    private fun evolutionItem(name: String): CobblemonItem = this.create(name, CobblemonItem(Item.Settings()))

    private fun pokeBallItem(pokeBall: PokeBall): PokeBallItem {
        val item = create(pokeBall.name.path, PokeBallItem(pokeBall))
        pokeBall.item = item
        pokeBalls.add(item)
        return item
    }

//    private fun heldItem(name: String): CobblemonItem = this.create(name, CobblemonItem(Item.Settings()))

    private fun candyItem(name: String, calculator: CandyItem.Calculator): CandyItem  = this.create(name, CandyItem(calculator))

    private fun heldItem(name: String, remappedName: String? = null): CobblemonItem = create(
        name,
        CobblemonItem(Item.Settings()).also {
            if (remappedName != null) {
                CobblemonHeldItemManager.registerRemap(it, remappedName)
            }
        }
    )

    private fun mulchItem(name: String): CobblemonItem = this.create(name, CobblemonItem(Item.Settings()))

    // USE THIS TO DETERMINE WHAT ITEMS GO WHERE
    // https://docs.google.com/spreadsheets/d/1QgaIlW-S9A-Blqhc-5G7OO3igaQdEAiYQqVEPnEBFmc/edit#gid=978365418

    fun registerToItemGroups(consumer: (ItemGroup, Item) -> Unit) {

        CobblemonItemGroups.BLOCKS.group().let { group ->
            consumer(group, PC)
            consumer(group, HEALING_MACHINE)
            consumer(group, APRICORN_LOG)
            consumer(group, APRICORN_WOOD)
            consumer(group, STRIPPED_APRICORN_LOG)
            consumer(group, STRIPPED_APRICORN_WOOD)
            consumer(group, APRICORN_PLANKS)
            consumer(group, APRICORN_STAIRS)
            consumer(group, APRICORN_SLAB)
            consumer(group, APRICORN_FENCE)
            consumer(group, APRICORN_FENCE_GATE)
            consumer(group, APRICORN_DOOR)
            consumer(group, APRICORN_TRAPDOOR)
            consumer(group, APRICORN_BUTTON)
            consumer(group, APRICORN_PRESSURE_PLATE)
            consumer(group, APRICORN_LEAVES)
            consumer(group, DAWN_STONE_ORE)
            consumer(group, DEEPSLATE_DAWN_STONE_ORE)
            consumer(group, DUSK_STONE_ORE)
            consumer(group, DEEPSLATE_DUSK_STONE_ORE)
            consumer(group, FIRE_STONE_ORE)
            consumer(group, DEEPSLATE_FIRE_STONE_ORE)
            consumer(group, ICE_STONE_ORE)
            consumer(group, DEEPSLATE_ICE_STONE_ORE)
            consumer(group, LEAF_STONE_ORE)
            consumer(group, DEEPSLATE_LEAF_STONE_ORE)
            consumer(group, MOON_STONE_ORE)
            consumer(group, DEEPSLATE_MOON_STONE_ORE)
            consumer(group, DRIPSTONE_MOON_STONE_ORE)
            consumer(group, SHINY_STONE_ORE)
            consumer(group, DEEPSLATE_SHINY_STONE_ORE)
            consumer(group, SUN_STONE_ORE)
            consumer(group, DEEPSLATE_SUN_STONE_ORE)
            consumer(group, THUNDER_STONE_ORE)
            consumer(group, DEEPSLATE_THUNDER_STONE_ORE)
            consumer(group, WATER_STONE_ORE)
            consumer(group, DEEPSLATE_WATER_STONE_ORE)
        }

        pokeBalls.forEach { item -> consumer(CobblemonItemGroups.POKE_BALLS.group(), item) }

        CobblemonItemGroups.AGRICULTURE.group().let { group ->
            consumer(group, MEDICINAL_LEEK)
            consumer(group, BIG_ROOT)
            consumer(group, ENERGY_ROOT)
            consumer(group, REVIVAL_HERB)
            consumer(group, PEP_UP_FLOWER)

            consumer(group, RED_APRICORN)
            consumer(group, YELLOW_APRICORN)
            consumer(group, GREEN_APRICORN)
            consumer(group, BLUE_APRICORN)
            consumer(group, PINK_APRICORN)
            consumer(group, BLACK_APRICORN)
            consumer(group, WHITE_APRICORN)
            consumer(group, RED_APRICORN_SEED)
            consumer(group, YELLOW_APRICORN_SEED)
            consumer(group, GREEN_APRICORN_SEED)
            consumer(group, BLUE_APRICORN_SEED)
            consumer(group, PINK_APRICORN_SEED)
            consumer(group, BLACK_APRICORN_SEED)
            consumer(group, WHITE_APRICORN_SEED)

            consumer(group, RED_MINT_SEEDS)
            consumer(group, RED_MINT_LEAF)
            consumer(group, BLUE_MINT_SEEDS)
            consumer(group, BLUE_MINT_LEAF)
            consumer(group, CYAN_MINT_SEEDS)
            consumer(group, CYAN_MINT_LEAF)
            consumer(group, PINK_MINT_SEEDS)
            consumer(group, PINK_MINT_LEAF)
            consumer(group, GREEN_MINT_SEEDS)
            consumer(group, GREEN_MINT_LEAF)
            consumer(group, WHITE_MINT_SEEDS)
            consumer(group, WHITE_MINT_LEAF)
        }

        CobblemonItemGroups.CONSUMABLES.group().let { group ->
            consumer(group, ROASTED_LEEK)
            consumer(group, LEEK_AND_POTATO_STEW)
            consumer(group, REMEDY)
            consumer(group, FINE_REMEDY)
            consumer(group, SUPERB_REMEDY)
            consumer(group, HEAL_POWDER)
            consumer(group, MEDICINAL_BREW)
            consumer(group, REVIVE)
            consumer(group, MAX_REVIVE)

            consumer(group, X_ATTACK)
            consumer(group, X_DEFENSE)
            consumer(group, X_SP_ATK)
            consumer(group, X_SP_DEF)
            consumer(group, X_SPEED)
            consumer(group, X_ACCURACY)

            consumer(group, HP_UP)
            consumer(group, PROTEIN)
            consumer(group, IRON)
            consumer(group, CALCIUM)
            consumer(group, ZINC)
            consumer(group, CARBOS)
            consumer(group, PP_UP)
            consumer(group, PP_MAX)
            consumer(group, EXPERIENCE_CANDY_XS)
            consumer(group, EXPERIENCE_CANDY_S)
            consumer(group, EXPERIENCE_CANDY_M)
            consumer(group, EXPERIENCE_CANDY_L)
            consumer(group, EXPERIENCE_CANDY_XL)
            consumer(group, RARE_CANDY)
            consumer(group, ABILITY_PATCH)

            consumer(group, LONELY_MINT)
            consumer(group, ADAMANT_MINT)
            consumer(group, NAUGHTY_MINT)
            consumer(group, BRAVE_MINT)
            consumer(group, BOLD_MINT)
            consumer(group, IMPISH_MINT)
            consumer(group, LAX_MINT)
            consumer(group, RELAXED_MINT)
            consumer(group, MODEST_MINT)
            consumer(group, MILD_MINT)
            consumer(group, RASH_MINT)
            consumer(group, QUIET_MINT)
            consumer(group, CALM_MINT)
            consumer(group, GENTLE_MINT)
            consumer(group, CAREFUL_MINT)
            consumer(group, SASSY_MINT)
            consumer(group, TIMID_MINT)
            consumer(group, HASTY_MINT)
            consumer(group, JOLLY_MINT)
            consumer(group, NAIVE_MINT)
            consumer(group, SERIOUS_MINT)

            consumer(group, PEWTER_CRUNCHIES)
            consumer(group, RAGE_CANDY_BAR)
            consumer(group, LAVA_COOKIE)
            consumer(group, OLD_GATEAU)
            consumer(group, CASTELIACONE)
            consumer(group, LUMIOSE_GALETTE)
            consumer(group, SHALOUR_SABLE)
            consumer(group, BIG_MALASADA)
            consumer(group, JUBILIFE_MUFFIN)
        }

        CobblemonItemGroups.EVOLUTION_ITEMS.group().let { group ->
            consumer(group, FIRE_STONE)
            consumer(group, WATER_STONE)
            consumer(group, THUNDER_STONE)
            consumer(group, LEAF_STONE)
            consumer(group, MOON_STONE)
            consumer(group, SUN_STONE)
            consumer(group, SHINY_STONE)
            consumer(group, DUSK_STONE)
            consumer(group, DAWN_STONE)
            consumer(group, ICE_STONE)
            consumer(group, LINK_CABLE)
            consumer(group, KINGS_ROCK)
            consumer(group, GALARICA_CUFF)
            consumer(group, GALARICA_WREATH)
            consumer(group, METAL_COAT)
            consumer(group, BLACK_AUGURITE)
            consumer(group, PROTECTOR)
            consumer(group, OVAL_STONE)
            consumer(group, DRAGON_SCALE)
            consumer(group, ELECTIRIZER)
            consumer(group, MAGMARIZER)
            consumer(group, UPGRADE)
            consumer(group, DUBIOUS_DISC)
            consumer(group, RAZOR_FANG)
            consumer(group, RAZOR_CLAW)
            consumer(group, PEAT_BLOCK)
            consumer(group, PRISM_SCALE)
            consumer(group, REAPER_CLOTH)
            consumer(group, DEEP_SEA_TOOTH)
            consumer(group, DEEP_SEA_SCALE)
            consumer(group, SACHET)
            consumer(group, WHIPPED_DREAM)
            consumer(group, TART_APPLE)
            consumer(group, SWEET_APPLE)
            consumer(group, CRACKED_POT)
            consumer(group, CHIPPED_POT)
            consumer(group, STRAWBERRY_SWEET)
            consumer(group, LOVE_SWEET)
            consumer(group, BERRY_SWEET)
            consumer(group, CLOVER_SWEET)
            consumer(group, FLOWER_SWEET)
            consumer(group, STAR_SWEET)
            consumer(group, RIBBON_SWEET)
        }
    }
}