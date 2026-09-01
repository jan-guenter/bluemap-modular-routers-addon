/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import com.flowpowered.math.vector.Vector3f;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
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
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;
import io.github.janguenter.bluemap.modularrouters.profile.GlassentialInteropProfile;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

/** Exact, class-link-free bridge to Glassential's generated contextless glass tile. */
final class GlassentialDefaultCamouflageBridge
        implements ModularRoutersRenderer.GlassentialTarget {

    private static final BlockState PROPERTYLESS_GLASS = new BlockState(
            Key.parse(GlassentialInteropProfile.GLASS), Map.of()
    );
    private static final Vector3f FULL_BLOCK_MAX = new Vector3f(16, 16, 16);

    private final boolean available;
    private final BlockRenderer renderer;
    private final Variant dispatchVariant;

    GlassentialDefaultCamouflageBridge(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            ModularRoutersResourceExtension extension
    ) {
        boolean valid = extension != null
                && extension.glassentialInteropArtifactPresent()
                && validActiveResources(resourcePack, extension);
        BlockRenderer foundRenderer = null;
        Variant foundDispatch = null;
        if (valid) {
            try {
                BlockRendererType rendererType = BlockRendererType.REGISTRY.get(
                        GlassentialInteropProfile.SYNTHETIC_DISPATCH
                );
                foundRenderer = rendererType.create(resourcePack, textures, settings);
                foundDispatch = onlyVariant(resourcePack.getBlockStates().get(
                        GlassentialInteropProfile.SYNTHETIC_DISPATCH
                ));
            } catch (RuntimeException | LinkageError exception) {
                valid = false;
            }
        }
        this.renderer = foundRenderer;
        this.dispatchVariant = foundDispatch;
        this.available = valid && renderer != null && dispatchVariant != null;
    }

    GlassentialDefaultCamouflageBridge(
            BlockRenderer renderer,
            Variant dispatchVariant
    ) {
        this.renderer = renderer;
        this.dispatchVariant = dispatchVariant;
        this.available = renderer != null && dispatchVariant != null;
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
        renderer.render(
                new CamouflageNeighborhood(originalHost, state),
                dispatchVariant,
                target,
                mapColor
        );
    }

    private static boolean validActiveResources(
            ResourcePack resourcePack,
            ModularRoutersResourceExtension modularRouters
    ) {
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
            if (!validDispatch(
                    dispatch,
                    rendererType,
                    variant -> variant.getRenderer() == rendererType
                            || modularRouters.originallyRenderedBy(variant, rendererType)
            )
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
        return validDispatch(
                state, rendererType, variant -> variant.getRenderer() == rendererType
        );
    }

    private static boolean validDispatch(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state,
            BlockRendererType rendererType,
            Predicate<Variant> rendererMatches
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
                && rendererMatches.test(variant)
                && GlassentialInteropProfile.SYNTHETIC_DISPATCH.equals(
                        rendererType.getKey()
                )
                && ResourcePack.MISSING_BLOCK_MODEL.equals(variant.getModel())
                && !variant.isTransformed()
                && !variant.isUvlock()
                && Double.compare(variant.getWeight(), 1D) == 0;
    }

    private static Variant onlyVariant(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state
    ) {
        return state.getVariants().getDefaultVariant().getVariants()[0];
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

    /** Target-state view for the exact Glassential renderer. */
    private static final class CamouflageNeighborhood extends BlockNeighborhood {

        private final BlockNeighborhood source;
        private final BlockState camouflage;

        private CamouflageNeighborhood(BlockNeighborhood source, BlockState camouflage) {
            super(
                    source,
                    source.getResourcePack(),
                    source.getRenderSettings(),
                    source.getDimensionType()
            );
            this.source = source;
            this.camouflage = camouflage;
            super.set(source.getX(), source.getY(), source.getZ());
        }

        @Override
        public BlockState getBlockState() {
            return camouflage;
        }

        @Override
        public BlockEntity getBlockEntity() {
            return null;
        }

        @Override
        public BlockProperties getProperties() {
            return getResourcePack().getBlockProperties(camouflage);
        }

        @Override
        public ExtendedBlock getNeighborBlock(int dx, int dy, int dz) {
            return dx == 0 && dy == 0 && dz == 0
                    ? this : source.getNeighborBlock(dx, dy, dz);
        }
    }
}
