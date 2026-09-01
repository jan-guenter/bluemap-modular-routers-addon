/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.adapter.ResourcesGson;
import de.bluecolored.bluemap.core.resources.pack.ResourcePool;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModularRoutersResourceExtensionTest {

    @Test
    void glassentialBitResetsWithoutDeactivatingTheCoreRoute(
            @TempDir Path temporary
    ) throws IOException {
        assertTrue(BlueMap523Adapter.install());
        ResourcePack resourcePack = mock(ResourcePack.class);
        ResourcePool<
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
                > states = new ResourcePool<>();
        states.put(ModularRoutersResourceExtension.SYNTHETIC, dispatch());
        var ordinary = ordinary();
        states.put(Key.parse("minecraft:oak_log"), ordinary);
        when(resourcePack.getBlockStates()).thenReturn(states);
        ModularRoutersResourceExtension extension = new ModularRoutersResourceExtension(
                resourcePack, ModularRoutersRuntime.INSTANCE
        );
        Path modularRouters = configured("modularRoutersJar");
        Path glassentialAddon = configured("glassentialAddonJar");

        extension.loadResources(List.of(modularRouters, glassentialAddon));
        assertTrue(ModularRoutersRuntime.INSTANCE.active());
        assertTrue(extension.glassentialInteropArtifactPresent());

        Variant oak = ordinary.getVariants().getVariants()[0].getVariants()[0];
        assertTrue(extension.originallyRenderedBy(oak, BlockRendererType.DEFAULT));
        BlockRendererType wrapper = mock(BlockRendererType.class);
        oak.setRenderer(wrapper);
        assertTrue(extension.originallyRenderedBy(oak, BlockRendererType.DEFAULT));
        assertFalse(extension.originallyRenderedBy(oak, wrapper));

        extension.loadResources(List.of(modularRouters));
        assertTrue(ModularRoutersRuntime.INSTANCE.active());
        assertFalse(extension.glassentialInteropArtifactPresent());

        Path mismatched = temporary.resolve("changed-glassential-addon.jar");
        Files.write(mismatched, new byte[162_440]);
        extension.loadResources(List.of(modularRouters, mismatched));
        assertTrue(ModularRoutersRuntime.INSTANCE.active());
        assertFalse(extension.glassentialInteropArtifactPresent());

        extension.loadResources(List.of(modularRouters, glassentialAddon));
        assertTrue(ModularRoutersRuntime.INSTANCE.active());
        assertTrue(extension.glassentialInteropArtifactPresent());
    }

    private static de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
            dispatch() {
        return ResourcesGson.INSTANCE.fromJson(
                """
                {"variants":{"":{"renderer":"bluemap_modularrouters:camouflage",
                  "model":"bluemap:block/missing"}}}
                """,
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState.class
        );
    }

    private static de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
            ordinary() {
        return ResourcesGson.INSTANCE.fromJson(
                """
                {"variants":{"axis=x":{"model":"minecraft:block/oak_log_horizontal"}}}
                """,
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState.class
        );
    }

    private static Path configured(String property) {
        String value = System.getProperty(property);
        assertTrue(value != null && !value.isBlank(),
                "test JVM needs -P" + property + "=<exact JAR>");
        return Path.of(value);
    }
}
