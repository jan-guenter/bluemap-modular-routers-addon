/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.profile;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExactGlassentialAddonArtifactDetectorTest {

    @Test
    void acceptsOnlyTheExternallySuppliedExactJar() {
        assertEquals(166_871L, ExactGlassentialAddonArtifactDetector.STANDALONE_SIZE);
        assertEquals(
                "9df99ffba26b1dd5a38452fb020e9a931b6a16a4ab4c374d85dad91cb9437e60",
                ExactGlassentialAddonArtifactDetector.STANDALONE_SHA256
        );
        assertEquals(166_916L, ExactGlassentialAddonArtifactDetector.AGGREGATE_SIZE);
        assertEquals(
                "1a6b5ec84cd6c1a1bb1f0f711ddec4d6cef4b493b80d8da4d1139ad8a4eba28c",
                ExactGlassentialAddonArtifactDetector.AGGREGATE_SHA256
        );
        String configured = System.getProperty("glassentialAddonJar");
        assertTrue(configured != null && !configured.isBlank(),
                "test JVM needs -PglassentialAddonJar=<exact JAR>");
        Path exact = Path.of(configured);
        assertTrue(ExactGlassentialAddonArtifactDetector.matches(List.of(exact)));
        assertFalse(ExactGlassentialAddonArtifactDetector.matches(List.of(
                Path.of("src/main/resources/bluemap.addon.json")
        )));
    }

    @Test
    void admitsOnlyTheTwoPinnedCompatibleIdentities() {
        assertTrue(ExactGlassentialAddonArtifactDetector.exactIdentity(
                ExactGlassentialAddonArtifactDetector.STANDALONE_SIZE,
                ExactGlassentialAddonArtifactDetector.STANDALONE_SHA256
        ));
        assertTrue(ExactGlassentialAddonArtifactDetector.exactIdentity(
                ExactGlassentialAddonArtifactDetector.AGGREGATE_SIZE,
                ExactGlassentialAddonArtifactDetector.AGGREGATE_SHA256
        ));
        assertFalse(ExactGlassentialAddonArtifactDetector.exactIdentity(
                ExactGlassentialAddonArtifactDetector.STANDALONE_SIZE,
                ExactGlassentialAddonArtifactDetector.AGGREGATE_SHA256
        ));
        assertFalse(ExactGlassentialAddonArtifactDetector.exactIdentity(
                ExactGlassentialAddonArtifactDetector.AGGREGATE_SIZE,
                ExactGlassentialAddonArtifactDetector.STANDALONE_SHA256
        ));
    }
}
