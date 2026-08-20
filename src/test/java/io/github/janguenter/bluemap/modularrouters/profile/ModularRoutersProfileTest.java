/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModularRoutersProfileTest {

    @Test
    void exactHostAndBlockEntityRosterIsClosed() {
        assertEquals(2, ModularRoutersProfile.HOST_IDS.size());
        assertEquals(2, ModularRoutersProfile.BLOCK_ENTITY_IDS.size());
        assertTrue(ModularRoutersProfile.matches(
                ModularRoutersProfile.MODULAR_ROUTER,
                ModularRoutersProfile.MODULAR_ROUTER));
        assertTrue(ModularRoutersProfile.matches(
                ModularRoutersProfile.TEMPLATE_FRAME,
                ModularRoutersProfile.TEMPLATE_FRAME));
        assertFalse(ModularRoutersProfile.matches(
                ModularRoutersProfile.MODULAR_ROUTER,
                ModularRoutersProfile.TEMPLATE_FRAME));
        assertFalse(ModularRoutersProfile.HOST_IDS.contains("minecraft:stone"));
    }
}
