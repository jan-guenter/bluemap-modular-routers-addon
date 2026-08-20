/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap522;

import com.flowpowered.math.vector.Vector3f;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.modularrouters.profile.GlassentialInteropProfile;

import java.io.IOException;
import java.util.Map;

/** Exact, class-link-free bridge to Glassential's generated contextless glass tile. */
final class GlassentialDefaultCamouflageBridge
        implements ModularRoutersRenderer.GlassentialTarget {

    private static final BlockState PROPERTYLESS_GLASS = new BlockState(
            Key.parse(GlassentialInteropProfile.GLASS), Map.of()
    );
    private static final Vector3f FULL_BLOCK_MAX = new Vector3f(16, 16, 16);

    private final boolean available;
    private final ResourceModelRenderer renderer;
    private final Variant bridgeVariant = new Variant(
            new ResourcePath<Model>(GlassentialInteropProfile.BRIDGE_MODEL)
    );

    GlassentialDefaultCamouflageBridge(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            boolean exactAddonArtifactPresent
    ) {
        this.renderer = new ResourceModelRenderer(resourcePack, textures, settings);
        this.available = exactAddonArtifactPresent && validActiveResources(resourcePack);
    }

    @Override
    public boolean owns(BlockState state) {
        return ownsRoute(state);
    }

    static boolean ownsRoute(BlockState state) {
        return state != null && GlassentialInteropProfile.ROUTED_BLOCK_IDS.contains(
                state.getId().getFormatted()
        );
    }

    @Override
    public boolean accepts(BlockState state) {
        return available && isPropertylessGlass(state);
    }

    static boolean isPropertylessGlass(BlockState state) {
        return state != null
                && GlassentialInteropProfile.GLASS.equals(
                        state.getId().getFormatted()
                )
                && state.getProperties().isEmpty();
    }

    @Override
    public void render(
            BlockNeighborhood originalHost,
            BlockState state,
            TileModelView target,
            Color mapColor
    ) {
        if (!accepts(state)) {
            throw new IllegalArgumentException("unsupported Glassential camouflage route");
        }
        renderer.render(originalHost, bridgeVariant, target, mapColor);
    }

    private static boolean validActiveResources(ResourcePack resourcePack) {
        try {
            ResourcePack.Extension<?> type = ResourcePack.Extension.REGISTRY.get(
                    GlassentialInteropProfile.EXTENSION
            );
            ResourcePackExtension extension = type == null
                    ? null : resourcePack.getExtension(type);
            if (extension == null || !GlassentialInteropProfile.SYNTHETIC_DISPATCH.equals(
                    extension.getBlockStateKey(PROPERTYLESS_GLASS.getId())
            )) {
                return false;
            }

            BlockRendererType rendererType = BlockRendererType.REGISTRY.get(
                    GlassentialInteropProfile.SYNTHETIC_DISPATCH
            );
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
                    dispatch = resourcePack.getBlockStates().get(
                            GlassentialInteropProfile.SYNTHETIC_DISPATCH
                    );
            if (!validDispatch(dispatch, rendererType)
                    || resourcePack.getBlockState(PROPERTYLESS_GLASS) != dispatch
                    || !validTile(resourcePack.getTextures().get(
                            GlassentialInteropProfile.DEFAULT_GLASS_TILE
                    ))) {
                return false;
            }
            return validBridgeModel(resourcePack.getModels().get(
                    GlassentialInteropProfile.BRIDGE_MODEL
            ));
        } catch (IOException | RuntimeException | LinkageError exception) {
            return false;
        }
    }

    static boolean validDispatch(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state,
            BlockRendererType rendererType
    ) {
        if (state == null || rendererType == null || state.getMultipart() != null) {
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
        Variant variant = set.getVariants()[0];
        return variant != null
                && variant.getRenderer() == rendererType
                && GlassentialInteropProfile.SYNTHETIC_DISPATCH.equals(
                        rendererType.getKey()
                )
                && ResourcePack.MISSING_BLOCK_MODEL.equals(variant.getModel())
                && !variant.isTransformed()
                && !variant.isUvlock()
                && Double.compare(variant.getWeight(), 1D) == 0;
    }

    static boolean validTile(Texture tile) throws IOException {
        return tile != null
                && GlassentialInteropProfile.DEFAULT_GLASS_TILE.equals(tile.getKey())
                && tile.getTextureImage().getWidth() == 16
                && tile.getTextureImage().getHeight() == 16;
    }

    static boolean validBridgeModel(Model model) {
        if (model == null || model.getElements() == null
                || model.getElements().length != 1) {
            return false;
        }
        Element element = model.getElements()[0];
        if (element == null || !Vector3f.ZERO.equals(element.getFrom())
                || !FULL_BLOCK_MAX.equals(element.getTo())
                || element.getFaces().size() != Direction.values().length) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            Face face = element.getFaces().get(direction);
            if (face == null || face.getCullface() != direction
                    || face.getTintindex() >= 0 || face.getRotation() != 0) {
                return false;
            }
            ResourcePath<Texture> texture = face.getTexture().getTexturePath(
                    model.getTextures()::get
            );
            if (!GlassentialInteropProfile.DEFAULT_GLASS_TILE.equals(texture)) {
                return false;
            }
        }
        return true;
    }
}
