/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.apricorn.Apricorn
import com.cobblemon.mod.common.block.*
import com.cobblemon.mod.common.block.MintBlock.MintType
import com.cobblemon.mod.common.mixin.invoker.DoorBlockInvoker
import com.cobblemon.mod.common.mixin.invoker.PressurePlateBlockInvoker
import com.cobblemon.mod.common.mixin.invoker.StairsBlockInvoker
import com.cobblemon.mod.common.mixin.invoker.TrapdoorBlockInvoker
import com.cobblemon.mod.common.mixin.invoker.*
import com.cobblemon.mod.common.platform.PlatformRegistry
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.block.BerryBlock
import net.minecraft.block.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.intprovider.UniformIntProvider

@Suppress("SameParameterValue", "HasPlatformType", "MemberVisibilityCanBePrivate", "unused")
object CobblemonBlocks : PlatformRegistry<Registry<Block>, RegistryKey<Registry<Block>>, Block>() {



    override val registry: Registry<Block> = Registries.BLOCK
    override val registryKey: RegistryKey<Registry<Block>> = RegistryKeys.BLOCK

    val APRICORN_BLOCK_SET_TYPE = BlockSetType("apricorn")
    val APRICORN_WOOD_TYPE = WoodType.register(WoodType("apricorn", APRICORN_BLOCK_SET_TYPE))

    // Evolution Ores
    @JvmField
    val DAWN_STONE_ORE = evolutionStoneOre("dawn_stone_ore")
    @JvmField
    val DUSK_STONE_ORE = evolutionStoneOre("dusk_stone_ore")
    @JvmField
    val FIRE_STONE_ORE = evolutionStoneOre("fire_stone_ore")
    @JvmField
    val ICE_STONE_ORE = evolutionStoneOre("ice_stone_ore")
    @JvmField
    val LEAF_STONE_ORE = evolutionStoneOre("leaf_stone_ore")
    @JvmField
    val MOON_STONE_ORE = evolutionStoneOre("moon_stone_ore")
    @JvmField
    val DRIPSTONE_MOON_STONE_ORE = evolutionStoneOre("dripstone_moon_stone_ore")
    @JvmField
    val SHINY_STONE_ORE = evolutionStoneOre("shiny_stone_ore")
    @JvmField
    val SUN_STONE_ORE = evolutionStoneOre("sun_stone_ore")
    @JvmField
    val THUNDER_STONE_ORE = evolutionStoneOre("thunder_stone_ore")
    @JvmField
    val WATER_STONE_ORE = evolutionStoneOre("water_stone_ore")

    @JvmField
    val DEEPSLATE_DAWN_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_dawn_stone_ore")
    @JvmField
    val DEEPSLATE_DUSK_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_dusk_stone_ore")
    @JvmField
    val DEEPSLATE_FIRE_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_fire_stone_ore")
    @JvmField
    val DEEPSLATE_ICE_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_ice_stone_ore")
    @JvmField
    val DEEPSLATE_LEAF_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_leaf_stone_ore")
    @JvmField
    val DEEPSLATE_MOON_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_moon_stone_ore")
    @JvmField
    val DEEPSLATE_SHINY_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_shiny_stone_ore")
    @JvmField
    val DEEPSLATE_SUN_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_sun_stone_ore")
    @JvmField
    val DEEPSLATE_THUNDER_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_thunder_stone_ore")
    @JvmField
    val DEEPSLATE_WATER_STONE_ORE = this.deepslateEvolutionStoneOre("deepslate_water_stone_ore")

