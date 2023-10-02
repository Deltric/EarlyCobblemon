package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(InfestedBlock.class)
public abstract class InfestedBlockMixin {
    @Inject(method = "spawnSilverfish", at = @At(value = "HEAD"), cancellable = true)
    public void cobblemon$doSilverfishSpawn(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (!Cobblemon.config.getVanillaSpawning().get("silverfish")) {
            System.out.println("cancelled");
            ci.cancel();
        }
    }
}
