/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin.invoker;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.FireBlock;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockInvoker {
    @Invoker("registerFlammableBlock") void registerNewFlammableBlock(Block block, int burnChance, int spreadChance);
}
