/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.map.hires.block.BlockStateModelRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Replaces an exact host with a conservative persisted camouflage model. */
final class ModularRoutersRenderer implements BlockRenderer {

    private static final int MAX_VARIANTS = 64;
    private static final Set<String> VANILLA_WATERLOGGED_STAIRS_1_21_1 = Set.of(
            "minecraft:acacia_stairs",
            "minecraft:andesite_stairs",
            "minecraft:bamboo_mosaic_stairs",
            "minecraft:bamboo_stairs",
            "minecraft:birch_stairs",
            "minecraft:blackstone_stairs",
            "minecraft:brick_stairs",
            "minecraft:cherry_stairs",
            "minecraft:cobbled_deepslate_stairs",
            "minecraft:cobblestone_stairs",
            "minecraft:crimson_stairs",
            "minecraft:cut_copper_stairs",
            "minecraft:dark_oak_stairs",
            "minecraft:dark_prismarine_stairs",
            "minecraft:deepslate_brick_stairs",
            "minecraft:deepslate_tile_stairs",
            "minecraft:diorite_stairs",
            "minecraft:end_stone_brick_stairs",
            "minecraft:exposed_cut_copper_stairs",
            "minecraft:granite_stairs",
            "minecraft:jungle_stairs",
            "minecraft:mangrove_stairs",
            "minecraft:mossy_cobblestone_stairs",
            "minecraft:mossy_stone_brick_stairs",
            "minecraft:mud_brick_stairs",
            "minecraft:nether_brick_stairs",
            "minecraft:oak_stairs",
            "minecraft:oxidized_cut_copper_stairs",
            "minecraft:polished_andesite_stairs",
            "minecraft:polished_blackstone_brick_stairs",
            "minecraft:polished_blackstone_stairs",
            "minecraft:polished_deepslate_stairs",
            "minecraft:polished_diorite_stairs",
            "minecraft:polished_granite_stairs",
            "minecraft:polished_tuff_stairs",
            "minecraft:prismarine_brick_stairs",
            "minecraft:prismarine_stairs",
            "minecraft:purpur_stairs",
            "minecraft:quartz_stairs",
            "minecraft:red_nether_brick_stairs",
            "minecraft:red_sandstone_stairs",
            "minecraft:sandstone_stairs",
            "minecraft:smooth_quartz_stairs",
            "minecraft:smooth_red_sandstone_stairs",
            "minecraft:smooth_sandstone_stairs",
            "minecraft:spruce_stairs",
            "minecraft:stone_brick_stairs",
            "minecraft:stone_stairs",
            "minecraft:tuff_brick_stairs",
            "minecraft:tuff_stairs",
            "minecraft:warped_stairs",
            "minecraft:waxed_cut_copper_stairs",
            "minecraft:waxed_exposed_cut_copper_stairs",
            "minecraft:waxed_oxidized_cut_copper_stairs",
            "minecraft:waxed_weathered_cut_copper_stairs",
            "minecraft:weathered_cut_copper_stairs"
    );

    private final ResourcePack resourcePack;
    private final ModularRoutersRuntime runtime;
    private final TargetPolicy targetPolicy;
    private final TargetRenderer targetRenderer;
    private final StockRenderer stockRenderer;
    private final CamouflageSnapshotDecoder decoder = new CamouflageSnapshotDecoder();

    ModularRoutersRenderer(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            ModularRoutersRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
        ResourceModelRenderer stock = new ResourceModelRenderer(
                resourcePack, textures, settings
        );
        BlockStateModelRenderer state = new BlockStateModelRenderer(
                resourcePack, textures, settings
        );
        ModularRoutersResourceExtension extension = BlueMap523Adapter.extension(
                resourcePack
        );
        GlassentialTarget glassential = new GlassentialDefaultCamouflageBridge(
                resourcePack,
                textures,
                settings,
                extension
        );
        this.targetPolicy = guardedPolicy(
                glassential,
                (block, target) -> ordinaryResourceState(extension, block, target)
        );
        this.targetRenderer = guardedRenderer(glassential, state::render);
        this.stockRenderer = (block, target, color) -> renderStockResources(
                stock, block, target, color
        );
    }

    ModularRoutersRenderer(
            ModularRoutersRuntime runtime,
            TargetPolicy targetPolicy,
            TargetRenderer targetRenderer,
            StockRenderer stockRenderer
    ) {
        this.resourcePack = null;
        this.runtime = runtime;
        this.targetPolicy = targetPolicy;
        this.targetRenderer = targetRenderer;
        this.stockRenderer = stockRenderer;
    }

    ModularRoutersRenderer(
            ModularRoutersRuntime runtime,
            TargetPolicy ordinaryPolicy,
            TargetRenderer ordinaryRenderer,
            GlassentialTarget glassential,
            StockRenderer stockRenderer
    ) {
        this.resourcePack = null;
        this.runtime = runtime;
        this.targetPolicy = guardedPolicy(glassential, ordinaryPolicy);
        this.targetRenderer = guardedRenderer(glassential, ordinaryRenderer);
        this.stockRenderer = stockRenderer;
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant ignored,
            TileModelView target,
            Color mapColor
    ) {
        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        if (!runtime.active()) {
            renderStock(block, target, mapColor);
            return;
        }
        try {
            ModularRoutersBlockEntityData data = block.getBlockEntity()
                    instanceof ModularRoutersBlockEntityData found ? found : null;
            String hostId = block.getBlockState().getId().getFormatted();
            String blockEntityId = data == null || data.getId() == null
                    ? null : data.getId().getFormatted();
            Optional<BlockState> camouflage =
                    ModularRoutersProfile.matches(hostId, blockEntityId)
                            ? decoder.decode(hostId, data) : Optional.empty();
            if (camouflage.isEmpty()
                    || !targetPolicy.test(block, camouflage.orElseThrow())) {
                resetAndRenderStock(block, target, start, mapColor, initialMapColor);
                return;
            }
            targetRenderer.render(
                    block, camouflage.orElseThrow(), target, mapColor
            );
        } catch (MaxCapacityReachedException exception) {
            resetPartial(target, start, mapColor, initialMapColor);
            throw exception;
        } catch (RuntimeException | LinkageError exception) {
            runtime.report("render-failed-" + exception.getClass().getSimpleName());
            resetAndRenderStock(block, target, start, mapColor, initialMapColor);
        }
    }

