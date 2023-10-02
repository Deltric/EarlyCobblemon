package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerLogic.class)
public abstract class MobSpawnerLogicMixin {

    // Disable mob spawner functionality when they are disabled in the config
    @Inject(method = "serverTick", at = @At(value = "HEAD"), cancellable = true)
    public void cobblemon$doSpawnerTick(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (!Cobblemon.config.getVanillaSpawning().get("spawners")) ci.cancel();
    }
}
