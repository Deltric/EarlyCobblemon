/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.storage.player.factory

import com.cobblemon.mod.common.api.storage.player.PlayerData
import com.cobblemon.mod.common.api.storage.player.adapter.MongoPlayerDataAdapter
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.removeIf
import com.mongodb.client.MongoClient
import java.util.UUID

class MongoPlayerDataStoreFactory(mongoClient: MongoClient, databaseName: String) : PlayerDataStoreFactory {

    private val cache = mutableMapOf<UUID, PlayerData>()
    private val adapter = MongoPlayerDataAdapter(mongoClient, databaseName)

    override fun load(uuid: UUID): PlayerData {
        return if (cache.contains(uuid))
            cache[uuid]!!;
        else {
            val data = adapter.load(uuid);
            cache[uuid] = data
            data
        }
    }

    override fun save(data: PlayerData) {
        adapter.save(data)
    }

    override fun saveAll() {
        cache.forEach { (_, pd) -> adapter.save(pd) }
        cache.removeIf { (uuid, _) -> uuid.getPlayer() == null }
    }

    override fun onPlayerDisconnect(uuid: UUID) {
        cache.remove(uuid)
    }
}
