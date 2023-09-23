/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.mixin;

import com.cobblemon.mod.forge.net.ExtendedChannel;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Channel.class)
public abstract class ChannelMixin implements ExtendedChannel {

    @Override
    public @NotNull Packet<?> createVanillaPacket(@NotNull NetworkDirection direction, @NotNull Object message) {
        return direction.buildPacket(this.toBuffer(message), getName()).getThis();
    }

    @Shadow
    protected abstract PacketByteBuf toBuffer(Object message);

    @Shadow
    public abstract Identifier getName();

}
