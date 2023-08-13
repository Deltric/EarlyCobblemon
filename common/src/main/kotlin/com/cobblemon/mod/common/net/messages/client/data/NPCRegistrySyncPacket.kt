package com.cobblemon.mod.common.net.messages.client.data

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.npc.NPCClass
import com.cobblemon.mod.common.api.npc.NPCClasses
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

// We do not need to know every single attribute as a client, as such, we only sync the aspects that matter
class NPCRegistrySyncPacket(npcs: Collection<NPCClass>) : DataRegistrySyncPacket<NPCClass, NPCRegistrySyncPacket>(npcs) {

    override val id = ID

    override fun encodeEntry(buffer: PacketByteBuf, entry: NPCClass) {
        try {
            buffer.writeIdentifier(entry.resourceIdentifier)
            entry.encode(buffer)
        } catch (e: Exception) {
            Cobblemon.LOGGER.error("Caught exception encoding the NPC class {}", entry.resourceIdentifier, e)
        }
    }

    override fun decodeEntry(buffer: PacketByteBuf): NPCClass? {
        val identifier = buffer.readIdentifier()
        val npc = NPCClass()
        npc.resourceIdentifier = identifier
        return try {
            npc.decode(buffer)
            npc
        } catch (e: Exception) {
            Cobblemon.LOGGER.error("Caught exception decoding the NPC class {}", identifier, e)
            null
        }
    }

    override fun synchronizeDecoded(entries: Collection<NPCClass>) {
        NPCClasses.reload(entries.associateBy { it.resourceIdentifier })
    }

    companion object {
        val ID = cobblemonResource("npcs_sync")
        fun decode(buffer: PacketByteBuf): NPCRegistrySyncPacket = NPCRegistrySyncPacket(emptyList()).apply { decodeBuffer(buffer) }
    }
}