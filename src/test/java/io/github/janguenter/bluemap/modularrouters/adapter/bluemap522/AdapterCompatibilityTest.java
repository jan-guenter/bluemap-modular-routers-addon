/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap522;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdapterCompatibilityTest {

    @Test
    void acceptsOnlyAuditedBlueMapIdentities() {
        assertTrue(AdapterCompatibility.supported(
                "5.22-agent.backport-5.22-mc1.21.1-2",
                "9be321df995a1103808621d529eb72773e719d4d"));
        assertTrue(AdapterCompatibility.supported(
                "5.22", "fe5115d5548a30d34175b8e0449aaca280af199f"));
        assertFalse(AdapterCompatibility.supported("5.22", "wrong"));
        assertFalse(AdapterCompatibility.supported("5.23", "wrong"));
    }
}
