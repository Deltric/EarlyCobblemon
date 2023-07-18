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
import com.cobblemon.mod.common.mixin.invoker.*
import com.cobblemon.mod.common.platform.PlatformRegistry
import net.minecraft.block.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.intprovider.UniformIntProvider

@Suppress("SameParameterValue", "HasPlatformType", "MemberVisibilityCanBePrivate")
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
    val MEDICINAL_LEEK_CROP = this.create("medicinal_leek_crop", MedicinalLeekBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).burnable().mapColor(MapColor.DULL_RED).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP)))
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
    val APRICORN_SIGN = this.create("apricorn_sign", ApricornSignBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).burnable().sounds(BlockSoundGroup.HANGING_SIGN), APRICORN_WOOD_TYPE))
    @JvmField
    val APRICORN_HANGING_SIGN = this.create("apricorn_hanging_sign", ApricornHangingSignBlock(AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.DESTROY).burnable().sounds(BlockSoundGroup.HANGING_SIGN), APRICORN_WOOD_TYPE))

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

    /**
     * Helper method for creating logs
     * copied over from Vanilla
     */
    private fun log(name: String, arg: MapColor = MapColor.DIRT_BROWN, arg2: MapColor = MapColor.DIRT_BROWN): PillarBlock {
        val block = BlocksInvoker.createLogBlock(arg, arg2)
        return this.create(name, block)
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