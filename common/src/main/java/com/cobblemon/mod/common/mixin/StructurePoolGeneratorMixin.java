/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.world.CobblemonStructureIDs;
import net.minecraft.registry.Registry;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(StructurePoolBasedGenerator.StructurePoolGenerator.class)
public abstract class StructurePoolGeneratorMixin {
    @Final
    @Mutable
    @Shadow Deque<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces;

    Map<String, Integer> generatedStructureGroupCounts;

    private static final Map<String, Integer> structureMaxes;
    private static final Map<Identifier, Set<String>> structureGroups = new HashMap<>();
    static {
        String pokecenter = "pokecenter";
        String berry = "berry_farm";

        structureGroups.put(CobblemonStructureIDs.PLAINS_POKECENTER, Set.of(pokecenter));
        structureGroups.put(CobblemonStructureIDs.DESERT_POKECENTER, Set.of(pokecenter));
        structureGroups.put(CobblemonStructureIDs.SAVANNA_POKECENTER, Set.of(pokecenter));
        structureGroups.put(CobblemonStructureIDs.SNOWY_POKECENTER, Set.of(pokecenter));
        structureGroups.put(CobblemonStructureIDs.TAIGA_POKECENTER, Set.of(pokecenter));

        structureGroups.put(CobblemonStructureIDs.SAVANNA_BERRY_SMALL, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.SAVANNA_BERRY_LARGE, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.PLAINS_BERRY_SMALL, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.PLAINS_BERRY_LARGE, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.SNOWY_BERRY_SMALL, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.SNOWY_BERRY_LARGE, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.DESERT_BERRY_SMALL, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.DESERT_BERRY_LARGE, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.TAIGA_BERRY_SMALL, Set.of(berry));
        structureGroups.put(CobblemonStructureIDs.TAIGA_BERRY_LARGE, Set.of(berry));

        //Mapped using location string as key
        Map<String, Integer> aMap = new HashMap<>();
        aMap.put(pokecenter, 1);
        aMap.put(berry, 2);

//        aMap.put(CobblemonStructureIDs.PLAINS_LONG_PATH, 3);
//        aMap.put(CobblemonStructureIDs.DESERT_LONG_PATH, 3);
//        aMap.put(CobblemonStructureIDs.SAVANNA_LONG_PATH, 3);
//        aMap.put(CobblemonStructureIDs.SNOWY_LONG_PATH, 3);
//        aMap.put(CobblemonStructureIDs.TAIGA_LONG_PATH, 3);
        structureMaxes = Collections.unmodifiableMap(aMap);
    }

    public Set<String> getGroups(Identifier structureIdentifier) {
        return structureGroups.getOrDefault(structureIdentifier, Set.of());
    }

    public boolean hasReachedMaximum(Identifier structureIdentifier) {
        Set<String> groups = getGroups(structureIdentifier);
        for (String group : groups) {
            int count = generatedStructureGroupCounts.getOrDefault(group, 0);
            if (count >= structureMaxes.getOrDefault(group, Integer.MAX_VALUE)) {
                return true;
            }
        }

        return false;
    }

