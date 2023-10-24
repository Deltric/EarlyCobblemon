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
