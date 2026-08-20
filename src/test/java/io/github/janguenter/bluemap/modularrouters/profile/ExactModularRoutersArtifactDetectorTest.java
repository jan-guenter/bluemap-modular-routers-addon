/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.profile;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExactModularRoutersArtifactDetectorTest {

    @Test
    void acceptsOnlyTheExternallySuppliedExactJar() {
        String configured = System.getProperty("modularRoutersJar");
        assertTrue(configured != null && !configured.isBlank(),
                "test JVM needs -PmodularRoutersJar=<exact JAR>");
        Path exact = Path.of(configured);
        assertTrue(ExactModularRoutersArtifactDetector.matches(List.of(exact)));
        assertFalse(ExactModularRoutersArtifactDetector.matches(List.of(
                Path.of("src/main/resources/bluemap.addon.json"))));
    }
}
