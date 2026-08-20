/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.ArrayTileModel;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModularRoutersRendererTest {

    private static final Color INITIAL = color(0.1f, 0.2f, 0.3f, 0.4f);
    private static final Color TARGET = color(0.8f, 0.1f, 0.2f, 1.0f);
    private static final Color STOCK = color(0.2f, 0.8f, 0.1f, 1.0f);

    @BeforeEach
    void activateRuntime() {
        ModularRoutersRuntime.INSTANCE.activate();
    }

    @Test
    void validCubeAndPropertyRichStairReplaceTheHost() throws Exception {
        List<BlockState> states = List.of(
                state("minecraft:bricks", Map.of()),
                state("minecraft:oak_stairs", Map.of(
                        "facing", "east",
                        "half", "bottom",
                        "shape", "straight",
                        "waterlogged", "false"
                ))
        );
        for (BlockState camouflage : states) {
            AtomicReference<BlockState> emitted = new AtomicReference<>();
            AtomicInteger stockCalls = new AtomicInteger();
            ModularRoutersRenderer renderer = renderer(
                    (block, target) -> true,
                    (block, target, model, mapColor) -> {
                        emitted.set(target);
                        model.add(2);
                        mapColor.set(TARGET);
                    },
                    (block, model, mapColor) -> stockCalls.incrementAndGet()
            );
            ArrayTileModel model = new ArrayTileModel(8);
            Color mapColor = new Color().set(INITIAL);

            renderer.render(neighborhood(camouflage), null,
                    new TileModelView(model), mapColor);

            assertEquals(camouflage, emitted.get());
            assertEquals(2, model.size());
            assertEquals(0, stockCalls.get());
            assertColor(TARGET, mapColor);
        }
    }

    @Test
    void absentAndMalformedCamouflageRenderOnlyTheStockHost() throws Exception {
        for (BlockState camouflage : List.of(
                state("modularrouters:template_frame", Map.of()),
                state("chiselsandbits:chiseled_block", Map.of())
        )) {
            assertStockFallback(camouflage);
        }
        assertStockFallback(null);
    }

    @Test
    void targetRuntimeFailureResetsBeforeRenderingStock() throws Exception {
        ModularRoutersRenderer renderer = renderer(
                (block, target) -> true,
                (block, target, model, mapColor) -> {
                    model.add(2);
                    mapColor.set(TARGET);
                    throw new IllegalStateException("injected target failure");
                },
                (block, model, mapColor) -> {
                    assertEquals(0, model.getTileModel().size());
                    assertColor(INITIAL, mapColor);
                    model.add(4);
                    mapColor.set(STOCK);
                }
        );
        ArrayTileModel model = new ArrayTileModel(8);
        Color mapColor = new Color().set(INITIAL);

        renderer.render(neighborhood(state("minecraft:bricks", Map.of())), null,
                new TileModelView(model), mapColor);

        assertEquals(4, model.size());
        assertColor(STOCK, mapColor);
    }

    @Test
    void targetCapacityFailureResetsAndRethrowsWithoutStock() throws Exception {
        AtomicInteger stockCalls = new AtomicInteger();
        ModularRoutersRenderer renderer = renderer(
                (block, target) -> true,
                (block, target, model, mapColor) -> {
                    model.add(2);
                    mapColor.set(TARGET);
                    throw new MaxCapacityReachedException("injected target capacity");
                },
                (block, model, mapColor) -> stockCalls.incrementAndGet()
        );
        ArrayTileModel model = new ArrayTileModel(8);
        Color mapColor = new Color().set(INITIAL);

        assertThrows(MaxCapacityReachedException.class, () -> renderer.render(
                neighborhood(state("minecraft:bricks", Map.of())), null,
                new TileModelView(model), mapColor
        ));

        assertEquals(0, model.size());
        assertEquals(0, stockCalls.get());
        assertColor(INITIAL, mapColor);
    }

    @Test
    void capacityDuringExceptionFallbackAlsoResetsAndRethrows() throws Exception {
        ModularRoutersRenderer renderer = renderer(
                (block, target) -> true,
                (block, target, model, mapColor) -> {
                    model.add(2);
                    mapColor.set(TARGET);
                    throw new IllegalStateException("injected target failure");
                },
                (block, model, mapColor) -> {
                    assertEquals(0, model.getTileModel().size());
                    model.add(1);
                    mapColor.set(STOCK);
                    throw new MaxCapacityReachedException("injected stock capacity");
                }
        );
        ArrayTileModel model = new ArrayTileModel(8);
        Color mapColor = new Color().set(INITIAL);

        assertThrows(MaxCapacityReachedException.class, () -> renderer.render(
                neighborhood(state("minecraft:bricks", Map.of())), null,
                new TileModelView(model), mapColor
        ));

        assertEquals(0, model.size());
        assertColor(INITIAL, mapColor);
    }

    private static void assertStockFallback(BlockState camouflage) throws Exception {
        AtomicInteger targetCalls = new AtomicInteger();
        ModularRoutersRenderer renderer = renderer(
                (block, target) -> true,
                (block, target, model, mapColor) -> targetCalls.incrementAndGet(),
                (block, model, mapColor) -> {
                    model.add(3);
                    mapColor.set(STOCK);
                }
        );
        ArrayTileModel model = new ArrayTileModel(8);
        Color mapColor = new Color().set(INITIAL);

        renderer.render(neighborhood(camouflage), null,
                new TileModelView(model), mapColor);

        assertEquals(0, targetCalls.get());
        assertEquals(3, model.size());
        assertColor(STOCK, mapColor);
    }

    private static ModularRoutersRenderer renderer(
            ModularRoutersRenderer.TargetPolicy policy,
            ModularRoutersRenderer.TargetRenderer target,
            ModularRoutersRenderer.StockRenderer stock
    ) {
        return new ModularRoutersRenderer(
                ModularRoutersRuntime.INSTANCE, policy, target, stock
        );
    }

    private static BlockNeighborhood neighborhood(BlockState camouflage)
            throws Exception {
        ModularRoutersBlockEntityData data = new ModularRoutersBlockEntityData();
        setField(MCABlockEntity.class, data, "id",
                Key.parse(ModularRoutersProfile.TEMPLATE_FRAME));
        setField(ModularRoutersBlockEntityData.class, data,
                "camouflageName", camouflage);

        BlockNeighborhood block = mock(BlockNeighborhood.class);
        when(block.getBlockState()).thenReturn(state(
                ModularRoutersProfile.TEMPLATE_FRAME, Map.of()
        ));
        when(block.getBlockEntity()).thenReturn(data);
        return block;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value)
            throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static BlockState state(String id, Map<String, String> properties) {
        return new BlockState(Key.parse(id), properties);
    }

    private static Color color(float red, float green, float blue, float alpha) {
        return new Color().set(red, green, blue, alpha, false);
    }

    private static void assertColor(Color expected, Color actual) {
        assertEquals(expected.r, actual.r, 0.0001f);
        assertEquals(expected.g, actual.g, 0.0001f);
        assertEquals(expected.b, actual.b, 0.0001f);
        assertEquals(expected.a, actual.a, 0.0001f);
        assertEquals(expected.premultiplied, actual.premultiplied);
    }
}
