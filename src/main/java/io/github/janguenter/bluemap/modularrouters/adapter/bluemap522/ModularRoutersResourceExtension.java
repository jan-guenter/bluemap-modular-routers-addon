/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.modularrouters.profile.ExactModularRoutersArtifactDetector;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;

import java.nio.file.Path;

/** Exact activation and narrow synthetic routing for persisted camouflage. */
final class ModularRoutersResourceExtension implements ResourcePackExtension {

    static final Key SYNTHETIC = Key.parse("bluemap_modularrouters:camouflage");

    private final ResourcePack resourcePack;
    private final ModularRoutersRuntime runtime;

    ModularRoutersResourceExtension(
            ResourcePack resourcePack,
            ModularRoutersRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
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
        if (state == null || state.getMultipart() != null) {
            return false;
        }
        Variants variants = state.getVariants();
        if (variants == null || variants.getDefaultVariant() == null) {
            return false;
        }
        VariantSet set = variants.getDefaultVariant();
        if (set.getVariants().length != 1) {
            return false;
        }
        return BlueMap522Adapter.isExpectedDispatch(set.getVariants()[0]);
    }
}
