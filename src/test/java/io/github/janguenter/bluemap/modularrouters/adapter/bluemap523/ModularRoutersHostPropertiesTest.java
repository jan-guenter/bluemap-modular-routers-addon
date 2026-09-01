/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModularRoutersHostPropertiesTest {

    @Test
    void bothExactHostsDisableAllThreeCullingSignals() {
        assertHostProperties(ModularRoutersProfile.MODULAR_ROUTER);
        assertHostProperties(ModularRoutersProfile.TEMPLATE_FRAME);
    }

    @Test
    void nonHostPropertiesRemainUntouched() {
        BlockProperties.Builder builder = enabledCullingBuilder();
        boolean applied = ModularRoutersResourceExtension.applyHostProperties(
                state("minecraft:stone"), builder
        );

        assertFalse(applied);
        BlockProperties properties = builder.build();
        assertTrue(properties.isCulling());
        assertTrue(properties.isOccluding());
        assertTrue(properties.getCullingIdentical());
    }

    private static void assertHostProperties(String id) {
        BlockProperties.Builder builder = enabledCullingBuilder();
        assertTrue(ModularRoutersResourceExtension.applyHostProperties(
                state(id), builder
        ));
        BlockProperties properties = builder.build();
        assertFalse(properties.isCulling());
        assertFalse(properties.isOccluding());
        assertFalse(properties.getCullingIdentical());
    }

    private static BlockProperties.Builder enabledCullingBuilder() {
        return BlockProperties.builder()
                .culling(true)
                .occluding(true)
                .cullingIdentical(true);
    }

    private static BlockState state(String id) {
        return new BlockState(Key.parse(id), Map.of());
    }
}
