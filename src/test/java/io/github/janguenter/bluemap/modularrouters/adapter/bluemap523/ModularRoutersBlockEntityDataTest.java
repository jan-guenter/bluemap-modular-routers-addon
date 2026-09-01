/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.mca.MCAUtil;
import de.bluecolored.bluenbt.BlueNBT;
import de.bluecolored.bluenbt.NBTWriter;
import de.bluecolored.bluenbt.TagType;
import io.github.janguenter.bluemap.modularrouters.profile.ModularRoutersProfile;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModularRoutersBlockEntityDataTest {

    private final CamouflageSnapshotDecoder decoder = new CamouflageSnapshotDecoder();

    @Test
    void readsExactRouterUpgradeComponentAndProperties() throws Exception {
        ModularRoutersBlockEntityData data = readRouter(
                5, "minecraft:oak_log", "axis", "x", 1, false, false
        );

        Optional<BlockState> state = decoder.decode(ModularRoutersProfile.MODULAR_ROUTER, data);
        assertTrue(state.isPresent());
        assertEquals(ModularRoutersProfile.MODULAR_ROUTER, data.getId().getFormatted());
        assertEquals("minecraft:oak_log", state.orElseThrow().getId().getFormatted());
        assertEquals("x", state.orElseThrow().getProperties().get("axis"));
    }

    @Test
    void readsExactTemplateFrameCamouflageName() throws Exception {
        ModularRoutersBlockEntityData data = readFrame(
                "minecraft:oak_stairs", "facing", "east", true
        );
        Optional<BlockState> state = decoder.decode(ModularRoutersProfile.TEMPLATE_FRAME, data);
        assertEquals("minecraft:oak_stairs",
                state.orElseThrow().getId().getFormatted());
        assertEquals("east", state.orElseThrow().getProperties().get("facing"));
    }

    @Test
    void clientOnlyBlockStateNameIsNotAPersistedRoute() throws Exception {
        ModularRoutersBlockEntityData data = readRouterWithClientOnlyState();
        assertTrue(decoder.decode(ModularRoutersProfile.MODULAR_ROUTER, data).isEmpty());
    }

    @Test
    void wrongInventorySizeDuplicateSlotAndDuplicateUpgradeFallBack() throws Exception {
        assertTrue(decoder.decode(ModularRoutersProfile.MODULAR_ROUTER, readRouter(
                4, "minecraft:bricks", null, null, 1, false, false
        )).isEmpty());
        assertTrue(decoder.decode(ModularRoutersProfile.MODULAR_ROUTER, readRouter(
                5, "minecraft:bricks", null, null, 1, true, false
        )).isEmpty());
        assertTrue(decoder.decode(ModularRoutersProfile.MODULAR_ROUTER, readRouter(
                5, "minecraft:bricks", null, null, 1, false, true
        )).isEmpty());
    }

    @Test
    void invalidCountUnknownUpgradeAndRecursiveTargetsFallBack() throws Exception {
        assertTrue(decoder.decode(ModularRoutersProfile.MODULAR_ROUTER, readRouter(
                5, "minecraft:bricks", null, null, 2, false, false
        )).isEmpty());
        assertTrue(decoder.decode(ModularRoutersProfile.MODULAR_ROUTER,
                readRouterWithUnknownUpgrade()).isEmpty());
        assertTrue(decoder.decode(ModularRoutersProfile.MODULAR_ROUTER, readRouter(
                5, "modularrouters:template_frame", null, null, 1, false, false
        )).isEmpty());
        assertTrue(decoder.decode(ModularRoutersProfile.TEMPLATE_FRAME, readFrame(
                "chiselsandbits:chiseled_block", null, null, false
        )).isEmpty());
    }

    @Test
    void omittedDefaultCountAndLegitimateOtherUpgradeAreAccepted() throws Exception {
        ModularRoutersBlockEntityData data = readRouter(
                5, "minecraft:glass", null, null, null, false, false
        );
        assertEquals("minecraft:glass", decoder.decode(
                ModularRoutersProfile.MODULAR_ROUTER, data
        ).orElseThrow().getId().getFormatted());
    }

    private static ModularRoutersBlockEntityData readRouter(
            int size,
            String targetId,
            String property,
            String value,
            Integer count,
            boolean duplicateSlot,
            boolean duplicateCamouflage
    ) throws IOException {
        int copies = duplicateSlot || duplicateCamouflage ? 2 : 1;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.name("").beginCompound();
            common(writer, ModularRoutersProfile.MODULAR_ROUTER);
            writer.name("Upgrades").beginCompound();
            writer.name("Size").value(size);
            writer.name("Items").beginList(copies + 1, TagType.COMPOUND);
            upgrade(writer, 4, "modularrouters:energy_upgrade", 8,
                    null, null, null);
            for (int index = 0; index < copies; index++) {
                int slot = duplicateSlot ? 0 : index;
                String id = duplicateCamouflage || index == 0
                        ? "modularrouters:camouflage_upgrade"
                        : "modularrouters:blast_upgrade";
                upgrade(writer, slot, id, count, targetId, property, value);
            }
            writer.endList();
            writer.endCompound();
            writer.endCompound();
        }
        return read(bytes);
    }

    private static ModularRoutersBlockEntityData readFrame(
            String targetId,
            String property,
            String value,
            boolean mimic
    ) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.name("").beginCompound();
            common(writer, ModularRoutersProfile.TEMPLATE_FRAME);
            writer.name("Mimic").value((byte) (mimic ? 1 : 0));
            blockState(writer, "CamouflageName", targetId, property, value);
            writer.endCompound();
        }
        return read(bytes);
    }

    private static ModularRoutersBlockEntityData readRouterWithClientOnlyState()
            throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.name("").beginCompound();
            common(writer, ModularRoutersProfile.MODULAR_ROUTER);
            blockState(writer, "BlockStateName", "minecraft:bricks", null, null);
            writer.endCompound();
        }
        return read(bytes);
    }

    private static ModularRoutersBlockEntityData readRouterWithUnknownUpgrade()
            throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.name("").beginCompound();
            common(writer, ModularRoutersProfile.MODULAR_ROUTER);
            writer.name("Upgrades").beginCompound();
            writer.name("Size").value(5);
            writer.name("Items").beginList(2, TagType.COMPOUND);
            upgrade(writer, 0, "modularrouters:camouflage_upgrade", 1,
                    "minecraft:bricks", null, null);
            upgrade(writer, 1, "example:unknown_upgrade", 1, null, null, null);
            writer.endList();
            writer.endCompound();
            writer.endCompound();
        }
        return read(bytes);
    }

    private static void common(NBTWriter writer, String id) throws IOException {
        writer.name("id").value(id);
        writer.name("x").value(170);
        writer.name("y").value(100);
        writer.name("z").value(164);
    }

    private static void upgrade(
            NBTWriter writer,
            int slot,
            String id,
            Integer count,
            String targetId,
            String property,
            String value
    ) throws IOException {
        writer.beginCompound();
        writer.name("Slot").value(slot);
        writer.name("id").value(id);
        if (count != null) {
            writer.name("count").value(count);
        }
        if (targetId != null) {
            writer.name("components").beginCompound();
            blockState(writer, "modularrouters:camouflage", targetId, property, value);
            writer.endCompound();
        }
        writer.endCompound();
    }

    private static void blockState(
            NBTWriter writer,
            String field,
            String targetId,
            String property,
            String value
    ) throws IOException {
        writer.name(field).beginCompound();
        writer.name("Name").value(targetId);
        if (property != null) {
            writer.name("Properties").beginCompound();
            writer.name(property).value(value);
            writer.endCompound();
        }
        writer.endCompound();
    }

    private static ModularRoutersBlockEntityData read(ByteArrayOutputStream bytes)
            throws IOException {
        return MCAUtil.addCommonNbtSettings(new BlueNBT()).read(
                new ByteArrayInputStream(bytes.toByteArray()),
                ModularRoutersBlockEntityData.class
        );
    }
}
