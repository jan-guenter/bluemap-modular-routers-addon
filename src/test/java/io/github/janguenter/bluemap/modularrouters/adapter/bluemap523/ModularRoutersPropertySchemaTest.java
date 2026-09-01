/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.adapter.ResourcesGson;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockState;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModularRoutersPropertySchemaTest {

    @Test
    void rejectsUnknownOrInvalidPropertiesWithoutRejectingExactSelectors() {
        var resource = ResourcesGson.INSTANCE.fromJson(
                """
                {"variants":{
                  "axis=x":{"model":"minecraft:block/oak_log_horizontal"},
                  "axis=y":{"model":"minecraft:block/oak_log"},
                  "axis=z":{"model":"minecraft:block/oak_log_horizontal"}
                }}
                """,
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState.class
        );

        assertTrue(ModularRoutersRenderer.propertiesMatchResource(
                resource, state(Map.of("axis", "x"))));
        assertFalse(ModularRoutersRenderer.propertiesMatchResource(
                resource, state(Map.of("axis", "x", "bogus", "x"))));
        assertFalse(ModularRoutersRenderer.propertiesMatchResource(
                resource, state(Map.of("axis", "invalid"))));
    }

    @Test
    void permitsOnlyKnownModelInertFalseWaterloggedStairs() {
        var resource = ResourcesGson.INSTANCE.fromJson(
                """
                {"variants":{
                  "facing=east,half=bottom,shape=straight":
                    {"model":"minecraft:block/oak_stairs"}
                }}
                """,
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState.class
        );
        assertTrue(ModularRoutersRenderer.propertiesMatchResource(
                resource,
                state("minecraft:oak_stairs", Map.of(
                        "facing", "east", "half", "bottom", "shape", "straight",
                        "waterlogged", "false"
                ))
        ));
        assertFalse(ModularRoutersRenderer.propertiesMatchResource(
                resource,
                state("minecraft:bricks", Map.of(
                        "facing", "east", "half", "bottom", "shape", "straight",
                        "waterlogged", "false"
                ))
        ));
    }

    private static BlockState state(Map<String, String> properties) {
        return state("minecraft:test", properties);
    }

    private static BlockState state(String id, Map<String, String> properties) {
        return new BlockState(Key.parse(id), properties);
    }
}
