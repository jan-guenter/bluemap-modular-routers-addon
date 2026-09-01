/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.modularrouters.profile.ExactGlassentialAddonArtifactDetector;
import io.github.janguenter.bluemap.modularrouters.profile.ExactModularRoutersArtifactDetector;
import io.github.janguenter.bluemap.modularrouters.profile.GlassentialInteropProfile;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;

import java.nio.file.Path;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/** Exact activation and narrow synthetic routing for persisted camouflage. */
final class ModularRoutersResourceExtension implements ResourcePackExtension {

    static final Key SYNTHETIC = Key.parse("bluemap_modularrouters:camouflage");

    private final ResourcePack resourcePack;
    private final ModularRoutersRuntime runtime;
    private volatile Map<Variant, BlockRendererType> originalRenderers = Map.of();
    private boolean rendererSnapshotCaptured;
    private volatile boolean glassentialInteropArtifactPresent;

    ModularRoutersResourceExtension(
            ResourcePack resourcePack,
            ModularRoutersRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        captureOriginalRenderers();
        glassentialInteropArtifactPresent =
                ExactGlassentialAddonArtifactDetector.matches(roots);
        if (Boolean.getBoolean("bluemap.modularrouters.disabled")) {
            runtime.inactive("operator-disabled");
        } else if (!ExactModularRoutersArtifactDetector.matches(roots)) {
            runtime.inactive("exact-modular-routers-artifact-not-found");
        } else if (!validDispatch(resourcePack.getBlockStates().get(SYNTHETIC))) {
            runtime.inactive("synthetic-dispatch-invalid");
        } else {
            runtime.activate();
        }
    }

    boolean glassentialInteropArtifactPresent() {
        return glassentialInteropArtifactPresent;
    }

    boolean originallyRenderedBy(Variant variant, BlockRendererType renderer) {
        return originalRenderers.get(variant) == renderer;
    }

    private void captureOriginalRenderers() {
        if (rendererSnapshotCaptured) {
            return;
        }
        BlockRendererType glassential = BlockRendererType.REGISTRY.get(
                GlassentialInteropProfile.SYNTHETIC_DISPATCH
        );
        IdentityHashMap<Variant, BlockRendererType> captured = new IdentityHashMap<>();
        resourcePack.getBlockStates().values().forEach(state -> state.forEach(variant -> {
            BlockRendererType renderer = variant.getRenderer();
            if (renderer == BlockRendererType.DEFAULT || renderer == glassential) {
                captured.put(variant, renderer);
            }
        }));
        originalRenderers = Collections.unmodifiableMap(captured);
        rendererSnapshotCaptured = true;
    }

    @Override
    public void bake() {
        if (runtime.active()) {
            System.out.println("BlueMap Modular Routers add-on active: routed "
                    + ModularRoutersProfile.HOST_IDS.size()
                    + " persisted-camouflage hosts.");
        }
    }

    @Override
    public Key getBlockStateKey(Key key) {
        if (runtime.active() && ModularRoutersProfile.HOST_IDS.contains(key.getFormatted())) {
            return SYNTHETIC;
        }
        return key;
    }

    @Override
    public void getBlockProperties(BlockState state, BlockProperties.Builder builder) {
        if (runtime.active()) {
            applyHostProperties(state, builder);
        }
    }

    static boolean applyHostProperties(BlockState state, BlockProperties.Builder builder) {
        if (!ModularRoutersProfile.HOST_IDS.contains(state.getId().getFormatted())) {
            return false;
        }
        builder.culling(false).occluding(false).cullingIdentical(false);
        return true;
    }

    private static boolean validDispatch(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state
    ) {
        return BlueMap523Adapter.isExpectedDispatch(state);
    }
}
