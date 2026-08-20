/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.modularrouters.profile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Set;

/** Exact-byte activation gate for the All the Mons 1.2.0 artifact. */
public final class ExactModularRoutersArtifactDetector {

    public static final long SIZE = 1_285_765L;
    public static final String SHA256 =
            "10f84e7f2d1bc7b655d8398d8c2e7146c4929c3ad2c97408f940ca86c1bf898c";
    private static final int MAX_ROOTS = 4096;

    private ExactModularRoutersArtifactDetector() {
    }

    public static boolean matches(Iterable<Path> roots) {
        int count = 0;
        Set<Path> inspected = new HashSet<>();
        for (Path root : roots) {
            if (++count > MAX_ROOTS || Thread.currentThread().isInterrupted()) {
                return false;
            }
            try {
                if (root == null || !Files.isRegularFile(root) || Files.size(root) != SIZE) {
                    continue;
                }
                Path real = root.toRealPath();
                if (inspected.add(real) && SHA256.equals(digest(real))) {
                    return true;
                }
            } catch (IOException exception) {
                return false;
            }
        }
        return false;
    }

    private static String digest(Path path) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
        byte[] buffer = new byte[64 * 1024];
        try (InputStream input = Files.newInputStream(path)) {
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
