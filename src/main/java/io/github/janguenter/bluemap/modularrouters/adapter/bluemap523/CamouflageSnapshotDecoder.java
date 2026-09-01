/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/** Strict bounded decoder for persisted router and template-frame camouflage. */
final class CamouflageSnapshotDecoder {

    private static final String CAMOUFLAGE_UPGRADE =
            "modularrouters:camouflage_upgrade";
    private static final int UPGRADE_SLOTS = 5;
    private static final int MAX_PROPERTIES = 32;
    private static final int MAX_TEXT = 128;
    private static final Pattern TOKEN = Pattern.compile("[a-z0-9_./:-]+");
    private static final Map<String, Integer> UPGRADE_LIMITS = Map.ofEntries(
            Map.entry("modularrouters:blast_upgrade", 1),
            Map.entry(CAMOUFLAGE_UPGRADE, 1),
            Map.entry("modularrouters:energy_upgrade", 64),
            Map.entry("modularrouters:fluid_upgrade", 35),
            Map.entry("modularrouters:muffler_upgrade", 3),
            Map.entry("modularrouters:security_upgrade", 1),
            Map.entry("modularrouters:speed_upgrade", 9),
            Map.entry("modularrouters:stack_upgrade", 6),
            Map.entry("modularrouters:sync_upgrade", 1)
    );

    Optional<BlockState> decode(
            String hostId,
            ModularRoutersBlockEntityData data
    ) {
        if (data == null) {
            return Optional.empty();
        }
        if (ModularRoutersProfile.TEMPLATE_FRAME.equals(hostId)) {
            return valid(data.camouflageName())
                    ? Optional.of(data.camouflageName()) : Optional.empty();
        }
        if (!ModularRoutersProfile.MODULAR_ROUTER.equals(hostId)) {
            return Optional.empty();
        }
        return decodeRouter(data.upgrades());
    }

    private Optional<BlockState> decodeRouter(
            ModularRoutersBlockEntityData.UpgradeInventory upgrades
    ) {
        if (upgrades == null || upgrades.size() == null
                || upgrades.size() != UPGRADE_SLOTS) {
            return Optional.empty();
        }
        List<ModularRoutersBlockEntityData.UpgradeStack> items = upgrades.items();
        if (items == null || items.isEmpty() || items.size() > UPGRADE_SLOTS) {
            return Optional.empty();
        }

        Set<Integer> occupiedSlots = new HashSet<>();
        Set<String> installedIds = new HashSet<>();
        BlockState found = null;
        for (ModularRoutersBlockEntityData.UpgradeStack item : items) {
            if (item == null || item.slot() == null
                    || item.slot() < 0 || item.slot() >= UPGRADE_SLOTS
                    || !occupiedSlots.add(item.slot())
                    || item.id() == null || !installedIds.add(item.id())) {
                return Optional.empty();
            }
            Integer limit = UPGRADE_LIMITS.get(item.id());
            int count = item.count() == null ? 1 : item.count();
            if (limit == null || count < 1 || count > limit) {
                return Optional.empty();
            }
            if (!CAMOUFLAGE_UPGRADE.equals(item.id())) {
                continue;
            }
            ModularRoutersBlockEntityData.Components components = item.components();
            BlockState camouflage = components == null ? null : components.camouflage();
            if (found != null || count != 1 || !valid(camouflage)) {
                return Optional.empty();
            }
            found = camouflage;
        }
        return Optional.ofNullable(found);
    }

    private static boolean valid(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        String id = state.getId().getFormatted();
        if (id.length() > MAX_TEXT || id.startsWith("modularrouters:")
                || id.startsWith("chiselsandbits:") || !TOKEN.matcher(id).matches()
                || state.getProperties().size() > MAX_PROPERTIES) {
            return false;
        }
        return state.getProperties().entrySet().stream().allMatch(entry ->
                entry.getKey() != null && entry.getValue() != null
                        && entry.getKey().length() <= MAX_TEXT
                        && entry.getValue().length() <= MAX_TEXT
                        && TOKEN.matcher(entry.getKey()).matches()
                        && TOKEN.matcher(entry.getValue()).matches());
    }
}
