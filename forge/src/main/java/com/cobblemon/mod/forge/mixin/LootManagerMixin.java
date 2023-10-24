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
import com.google.gson.JsonElement;
import kotlin.Unit;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

@Mixin(LootManager.class)
public class LootManagerMixin {

    @Shadow private Map<LootDataKey<?>, ?> keyToValue;

    private static final String LOAD_CONDITIONS = "cobblemon:forge_load_conditions";

    // Forge was going to add support to LootTable for their ICondition in 1.20, then it was 1.20.1, now it looks like it's 1.20.2
    // Who knows when it will actually make it in, keep an eye on the necessity of this
    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    private void cobblemon$supportICondition(Map<Identifier, JsonElement> map, BiConsumer<Identifier, JsonElement> consumer) {
        map.forEach((identifier, jsonElement) -> {
            // If the element isn't present the result is true as well, also, it's safe to cast as JsonObject
            if (CraftingHelper.processConditions(jsonElement.getAsJsonObject(), LOAD_CONDITIONS, ICondition.IContext.EMPTY)) {
                consumer.accept(identifier, jsonElement);
            }
        });
    }

    // Credit to https://github.com/FabricMC/fabric/blob/1.19.4/fabric-loot-api-v2/src/main/java/net/fabricmc/fabric/mixin/loot/LootManagerMixin.java#L49-L86
    // This is very much a copy pasta as it does exactly what we needed Forge has an event but it gets fired as each individual table loads, we need to know all the loaded ones.
    @Inject(method = "reload", at = @At("RETURN"), cancellable = true)
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
            lootTableBuilderAccessor.getFunctions().addAll(List.of(lootTableAccessor.getFunctions()));
            LootInjector.INSTANCE.attemptInjection(lootTable.getLootTableId(), lootManager, resultingBuilder -> {
                builder.pool(resultingBuilder);
                return Unit.INSTANCE;
            });
            newTables.put(dataKey, builder.build());
        });
        this.keyToValue = newTables.build();
    }

}