    public void incrementStructureCount(Identifier structureIdentifier) {
        Set<String> groups = getGroups(structureIdentifier);
        for (String group : groups) {
            generatedStructureGroupCounts.put(group, generatedStructureGroupCounts.getOrDefault(group, 0) + 1);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onStructurePoolGeneratorCreation(Registry<StructurePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List<? super PoolStructurePiece> children, Random random, CallbackInfo ci) {
        generatedStructureGroupCounts = new HashMap<>();
    }

    @ModifyVariable(method = "generatePiece", at = @At("STORE"), ordinal = 1)
    private Iterator<StructurePoolElement> reduceStructurePoolElementIterator(Iterator<StructurePoolElement> iterator) {
        List<StructurePoolElement> reducedList = new ArrayList<>();

        while (iterator.hasNext()) {
            StructurePoolElement structure = iterator.next();
            Identifier structurePieceLocationKey = getCobblemonOnlyLocation(structure);
            if (structurePieceLocationKey == null) {
                reducedList.add(structure);
                continue;
            }

            if (!hasReachedMaximum(structurePieceLocationKey)) {
                reducedList.add(structure);
            }
        }

        return reducedList.iterator();
    }

    @ModifyVariable(method = "generatePiece", at = @At("STORE"), ordinal = 1)
    private PoolStructurePiece injected(PoolStructurePiece poolStructurePiece) {
        Identifier structureLocationKey = getCobblemonOnlyLocation(poolStructurePiece.getPoolElement());
        if (structureLocationKey != null) {
            incrementStructureCount(structureLocationKey);
        }
        return poolStructurePiece;
    }

    // This doesn't SEEM necessary? It appears to work without.
    @Inject(method = "generatePiece", at = @At("HEAD"))
    private void beforeGeneratePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, boolean modifyBoundingBox, HeightLimitView world, NoiseConfig noiseConfig, CallbackInfo ci) {
//        Identifier structureLocationKey = getCobblemonOnlyLocation(piece.getPoolElement());
//
//        if (structureLocationKey != null) {
//            Integer currentlyGenerated = generatedStructureCounts.get(structureLocationKey);
//            if (currentlyGenerated == null) currentlyGenerated = 0;
//            generatedStructureCounts.put(structureLocationKey, currentlyGenerated + 1);
//        }
//
//        List<StructurePoolBasedGenerator.ShapedPoolStructurePiece> reducedStructurePiecesList = structurePieces.stream().toList();
//
//        for (Identifier maxStructureLocationKey : structureMaxes.keySet()) {
//            Integer maxAllowed = structureMaxes.get(maxStructureLocationKey);
//
//            Integer currentlyGenerated = generatedStructureCounts.get(maxStructureLocationKey);
//            if (currentlyGenerated == null) currentlyGenerated = 0;
//            if (currentlyGenerated < maxAllowed) {
//                continue;
//            }
//
//            //Already have max so need to remove
//            reducedStructurePiecesList = reducedStructurePiecesList.stream()
//                    .filter(shapedStructurePiece -> {
//                        Identifier locationKey = getCobblemonOnlyLocation(shapedStructurePiece.piece.getPoolElement());
//                        if (locationKey == null) {
//                            return true;
//                        }
//
//                        return !locationKey.equals(maxStructureLocationKey);
//                    })
//                    .collect(Collectors.toList());
//        }
//
//        structurePieces = new ArrayDeque<>(reducedStructurePiecesList);
    }

    private static Identifier getCobblemonOnlyLocation(StructurePoolElement structurePoolElement) {
        Identifier location = getLocationIfAvailable(structurePoolElement);
        if (location == null) return null;

        if (!location.getNamespace().equals("cobblemon")) return null;

        return location;
    }

    private static Identifier getLocationIfAvailable(StructurePoolElement structurePoolElement) {
        if (structurePoolElement instanceof LegacySinglePoolElement legacySinglePoolElement) {
            if (legacySinglePoolElement.location.left().isEmpty()) return null;

            return legacySinglePoolElement.location.left().get();
        } else if (structurePoolElement instanceof SinglePoolElement singlePoolElement) {
            if (singlePoolElement.location.left().isEmpty()) return null;

            return singlePoolElement.location.left().get();
        } else {
            return null;
        }
    }

//    private List<StructurePoolElement> removeInstanceOfLocationKeyFrom(List<StructurePoolElement> structureList, Integer allowedNumberOfInstances, Identifier locationKey) {
//        List<StructurePoolElement> reducedList = new ArrayList<>();
//        int instancesFound = 0;
//
//        for (StructurePoolElement structurePoolElement: structureList) {
//            Identifier structureKey = getCobblemonOnlyLocation(structurePoolElement);
//            if (structureKey == null || !structureKey.equals(locationKey)) {
//                reducedList.add(structurePoolElement);
//                continue;
//            }
//
//            if (instancesFound >= allowedNumberOfInstances) {
//                continue;
//            }
//
//            reducedList.add(structurePoolElement);
//            instancesFound++;
//        }
//
//        return  reducedList;
//    }
}
