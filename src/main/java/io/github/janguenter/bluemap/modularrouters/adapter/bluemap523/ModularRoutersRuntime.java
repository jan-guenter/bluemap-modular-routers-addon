/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.adapter.bluemap523;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Shared activation state and bounded diagnostics for the single route. */
final class ModularRoutersRuntime {

    static final ModularRoutersRuntime INSTANCE = new ModularRoutersRuntime();
    private static final int MAX_DIAGNOSTICS = 8;

    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicInteger diagnostics = new AtomicInteger();

    private ModularRoutersRuntime() {
    }

    boolean active() {
        return active.get();
    }

    void activate() {
        active.set(true);
    }

    void inactive(String reason) {
        active.set(false);
        report("inactive-" + reason);
    }

    void report(String reason) {
        if (diagnostics.incrementAndGet() <= MAX_DIAGNOSTICS) {
            System.err.println("BlueMap Modular Routers add-on: " + reason + ".");
        }
    }
}
