/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

object DataKeys {
    const val POKEMON_LAST_SAVED_VERSION = "CobblemonVersion"
    const val POKEMON = "Pokemon"
    const val POKEMON_UUID = "UUID"
    const val POKEMON_SPECIES_IDENTIFIER = "Species"
    const val POKEMON_NICKNAME = "Nickname"
    const val POKEMON_FORM_ID = "FormId"
    const val POKEMON_LEVEL = "Level"
    const val POKEMON_GENDER = "Gender"
    const val POKEMON_EXPERIENCE = "Experience"
    const val POKEMON_FRIENDSHIP = "Friendship"

    const val POKEMON_STATS = "Stats"
    const val POKEMON_IVS = "IVs"
    const val POKEMON_EVS = "EVs"
    const val POKEMON_HEALTH = "Health"
    const val POKEMON_SCALE_MODIFIER = "ScaleModifier"
    const val POKEMON_MOVESET = "MoveSet"
    const val POKEMON_MOVESET_MOVENAME = "MoveName"
    const val POKEMON_MOVESET_MOVEPP = "MovePP"
    const val POKEMON_MOVESET_RAISED_PP_STAGES = "RaisedPPStages"
    const val POKEMON_ABILITY = "Ability"
    const val POKEMON_ABILITY_NAME = "AbilityName"
    const val POKEMON_ABILITY_FORCED = "AbilityForced"
    const val POKEMON_ABILITY_INDEX = "AbilityIndex"
    const val POKEMON_ABILITY_PRIORITY = "AbilityPriority"
    const val POKEMON_SHINY = "Shiny"
    const val POKEMON_STATUS = "Status"
    const val POKEMON_STATUS_NAME = "StatusName"
    const val POKEMON_STATUS_TIMER = "StatusTimer"
    const val POKEMON_CAUGHT_BALL = "CaughtBall"
    const val POKEMON_FAINTED_TIMER = "FaintedTimer"
    const val POKEMON_HEALING_TIMER = "HealingTimer"
    const val POKEMON_DATA = "PokemonData"
    const val POKEMON_NATURE = "Nature"
    const val POKEMON_MINTED_NATURE = "MintedNature"
    const val HELD_ITEM = "HeldItem"
    const val POKEMON_TERA_TYPE = "TeraType"
    const val POKEMON_DMAX_LEVEL = "DmaxLevel"
    const val POKEMON_GMAX_FACTOR = "GmaxFactor"
    const val POKEMON_TRADEABLE = "Tradeable"

    const val POKEMON_STATE = "State"
    const val POKEMON_STATE_TYPE = "StateType"
    const val POKEMON_STATE_SHOULDER = "StateShoulder"
    const val POKEMON_STATE_ID = "StateId"
    const val POKEMON_STATE_PLAYER_UUID = "PlayerUUID"
    const val POKEMON_STATE_POKEMON_UUID = "PokemonUUID"

    const val POKEMON_BATTLE_ID = "BattleId"
    const val POKEMON_POSE_TYPE = "PoseType"
    const val POKEMON_BEHAVIOUR_FLAGS = "BehaviourFlags"
    const val POKEMON_OWNER_ID = "PokemonOwnerId"
    const val POKEMON_HIDE_LABEL = "HideLabel"
    const val POKEMON_UNBATTLEABLE = "Unbattleable"

    // Evolution stuff
    const val POKEMON_EVOLUTIONS = "Evolutions"
    const val POKEMON_PENDING_EVOLUTIONS = "Pending"

    const val BENCHED_MOVES = "BenchedMoves"

    const val PC_ID = "PCId"
    const val STORE_SLOT = "Slot"
    const val STORE_SLOT_COUNT = "SlotCount"
    const val STORE_BOX = "Box"
    const val STORE_BOX_COUNT = "BoxCount"
    const val STORE_BOX_COUNT_LOCKED = "BoxCountLocked"
    const val STORE_BACKUP = "BackupStore"

    const val TETHER_OWNER_ID = "TetherOwnerId"
    const val TETHER_OWNER_NAME = "TetherOwnerName"
    const val TETHERING_ID = "TetheringId"
    const val TETHER_POKEMON = "TetherPokemon"
    const val TETHER_MIN_ROAM_POS = "TetherMinRoamPos"
    const val TETHER_MAX_ROAM_POS = "TetherMaxRoamPos"
    const val TETHER_COUNT = "TetherCount"
    const val TETHERING = "Tethering"
    const val TETHERING_POS = "Pos"
    const val TETHERING_PLAYER_ID = "PlayerId"
    const val TETHERING_ENTITY_ID = "EntityId"

    /* Form stuff */
    const val ALOLAN = "alolan"
    const val GALARIAN = "galarian"
    const val HISUIAN = "hisuian"
    const val VALENCIAN = "valencian"
    const val CRYSTAL = "crystal"
    /* ---------- */

    const val POKEMON_PROPERTIES = "Properties"
    const val POKEMON_PROPERTIES_CUSTOM = "CustomProperties"
    const val POKEMON_PROPERTIES_ORIGINAL_TEXT = "OriginalText"
    const val POKEMON_SPECIES_TEXT = "SpeciesText"

    /* Healer  Block */
    const val HEALER_MACHINE_USER = "MachineUser"
    const val HEALER_MACHINE_POKEBALLS = "MachinePokeBalls"
    const val HEALER_MACHINE_TIME_LEFT = "MachineTimeLeft"
    const val HEALER_MACHINE_CHARGE = "MachineCharge"
    const val HEALER_MACHINE_INFINITE = "MachineInfinite"
    /* ----------- */

    // Pokemon Item
    const val POKEMON_ITEM_SPECIES = "species"
    const val POKEMON_ITEM_ASPECTS = "aspects"
    const val POKEMON_ITEM_TINT_RED = "TintRed"
    const val POKEMON_ITEM_TINT_GREEN = "TintGreen"
    const val POKEMON_ITEM_TINT_BLUE = "TintBlue"
    const val POKEMON_ITEM_TINT_ALPHA = "TintAlpha"

    // Features
    const val CAN_BE_MILKED = "milkable"
    const val HAS_BEEN_SHEARED = "sheared"

    // Persistent Data
    const val POKEMON_PERSISTENT_DATA = "PersistentData"

    // Item Tooltips
    const val HIDE_TOOLTIP = "HideTooltip"


    // Shoulder Mount
    const val SHOULDER_UUID = "shoulder_uuid"
    const val SHOULDER_SPECIES = "shoulder_species"
    const val SHOULDER_FORM = "shoulder_form"
    const val SHOULDER_ASPECTS = "shoulder_aspects"
    const val SHOULDER_SCALE_MODIFIER = "shoulder_scale"

}