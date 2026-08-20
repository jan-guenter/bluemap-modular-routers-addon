/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.profile;

import java.util.Map;
import java.util.Set;

/** Closed host and block-entity roster from Modular Routers 13.2.7. */
public final class ModularRoutersProfile {

    public static final String ROUTE = "modularrouters-camouflage";
    public static final String MODULAR_ROUTER = "modularrouters:modular_router";
    public static final String TEMPLATE_FRAME = "modularrouters:template_frame";

    public static final Map<String, String> HOST_TO_BLOCK_ENTITY = Map.of(
            MODULAR_ROUTER, MODULAR_ROUTER,
            TEMPLATE_FRAME, TEMPLATE_FRAME
    );
    public static final Set<String> HOST_IDS = Set.copyOf(HOST_TO_BLOCK_ENTITY.keySet());
    public static final Set<String> BLOCK_ENTITY_IDS =
            Set.copyOf(HOST_TO_BLOCK_ENTITY.values());

    private ModularRoutersProfile() {
    }

    public static boolean matches(String hostId, String blockEntityId) {
        return blockEntityId != null
                && blockEntityId.equals(HOST_TO_BLOCK_ENTITY.get(hostId));
    }
}
