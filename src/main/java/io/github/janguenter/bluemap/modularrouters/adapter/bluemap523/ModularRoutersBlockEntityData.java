/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

import java.util.List;

/** Narrow BlueNBT projection of the two persisted camouflage contracts. */
public final class ModularRoutersBlockEntityData extends MCABlockEntity {

    @NBTName("CamouflageName")
    private BlockState camouflageName;

    @NBTName("Upgrades")
    private UpgradeInventory upgrades;

    public ModularRoutersBlockEntityData() {
    }

    BlockState camouflageName() {
        return camouflageName;
    }

    UpgradeInventory upgrades() {
        return upgrades;
    }

    /** Exact NeoForge ItemStackHandler container shape. */
    public static final class UpgradeInventory {

        @NBTName("Size")
        private Integer size;

        @NBTName("Items")
        private List<UpgradeStack> items;

        public UpgradeInventory() {
        }

        Integer size() {
            return size;
        }

        List<UpgradeStack> items() {
            return items;
        }
    }

    /** Persisted ItemStack with its ItemStackHandler slot. */
    public static final class UpgradeStack {

        @NBTName("Slot")
        private Integer slot;

        private String id;
        private Integer count;
        private Components components;

        public UpgradeStack() {
        }

        Integer slot() {
            return slot;
        }

        String id() {
            return id;
        }

        Integer count() {
            return count;
        }

        Components components() {
            return components;
        }
    }

    /** ItemStack component patch containing the stable camouflage state. */
    public static final class Components {

        @NBTName("modularrouters:camouflage")
        private BlockState camouflage;

        public Components() {
        }

        BlockState camouflage() {
            return camouflage;
        }
    }
}
