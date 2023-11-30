/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin.invoker;

import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unused")
@Mixin(Blocks.class)
public interface BlocksInvoker {

    @Invoker("createLogBlock")
    static Block createLogBlock(MapColor topMapColor, MapColor sideMapColor) {
        throw new UnsupportedOperationException();
    }

    @Invoker("createLeavesBlock")
    static Block createLeavesBlock(BlockSoundGroup soundGroup) {
        throw new UnsupportedOperationException();
    }

    @Invoker("createWoodenButtonBlock")
    static Block createWoodenButtonBlock(BlockSetType blockSetType) {
        throw new UnsupportedOperationException();
    }

    @Invoker("createFlowerPotBlock")
    static Block createFlowerPotBlock(Block flower) {
        throw new UnsupportedOperationException();
    }

}