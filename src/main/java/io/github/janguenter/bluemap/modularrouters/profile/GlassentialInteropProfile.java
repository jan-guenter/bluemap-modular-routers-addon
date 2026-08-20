/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.profile;

import de.bluecolored.bluemap.core.util.Key;

import java.util.Set;

/** Closed interop roster from the exact released Glassential add-on. */
public final class GlassentialInteropProfile {

    public static final String ADDON_ID = "bluemap_glassential";
    public static final String GLASS = "minecraft:glass";
    public static final Key EXTENSION =
            Key.parse("bluemap_glassential:exact_profile");
    public static final Key SYNTHETIC_DISPATCH =
            Key.parse("bluemap_glassential:fusion_model");
    public static final Key DEFAULT_GLASS_TILE = Key.parse(
            "bluemap_glassential:tiles/glassential/block/glass/0"
    );
    public static final Key BRIDGE_MODEL =
            Key.parse("bluemap_modularrouters:block/glassential_default");

    public static final Set<String> ROUTED_BLOCK_IDS = Set.of(
            "glassential:colorable_glass",
            "glassential:colorable_glass_pane",
            "glassential:colorable_stained_glass",
            "glassential:colorable_stained_glass_pane",
            "glassential:glass_dark_ethereal",
            "glassential:glass_dark_ethereal_pane",
            "glassential:glass_dark_ethereal_reverse",
            "glassential:glass_dark_ethereal_reverse_pane",
            "glassential:glass_ethereal",
            "glassential:glass_ethereal_pane",
            "glassential:glass_ethereal_reverse",
            "glassential:glass_ethereal_reverse_pane",
            "glassential:glass_ghostly",
            "glassential:glass_ghostly_pane",
            "glassential:glass_light",
            "glassential:glass_light_pane",
            "glassential:glass_light_tinted",
            "glassential:glass_light_tinted_pane",
            "glassential:glass_redstone",
            "glassential:glass_redstone_pane",
            "glassential:glass_redstone_tinted",
            "glassential:glass_redstone_tinted_pane",
            "glassential:glass_slab",
            "glassential:gravity_glass",
            "glassential:ice_glass",
            "glassential:iron_glass",
            "glassential:obsidian_glass",
            "glassential:one_way_glass",
            "glassential:sandstone_glass",
            "glassential:stone_glass",
            "glassential:tinted_one_way_glass",
            "minecraft:black_stained_glass",
            "minecraft:blue_stained_glass",
            "minecraft:brown_stained_glass",
            "minecraft:cyan_stained_glass",
            GLASS,
            "minecraft:gray_stained_glass",
            "minecraft:green_stained_glass",
            "minecraft:light_blue_stained_glass",
            "minecraft:light_gray_stained_glass",
            "minecraft:lime_stained_glass",
            "minecraft:magenta_stained_glass",
            "minecraft:orange_stained_glass",
            "minecraft:pink_stained_glass",
            "minecraft:purple_stained_glass",
            "minecraft:red_stained_glass",
            "minecraft:tinted_glass",
            "minecraft:white_stained_glass",
            "minecraft:yellow_stained_glass"
    );

    private GlassentialInteropProfile() {
    }
}
