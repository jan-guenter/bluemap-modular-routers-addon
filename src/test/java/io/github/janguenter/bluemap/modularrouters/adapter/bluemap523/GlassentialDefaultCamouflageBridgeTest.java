/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import com.flowpowered.math.vector.Vector3f;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.adapter.ResourcesGson;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.TextureVariable;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.DimensionType;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;
import io.github.janguenter.bluemap.modularrouters.profile.GlassentialInteropProfile;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlassentialDefaultCamouflageBridgeTest {

    @Test
    void exactRosterGuardsEveryGlassentialRoute() {
        assertEquals(49, GlassentialInteropProfile.ROUTED_BLOCK_IDS.size());
        assertTrue(GlassentialInteropProfile.ROUTED_BLOCK_IDS.contains(
                GlassentialInteropProfile.GLASS
        ));
        assertTrue(GlassentialInteropProfile.ROUTED_BLOCK_IDS.contains(
                "glassential:colorable_glass"
        ));
        assertTrue(GlassentialInteropProfile.ROUTED_BLOCK_IDS.contains(
                "minecraft:yellow_stained_glass"
        ));
        assertFalse(GlassentialInteropProfile.ROUTED_BLOCK_IDS.contains(
                "minecraft:bricks"
        ));
    }

    @Test
    void classifiesOnlyPropertylessGlassAsTheSupportedRoute() {
        BlockState glass = state("minecraft:glass", Map.of());
        assertTrue(GlassentialDefaultCamouflageBridge.ownsRoute(glass));
        assertTrue(GlassentialDefaultCamouflageBridge.isPropertylessGlass(glass));

        BlockState propertyRichGlass = state(
                "minecraft:glass", Map.of("bogus", "value")
        );
        assertTrue(GlassentialDefaultCamouflageBridge.ownsRoute(propertyRichGlass));
        assertFalse(GlassentialDefaultCamouflageBridge.isPropertylessGlass(
                propertyRichGlass
        ));

        BlockState stained = state("minecraft:red_stained_glass", Map.of());
        assertTrue(GlassentialDefaultCamouflageBridge.ownsRoute(stained));
        assertFalse(GlassentialDefaultCamouflageBridge.isPropertylessGlass(stained));

        BlockState ordinary = state("minecraft:bricks", Map.of());
        assertFalse(GlassentialDefaultCamouflageBridge.ownsRoute(ordinary));
        assertFalse(GlassentialDefaultCamouflageBridge.isPropertylessGlass(ordinary));
    }

    @Test
    void requiresExactSingleSyntheticDispatchVariant() {
        BlockRendererType expected = mock(BlockRendererType.class);
        org.mockito.Mockito.when(expected.getKey()).thenReturn(
                GlassentialInteropProfile.SYNTHETIC_DISPATCH
        );

        var valid = dispatch("bluemap:block/missing", false, 1D);
        onlyVariant(valid).setRenderer(expected);
        assertTrue(GlassentialDefaultCamouflageBridge.validDispatch(
                valid, expected
        ));

        var wrongModel = dispatch("minecraft:block/glass", false, 1D);
        onlyVariant(wrongModel).setRenderer(expected);
        assertFalse(GlassentialDefaultCamouflageBridge.validDispatch(
                wrongModel, expected
        ));

        var transformed = dispatch("bluemap:block/missing", true, 1D);
        onlyVariant(transformed).setRenderer(expected);
        assertFalse(GlassentialDefaultCamouflageBridge.validDispatch(
                transformed, expected
        ));

        var weighted = dispatch("bluemap:block/missing", false, 2D);
        onlyVariant(weighted).setRenderer(expected);
        assertFalse(GlassentialDefaultCamouflageBridge.validDispatch(
                weighted, expected
        ));

        BlockRendererType impostor = mock(BlockRendererType.class);
        org.mockito.Mockito.when(impostor.getKey()).thenReturn(
                GlassentialInteropProfile.SYNTHETIC_DISPATCH
        );
        assertFalse(GlassentialDefaultCamouflageBridge.validDispatch(
                valid, impostor
        ));
    }

    @Test
    void requiresTheExactGeneratedSixteenPixelTile() throws IOException {
        assertTrue(GlassentialDefaultCamouflageBridge.validTile(Texture.from(
                GlassentialInteropProfile.DEFAULT_GLASS_TILE,
                new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
        )));
        assertFalse(GlassentialDefaultCamouflageBridge.validTile(Texture.from(
                GlassentialInteropProfile.DEFAULT_GLASS_TILE,
                new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
        )));
        assertFalse(GlassentialDefaultCamouflageBridge.validTile(Texture.from(
                Key.parse("minecraft:block/glass"),
                new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
        )));
    }

    @Test
    void requiresTheExactProjectOwnedCubeModelSchema() {
        assertTrue(GlassentialDefaultCamouflageBridge.validBridgeModel(
                cubeModel(GlassentialInteropProfile.DEFAULT_GLASS_TILE, true)
        ));
        assertFalse(GlassentialDefaultCamouflageBridge.validBridgeModel(
                cubeModel(Key.parse("minecraft:block/glass"), true)
        ));
        assertFalse(GlassentialDefaultCamouflageBridge.validBridgeModel(
                cubeModel(GlassentialInteropProfile.DEFAULT_GLASS_TILE, false)
        ));
        assertFalse(GlassentialDefaultCamouflageBridge.validBridgeModel(
                new Model(new Element[0])
        ));
    }

    @Test
    void delegatesThroughAPropertylessGlassNeighborhood() {
        AtomicReference<BlockNeighborhood> rendered = new AtomicReference<>();
        BlockRenderer renderer = (block, variant, target, color) -> rendered.set(block);
        GlassentialDefaultCamouflageBridge bridge =
                new GlassentialDefaultCamouflageBridge(renderer, mock(Variant.class));
        ResourcePack resourcePack = mock(ResourcePack.class);
        RenderSettings settings = mock(RenderSettings.class);
        DimensionType dimension = mock(DimensionType.class);
        BlockNeighborhood source = mock(BlockNeighborhood.class);
        BlockProperties properties = mock(BlockProperties.class);
        ExtendedBlock neighbor = mock(ExtendedBlock.class);
        BlockState glass = state("minecraft:glass", Map.of());
        when(source.getResourcePack()).thenReturn(resourcePack);
        when(source.getRenderSettings()).thenReturn(settings);
        when(source.getDimensionType()).thenReturn(dimension);
        when(source.getX()).thenReturn(17);
        when(source.getY()).thenReturn(23);
        when(source.getZ()).thenReturn(41);
        when(source.copy()).thenReturn(neighbor);
        when(source.getNeighborBlock(1, 0, 0)).thenReturn(neighbor);
        when(resourcePack.getBlockProperties(glass)).thenReturn(properties);

        bridge.render(source, glass, mock(TileModelView.class), new Color());

        BlockNeighborhood view = rendered.get();
        assertSame(glass, view.getBlockState());
        assertNull(view.getBlockEntity());
        assertSame(properties, view.getProperties());
        assertSame(view, view.getNeighborBlock(0, 0, 0));
        assertSame(neighbor, view.getNeighborBlock(1, 0, 0));
    }

    private static de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
            dispatch(String model, boolean transformed, double weight) {
        return ResourcesGson.INSTANCE.fromJson(
                "{\"variants\":{\"\":{\"model\":\"" + model
                        + "\",\"x\":" + (transformed ? 90 : 0)
                        + ",\"weight\":" + weight + "}}}",
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState.class
        );
    }

    private static Variant onlyVariant(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state
    ) {
        return state.getVariants().getDefaultVariant().getVariants()[0];
    }

    private static BlockState state(String id, Map<String, String> properties) {
        return new BlockState(Key.parse(id), properties);
    }

    private static Model cubeModel(Key texture, boolean allFaces) {
        Map<Direction, Face> faces = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            if (allFaces || direction != Direction.DOWN) {
                faces.put(direction, new Face(
                        null,
                        new TextureVariable(new ResourcePath<Texture>(texture)),
                        direction
                ));
            }
        }
        return new Model(new Element(
                Vector3f.ZERO, new Vector3f(16, 16, 16), faces
        ));
    }
}
