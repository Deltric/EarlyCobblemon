/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge.mixin;

import com.cobblemon.mod.common.loot.LootInjector;
import com.cobblemon.mod.forge.mixin.accessor.LootTableAccessor;
import com.cobblemon.mod.forge.mixin.accessor.LootTableBuilderAccessor;
import com.google.common.collect.ImmutableMap;
import kotlin.Unit;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(LootManager.class)
public class LootManagerMixin {

    @Shadow private Map<LootDataKey<?>, ?> keyToValue;

    // Credit to https://github.com/FabricMC/fabric/blob/1.19.4/fabric-loot-api-v2/src/main/java/net/fabricmc/fabric/mixin/loot/LootManagerMixin.java#L49-L86
    // This is very much a copy pasta as it does exactly what we needed Forge has an event but it gets fired as each individual table loads, we need to know all the loaded ones.
    @Inject(method = "reload", at = @At("RETURN"))
    private void cobblemon$injectLootTables(ResourceReloader.Synchronizer synchronizer, ResourceManager arg, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ImmutableMap.Builder<LootDataKey<?>, Object> newTables = ImmutableMap.builder();
        this.keyToValue.forEach((dataKey, entry) -> {
            if (dataKey == LootManager.EMPTY_LOOT_TABLE) {
                newTables.put(dataKey, entry);
                return;
            }
            if (!(entry instanceof LootTable lootTable)) {
                newTables.put(dataKey, entry);
                return;
            }
            @SuppressWarnings("DataFlowIssue") LootManager lootManager = (LootManager) (Object) this;
            LootTableAccessor lootTableAccessor = (LootTableAccessor) lootTable;
            LootTable.Builder builder = LootTable.builder().type(lootTable.getType());
            LootTableBuilderAccessor lootTableBuilderAccessor = (LootTableBuilderAccessor) builder;
            lootTableBuilderAccessor.getPools().addAll(lootTableAccessor.getPools());
            lootTableBuilderAccessor.getFunctions().addAll(lootTableAccessor.getFunctions());
            LootInjector.INSTANCE.attemptInjection(lootTable.getLootTableId(), lootManager, resultingBuilder -> {
                builder.pool(resultingBuilder);
                return Unit.INSTANCE;
            });
            newTables.put(dataKey, builder.build());
        });
        this.keyToValue = newTables.build();
    }

}