    // Apricorns
    @JvmField
    val APRICORN_LOG = log("apricorn_log", arg2 = MapColor.BROWN)
    @JvmField
    val STRIPPED_APRICORN_LOG = log("stripped_apricorn_log")
    @JvmField
    val APRICORN_WOOD = log("apricorn_wood")
    @JvmField
    val STRIPPED_APRICORN_WOOD = log("stripped_apricorn_wood")
    @JvmField
    val APRICORN_PLANKS = this.create("apricorn_planks", Block(AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)))
    @JvmField
    val APRICORN_LEAVES = leaves("apricorn_leaves")
    @JvmField
    val APRICORN_FENCE = this.create("apricorn_fence", FenceBlock(AbstractBlock.Settings.create().mapColor(APRICORN_PLANKS.defaultMapColor).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)))
    @JvmField
    val APRICORN_FENCE_GATE = this.create("apricorn_fence_gate", FenceGateBlock(AbstractBlock.Settings.create().mapColor(APRICORN_PLANKS.defaultMapColor).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD), APRICORN_WOOD_TYPE))
    @JvmField // Note At the time of 1.20.0 we don't need our own BlockSetType for Apricorn wood
    val APRICORN_BUTTON = this.create("apricorn_button", BlocksInvoker.createWoodenButtonBlock(BlockSetType.OAK))
    @JvmField
    val APRICORN_PRESSURE_PLATE = this.create("apricorn_pressure_plate", PressurePlateBlockInvoker.`cobblemon$create`(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.create().mapColor(APRICORN_PLANKS.defaultMapColor).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD), APRICORN_BLOCK_SET_TYPE))
    // Tag was removed be sure to add it back when implemented
    //val APRICORN_SIGN = queue("apricorn_sign") { StandingSignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollission().strength(1.0f).sounds(BlockSoundGroup.WOOD), APRICORN_WOOD_TYPE) }
    //val APRICORN_WALL_SIGN = queue("apricorn_wall_sign") { WallSignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollission().strength(1.0f).sounds(BlockSoundGroup.WOOD).dropsLike(APRICORN_SIGN), APRICORN_WOOD_TYPE) }
    @JvmField
    val APRICORN_SLAB = this.create("apricorn_slab", SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)))
    @JvmField
    val APRICORN_STAIRS = this.create("apricorn_stairs", StairsBlockInvoker.`cobblemon$create`(APRICORN_PLANKS.defaultState, AbstractBlock.Settings.copy(APRICORN_PLANKS)))
    @JvmField
    val APRICORN_DOOR = this.create("apricorn_door", DoorBlockInvoker.`cobblemon$create`(AbstractBlock.Settings.create().mapColor(APRICORN_PLANKS.defaultMapColor).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque(), APRICORN_BLOCK_SET_TYPE))
    @JvmField
    val APRICORN_TRAPDOOR = this.create("apricorn_trapdoor", TrapdoorBlockInvoker.`cobblemon$create`(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning { _, _, _, _ -> false }, APRICORN_BLOCK_SET_TYPE))

    private val PLANT_PROPERTIES = AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS).pistonBehavior(PistonBehavior.DESTROY)
    @JvmField
    val BLACK_APRICORN_SAPLING = this.create("black_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, Apricorn.BLACK))
    @JvmField
    val BLUE_APRICORN_SAPLING = this.create("blue_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, Apricorn.BLUE))
    @JvmField
    val GREEN_APRICORN_SAPLING = this.create("green_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, Apricorn.GREEN))
    @JvmField
    val PINK_APRICORN_SAPLING = this.create("pink_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, Apricorn.PINK))
    @JvmField
    val RED_APRICORN_SAPLING = this.create("red_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, Apricorn.RED))
    @JvmField
    val WHITE_APRICORN_SAPLING = this.create("white_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, Apricorn.WHITE))
    @JvmField
    val YELLOW_APRICORN_SAPLING = this.create("yellow_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, Apricorn.YELLOW))

    @JvmField
    val MEDICINAL_LEEK = this.create("medicinal_leek", MedicinalLeekBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).burnable().mapColor(MapColor.DULL_RED).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
    @JvmField
    val ENERGY_ROOT = this.create("energy_root", EnergyRootBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).burnable().mapColor(MapColor.DIRT_BROWN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.ROOTS)))
    @JvmField
    val BIG_ROOT = this.create("big_root", BigRootBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).burnable().mapColor(MapColor.DARK_GREEN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.ROOTS)))
    @JvmField
    val REVIVAL_HERB = this.create("revival_herb", RevivalHerbBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).mapColor(MapColor.DARK_GREEN).burnable().noCollision().breakInstantly().sounds(BlockSoundGroup.CROP)))

    @JvmField
    val BLACK_APRICORN = apricornBlock("black_apricorn", Apricorn.BLACK)
    @JvmField
    val BLUE_APRICORN = apricornBlock("blue_apricorn", Apricorn.BLUE)
    @JvmField
    val GREEN_APRICORN = apricornBlock("green_apricorn", Apricorn.GREEN)
    @JvmField
    val PINK_APRICORN = apricornBlock("pink_apricorn", Apricorn.PINK)
    @JvmField
    val RED_APRICORN = apricornBlock("red_apricorn", Apricorn.RED)
    @JvmField
    val WHITE_APRICORN = apricornBlock("white_apricorn", Apricorn.WHITE)
    @JvmField
    val YELLOW_APRICORN = apricornBlock("yellow_apricorn", Apricorn.YELLOW)

    @JvmField
    val HEALING_MACHINE = create(
        "healing_machine",
        HealingMachineBlock(
            AbstractBlock.Settings.create()
                .mapColor(MapColor.IRON_GRAY)
                .sounds(BlockSoundGroup.METAL)
                .pistonBehavior(PistonBehavior.BLOCK)
                .strength(2f)
                .nonOpaque()
                .luminance { if (it.get(HealingMachineBlock.CHARGE_LEVEL) >= HealingMachineBlock.MAX_CHARGE_LEVEL) 7 else 2 }
        )
    )
    @JvmField
    val PC = create(
        "pc",
        PCBlock(
            AbstractBlock.Settings.create()
                .mapColor(MapColor.IRON_GRAY)
                .sounds(BlockSoundGroup.METAL)
                .pistonBehavior(PistonBehavior.BLOCK)
                .strength(2F)
                .nonOpaque()
                .luminance { if ((it.get(PCBlock.ON) as Boolean) && (it.get(PCBlock.PART) == PCBlock.PCPart.TOP)) 10 else 0 }
        )
    )

    @JvmField
    val RED_MINT = create("red_mint", MintBlock(MintType.RED, AbstractBlock.Settings.create().mapColor(MapColor.RED).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
    @JvmField
    val BLUE_MINT = create("blue_mint", MintBlock(MintType.BLUE, AbstractBlock.Settings.create().mapColor(MapColor.BLUE).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
    @JvmField
    val CYAN_MINT = create("cyan_mint", MintBlock(MintType.CYAN, AbstractBlock.Settings.create().mapColor(MapColor.CYAN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
    @JvmField
    val PINK_MINT = create("pink_mint", MintBlock(MintType.PINK, AbstractBlock.Settings.create().mapColor(MapColor.PINK).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
    @JvmField
    val GREEN_MINT = create("green_mint", MintBlock(MintType.GREEN, AbstractBlock.Settings.create().mapColor(MapColor.GREEN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
    @JvmField
    val WHITE_MINT = create("white_mint", MintBlock(MintType.WHITE, AbstractBlock.Settings.create().mapColor(MapColor.WHITE).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))

    @JvmField
    val PASTURE = create(
        "pasture",
        PastureBlock(
            AbstractBlock.Settings.create()
                .mapColor(MapColor.BROWN)
                .sounds(BlockSoundGroup.WOOD)
                .strength(2F)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.BLOCK)
                .luminance { if ((it.get(PastureBlock.ON) as Boolean) && (it.get(PastureBlock.PART) == PastureBlock.PasturePart.TOP)) 10 else 0 }
        )
    )

    @JvmField
    val VIVICHOKE_SEEDS = this.create("vivichoke_seeds", VivichokeBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).burnable().mapColor(MapColor.DARK_GREEN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
    @JvmField
    val PEP_UP_FLOWER = this.create("pep_up_flower", FlowerBlock(StatusEffects.LEVITATION, 10, AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XZ).pistonBehavior(PistonBehavior.DESTROY)))
    @JvmField
    val POTTED_PEP_UP_FLOWER = this.create("potted_pep_up_flower", BlocksInvoker.createFlowerPotBlock(PEP_UP_FLOWER))

    /**
     * Returns a map of all the blocks that can be stripped with an axe in the format of input - output.
     *
     * @return A map of all the blocks that can be stripped with an axe in the format of input - output.
     */
    fun strippedBlocks(): Map<Block, Block> = mapOf(
        APRICORN_WOOD to STRIPPED_APRICORN_WOOD,
        APRICORN_LOG to STRIPPED_APRICORN_LOG
    )



    private fun apricornBlock(name: String, apricorn: Apricorn): ApricornBlock = this.create(name, ApricornBlock(AbstractBlock.Settings.create().mapColor(apricorn.mapColor()).ticksRandomly().strength(Blocks.OAK_LOG.hardness, Blocks.OAK_LOG.blastResistance).sounds(BlockSoundGroup.WOOD).nonOpaque(), apricorn))

    private val berries = mutableMapOf<Identifier, BerryBlock>()

    val AGUAV_BERRY = this.berryBlock("aguav")
    val APICOT_BERRY = this.berryBlock("apicot")
    val ASPEAR_BERRY = this.berryBlock("aspear")
    val BABIRI_BERRY = this.berryBlock("babiri")
    val BELUE_BERRY = this.berryBlock("belue")
    val BLUK_BERRY = this.berryBlock("bluk")
    val CHARTI_BERRY = this.berryBlock("charti")
    val CHERI_BERRY = this.berryBlock("cheri")
    val CHESTO_BERRY = this.berryBlock("chesto")
    val CHILAN_BERRY = this.berryBlock("chilan")
    val CHOPLE_BERRY = this.berryBlock("chople")
    val COBA_BERRY = this.berryBlock("coba")
    val COLBUR_BERRY = this.berryBlock("colbur")
    val CORNN_BERRY = this.berryBlock("cornn")
    val CUSTAP_BERRY = this.berryBlock("custap")
    val DURIN_BERRY = this.berryBlock("durin")
    val ENIGMA_BERRY = this.berryBlock("enigma")
    val FIGY_BERRY = this.berryBlock("figy")
    val GANLON_BERRY = this.berryBlock("ganlon")
    val GREPA_BERRY = this.berryBlock("grepa")
    val HABAN_BERRY = this.berryBlock("haban")
    val HONDEW_BERRY = this.berryBlock("hondew")
    val HOPO_BERRY = this.berryBlock("hopo")
    val IAPAPA_BERRY = this.berryBlock("iapapa")
    val JABOCA_BERRY = this.berryBlock("jaboca")
    val KASIB_BERRY = this.berryBlock("kasib")
    val KEBIA_BERRY = this.berryBlock("kebia")
    val KEE_BERRY = this.berryBlock("kee")
    val KELPSY_BERRY = this.berryBlock("kelpsy")
    val LANSAT_BERRY = this.berryBlock("lansat")
    val LEPPA_BERRY = this.berryBlock("leppa")
    val LIECHI_BERRY = this.berryBlock("liechi")
    val LUM_BERRY = this.berryBlock("lum")
    val MAGO_BERRY = this.berryBlock("mago")
    val MAGOST_BERRY = this.berryBlock("magost")
    val MARANGA_BERRY = this.berryBlock("maranga")
    val MICLE_BERRY = this.berryBlock("micle")
    val NANAB_BERRY = this.berryBlock("nanab")
    val NOMEL_BERRY = this.berryBlock("nomel")
    val OCCA_BERRY = this.berryBlock("occa")
    val ORAN_BERRY = this.berryBlock("oran")
    val PAMTRE_BERRY = this.berryBlock("pamtre")
    val PASSHO_BERRY = this.berryBlock("passho")
    val PAYAPA_BERRY = this.berryBlock("payapa")
    val PECHA_BERRY = this.berryBlock("pecha")
    val PERSIM_BERRY = this.berryBlock("persim")
    val PETAYA_BERRY = this.berryBlock("petaya")
    val PINAP_BERRY = this.berryBlock("pinap")
    val POMEG_BERRY = this.berryBlock("pomeg")
    val QUALOT_BERRY = this.berryBlock("qualot")
    val RABUTA_BERRY = this.berryBlock("rabuta")
    val RAWST_BERRY = this.berryBlock("rawst")
    val RAZZ_BERRY = this.berryBlock("razz")
    val RINDO_BERRY = this.berryBlock("rindo")
    val ROSELI_BERRY = this.berryBlock("roseli")
    val ROWAP_BERRY = this.berryBlock("rowap")
    val SALAC_BERRY = this.berryBlock("salac")
    val SHUCA_BERRY = this.berryBlock("shuca")
    val SITRUS_BERRY = this.berryBlock("sitrus")
    val SPELON_BERRY = this.berryBlock("spelon")
    val STARF_BERRY = this.berryBlock("starf")
    val TAMATO_BERRY = this.berryBlock("tamato")
    val TANGA_BERRY = this.berryBlock("tanga")
    val TOUGA_BERRY = this.berryBlock("touga")
    val WACAN_BERRY = this.berryBlock("wacan")
    val WATMEL_BERRY = this.berryBlock("watmel")
    val WEPEAR_BERRY = this.berryBlock("wepear")
    val WIKI_BERRY = this.berryBlock("wiki")
    val YACHE_BERRY = this.berryBlock("yache")

    init {
        /**
         * Makes all blocks in array flammable by adding them to FireBlock's flammableRegistry.
         * second value is burn chance and third value is spread chance
         */
        arrayOf(
            Triple(APRICORN_LOG, 5, 5),
            Triple(STRIPPED_APRICORN_LOG, 5, 5),
            Triple(APRICORN_WOOD, 5, 5),
            Triple(STRIPPED_APRICORN_WOOD, 5, 5),
            Triple(APRICORN_PLANKS, 5, 20),
            Triple(APRICORN_LEAVES, 30, 60),
            Triple(APRICORN_FENCE, 5, 20),
            Triple(APRICORN_FENCE_GATE, 5, 20),
            Triple(APRICORN_SLAB, 5, 20),
            Triple(APRICORN_STAIRS, 5, 20)
        ).onEach{ data -> setFlammable(data.first, data.second, data.third) }
    }

    fun berries() = this.berries.toMap()

    private fun berryBlock(name: String): BerryBlock {
        val identifier = cobblemonResource("${name}_berry")
        val block = this.create(identifier.path, BerryBlock(identifier, AbstractBlock.Settings.copy(Blocks.WHEAT).dynamicBounds().sounds(BlockSoundGroup.CROP).strength(0.2F)))
        this.berries[identifier] = block
        return block
    }

    /**
     * Calls helper method from Vanilla
     */
    private fun log(name: String, arg: MapColor = MapColor.DIRT_BROWN, arg2: MapColor = MapColor.DIRT_BROWN): PillarBlock {
        val block = BlocksInvoker.createLogBlock(arg, arg2)
        return this.create(name, block)
    }

    /**
     * Method uses generic E in order to keep the block as the same return type.
     * If E is not a block then it will not be set as flammable.
     * Calls Vanilla implementation of registering a flammable block.
     * Mixins looks cursed but it is java's fault.
     */
    private fun <E> setFlammable(block: E, burnChance: Int, spreadChance: Int): E {
        if(block !is Block) return block

        var fireBlock: FireBlock =  Blocks.FIRE as FireBlock
        //Cursed Mixin stuff
        (fireBlock as FireBlockInvoker).registerNewFlammableBlock(block as Block, burnChance, spreadChance)
        return block
    }

    private fun evolutionStoneOre(name: String) = this.create(name, ExperienceDroppingBlock(AbstractBlock.Settings.copy(Blocks.IRON_ORE), UniformIntProvider.create(1, 2)))

    private fun deepslateEvolutionStoneOre(name: String) = this.create(name, ExperienceDroppingBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE), UniformIntProvider.create(1, 2)))

    /**
     * Helper method for creating leaves
     * copied over from Vanilla
     */
    private fun leaves(name: String): LeavesBlock {
        val block = BlocksInvoker.createLeavesBlock(BlockSoundGroup.GRASS)
        return this.create(name, block)
    }
}