    private boolean ordinaryResourceState(
            ModularRoutersResourceExtension extension,
            BlockNeighborhood block,
            BlockState state
    ) {
        if (state.isWaterlogged()) {
            return false;
        }
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState raw =
                resourcePack.getBlockStates().get(state.getId());
        if (extension == null || raw == null || resourcePack.getBlockState(state) != raw
                || !propertiesMatchResource(raw, state)) {
            return false;
        }
        List<Variant> variants = new ArrayList<>();
        raw.forEach(state, block.getX(), block.getY(), block.getZ(), variants::add);
        if (variants.isEmpty() || variants.size() > MAX_VARIANTS) {
            return false;
        }
        return variants.stream().allMatch(variant ->
                extension.originallyRenderedBy(variant, BlockRendererType.DEFAULT)
                        && !ResourcePack.MISSING_BLOCK_MODEL.equals(variant.getModel())
                        && ordinaryModel(resourcePack.getModels().get(variant.getModel())));
    }

    private static TargetPolicy guardedPolicy(
            GlassentialTarget glassential,
            TargetPolicy ordinary
    ) {
        return (block, state) -> glassential.owns(state)
                ? glassential.accepts(state) : ordinary.test(block, state);
    }

    private static TargetRenderer guardedRenderer(
            GlassentialTarget glassential,
            TargetRenderer ordinary
    ) {
        return (block, state, target, color) -> {
            if (glassential.owns(state)) {
                glassential.render(block, state, target, color);
            } else {
                ordinary.render(block, state, target, color);
            }
        };
    }

    static boolean propertiesMatchResource(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource,
            BlockState state
    ) {
        for (Map.Entry<String, String> property : state.getProperties().entrySet()) {
            if ("waterlogged".equals(property.getKey())
                    && "false".equals(property.getValue())
                    && VANILLA_WATERLOGGED_STAIRS_1_21_1.contains(
                            state.getId().getFormatted())) {
                continue;
            }
            Map<String, String> reducedProperties = new HashMap<>(state.getProperties());
            reducedProperties.remove(property.getKey());
            BlockState reduced = new BlockState(state.getId(), Map.copyOf(reducedProperties));
            if (!selectionChanges(resource, state, reduced)) {
                return false;
            }
        }
        return true;
    }

    private static boolean selectionChanges(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState resource,
            BlockState full,
            BlockState reduced
    ) {
        if (resource.getVariants() != null) {
            for (VariantSet set : resource.getVariants().getVariants()) {
                if (set.getCondition().matches(full) != set.getCondition().matches(reduced)) {
                    return true;
                }
            }
        }
        if (resource.getMultipart() != null) {
            for (VariantSet set : resource.getMultipart().getParts()) {
                if (set.getCondition().matches(full) != set.getCondition().matches(reduced)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean ordinaryModel(Model model) {
        if (model == null || model.getElements() == null || model.getElements().length == 0) {
            return false;
        }
        for (Element element : model.getElements()) {
            if (element.getFaces().values().stream().anyMatch(face -> face.getTintindex() >= 0)) {
                return false;
            }
        }
        return true;
    }

    private void resetAndRenderStock(
            BlockNeighborhood block,
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        resetPartial(target, start, mapColor, initialMapColor);
        try {
            renderStock(block, target, mapColor);
        } catch (MaxCapacityReachedException exception) {
            resetPartial(target, start, mapColor, initialMapColor);
            throw exception;
        }
    }

    private void resetPartial(
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        target.getTileModel().reset(start);
        target.initialize(start);
        mapColor.set(initialMapColor);
    }

    private void renderStock(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        stockRenderer.render(block, target, mapColor);
    }

    private void renderStockResources(
            ResourceModelRenderer stock,
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (state == null) {
            return;
        }
        state.forEach(
                block.getBlockState(),
                block.getX(),
                block.getY(),
                block.getZ(),
                variant -> stock.render(block, variant, target, mapColor)
        );
    }

    @FunctionalInterface
    interface TargetPolicy {
        boolean test(BlockNeighborhood block, BlockState state);
    }

    @FunctionalInterface
    interface TargetRenderer {
        void render(
                BlockNeighborhood block,
                BlockState state,
                TileModelView target,
                Color mapColor
        );
    }

    @FunctionalInterface
    interface StockRenderer {
        void render(BlockNeighborhood block, TileModelView target, Color mapColor);
    }

    interface GlassentialTarget {
        boolean owns(BlockState state);

        boolean accepts(BlockState state);

        void render(
                BlockNeighborhood block,
                BlockState state,
                TileModelView target,
                Color mapColor
        );
    }
}
