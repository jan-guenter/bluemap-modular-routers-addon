#!/usr/bin/env python3
"""Verify the exact Modular Routers and Glassential bridge artifacts."""

from __future__ import annotations

import argparse
import hashlib
import pathlib
import sys
import zipfile

EXPECTED_SIZE = 1_285_765
EXPECTED_SHA256 = "10f84e7f2d1bc7b655d8398d8c2e7146c4929c3ad2c97408f940ca86c1bf898c"
REQUIRED = {
    "META-INF/neoforge.mods.toml",
    "assets/modularrouters/blockstates/modular_router.json",
    "assets/modularrouters/blockstates/template_frame.json",
    "me/desht/modularrouters/block/tile/ModularRouterBlockEntity.class",
    "me/desht/modularrouters/block/tile/TemplateFrameBlockEntity.class",
}
GLASSENTIAL_ADDON_SIZE = 166_871
GLASSENTIAL_ADDON_SHA256 = (
    "9df99ffba26b1dd5a38452fb020e9a931b6a16a4ab4c374d85dad91cb9437e60"
)
GLASSENTIAL_ADDON_REQUIRED = {
    "bluemap.addon.json",
    "assets/bluemap_glassential/blockstates/fusion_model.json",
    (
        "io/github/janguenter/bluemap/glassential/adapter/bluemap523/"
        "GlassentialResourceExtension.class"
    ),
    (
        "bluemap-glassential/profiles/glassential/3.4.5-fusion-1.3.12/"
        "profile.json"
    ),
}


def verify(
    path: pathlib.Path,
    expected_size: int,
    expected_sha256: str,
    required: set[str],
    label: str,
) -> bool:
    payload = path.read_bytes()
    digest = hashlib.sha256(payload).hexdigest()
    if len(payload) != expected_size or digest != expected_sha256:
        print(
            f"{label} mismatch: {len(payload)} bytes, SHA-256 {digest}",
            file=sys.stderr,
        )
        return False
    with zipfile.ZipFile(path) as archive:
        missing = sorted(required.difference(archive.namelist()))
    if missing:
        print(f"{label} missing entries: {missing}", file=sys.stderr)
        return False
    print(f"verified {label}: {len(payload)} bytes, SHA-256 {digest}")
    return True


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar", required=True, type=pathlib.Path)
    parser.add_argument("--glassential-addon", required=True, type=pathlib.Path)
    args = parser.parse_args()
    if not verify(
        args.jar,
        EXPECTED_SIZE,
        EXPECTED_SHA256,
        REQUIRED,
        "Modular Routers 13.2.7",
    ):
        return 1
    if not verify(
        args.glassential_addon,
        GLASSENTIAL_ADDON_SIZE,
        GLASSENTIAL_ADDON_SHA256,
        GLASSENTIAL_ADDON_REQUIRED,
        "BlueMap Glassential add-on 0.1.0-alpha.2",
    ):
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
