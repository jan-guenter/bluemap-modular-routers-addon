/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.SyntheticDispatch;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;

import java.util.List;

/** Exact BlueMap 5.23 feature-backport internal ABI registration boundary. */
public final class BlueMap523Adapter {

    private static final ModularRoutersRuntime RUNTIME = ModularRoutersRuntime.INSTANCE;
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_modularrouters:camouflage"),
            (pack, gallery, settings) -> new ModularRoutersRenderer(
                    pack, gallery, settings, RUNTIME
            )
    );
    private static final ResourcePack.Extension<ModularRoutersResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    Key.parse("bluemap_modularrouters:camouflage_extension"),
                    pack -> new ModularRoutersResourceExtension(pack, RUNTIME)
            );
    private static final List<BlockEntityType> BLOCK_ENTITY_TYPES =
            ModularRoutersProfile.BLOCK_ENTITY_IDS.stream()
                    .sorted()
                    .map(id -> (BlockEntityType) new BlockEntityType.Impl(
                            Key.parse(id), ModularRoutersBlockEntityData.class
                    ))
                    .toList();

    private BlueMap523Adapter() {
    }

    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)
                || BLOCK_ENTITY_TYPES.stream()
                        .anyMatch(type -> !RegistryGuard.canRegister(
                                BlockEntityType.REGISTRY, type))) {
            RUNTIME.inactive("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.inactive("registry-collision");
            return false;
        }
        for (BlockEntityType type : BLOCK_ENTITY_TYPES) {
            if (!RegistryGuard.register(BlockEntityType.REGISTRY, type)) {
                RUNTIME.inactive("block-entity-registry-collision");
                return false;
            }
        }
        return true;
    }

    static boolean isExpectedDispatch(BlockState state) {
        return SyntheticDispatch.matches(state, RENDERER);
    }

    static ModularRoutersResourceExtension extension(ResourcePack resourcePack) {
        return resourcePack.getExtension(EXTENSION);
    }

}
