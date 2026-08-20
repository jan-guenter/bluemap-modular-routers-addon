/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;

/** Resource-pack extension factory registered before resource loading. */
final class ModularRoutersResourceExtensionType
        implements ResourcePack.Extension<ModularRoutersResourceExtension> {

    private static final Key KEY =
            Key.parse("bluemap_modularrouters:camouflage_extension");
    private final ModularRoutersRuntime runtime;

    ModularRoutersResourceExtensionType(ModularRoutersRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public Key getKey() {
        return KEY;
    }

    @Override
    public ModularRoutersResourceExtension create(ResourcePack pack) {
        return new ModularRoutersResourceExtension(pack, runtime);
    }
}
