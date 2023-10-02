package com.cobblemon.mod.common.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LargeEntitySpawnHelper.class)
public abstract class LargeEntitySpawnHelperMixin {
//    @Inject(method = "trySpawnAt", at = @At(value = "HEAD"))
//    private static void cobblemon$doLargeEntitySpawns(EntityType<T> entityType, SpawnReason reason, ServerWorld world, BlockPos pos, int tries, int horizontalRange, int verticalRange, LargeEntitySpawnHelper.Requirements requirements, CallbackInfoReturnable<Optional<T>> cir) {
//        //not sure how to check the entity type here (we want a separate check for golems and wardens)
//    }
}
