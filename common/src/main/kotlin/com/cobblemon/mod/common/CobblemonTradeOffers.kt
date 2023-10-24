/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import net.minecraft.registry.Registries
import net.minecraft.village.TradeOffers
import net.minecraft.village.VillagerData
import net.minecraft.village.VillagerProfession

/**
 * A generator for various trade offers in the mod.
 */
object CobblemonTradeOffers {

    /**
     * Creates a list of all Cobblemon trade offers for every villager profession.
     *
     * @return The resulting list.
     */
    fun tradeOffersForAll(): List<VillagerTradeOffer> = Registries.VILLAGER_PROFESSION.map(this::tradeOffersFor).flatten()

    /**
     * Creates a list of all Cobblemon trade offers for the given [VillagerProfession].
     *
     * @param profession The [VillagerProfession] being queried.
     * @return The resulting list.
     */
    fun tradeOffersFor(profession: VillagerProfession): List<VillagerTradeOffer> = when (profession) {
        VillagerProfession.FARMER -> listOf(VillagerTradeOffer(VillagerProfession.FARMER, 3, listOf(TradeOffers.SellItemFactory(CobblemonItems.VIVICHOKE_SEEDS, 24, 1, 1, 6))))
        else -> emptyList()
    }

    /**
     * Creates a list of all Cobblemon trade offers for the Wandering trader.
     *
     * @return The resulting list.
     */
    fun resolveWanderingTradeOffers(): List<WandererTradeOffer> = listOf(
        WandererTradeOffer(false, listOf(TradeOffers.SellItemFactory(CobblemonItems.VIVICHOKE_SEEDS, 24, 1, 1, 6)))
    )

    /**
     * Represents a trade offer that can be attached to a villager or a wandering trader.
     */
    interface TradeOfferHolder {
        /**
         * The list of the possible [TradeOffers.Factory].
         */
        val tradeOffers: List<TradeOffers.Factory>
    }

    /**
     * Represents a trade offer from a villager.
     *
     * @property profession The target profession for this trade.
     * @property requiredLevel The level required must be a possible level.
     * @property tradeOffers The list of the possible [TradeOffers.Factory].
     *
     * @throws IllegalArgumentException If the [requiredLevel] is not within the bounds of [VillagerData.MIN_LEVEL] & [VillagerData.MAX_LEVEL].
     */
    data class VillagerTradeOffer(
        val profession: VillagerProfession,
        val requiredLevel: Int,
        override val tradeOffers: List<TradeOffers.Factory>
    ) : TradeOfferHolder {

        init {
            if (this.requiredLevel < VillagerData.MIN_LEVEL || this.requiredLevel > VillagerData.MAX_LEVEL) {
                throw IllegalArgumentException("${this.requiredLevel} is not a valid level for a villager trade accepted range is ${VillagerData.MIN_LEVEL}-${VillagerData.MAX_LEVEL}")
            }
        }

    }

    /**
     * Represents a trade offer from a wandering trader.
     *
     * @property isRareTrade If this is rare or common trade.
     * @property tradeOffers The list of the possible [TradeOffers.Factory].
     */
    data class WandererTradeOffer(
        val isRareTrade: Boolean,
        override val tradeOffers: List<TradeOffers.Factory>
    ) : TradeOfferHolder
    
}