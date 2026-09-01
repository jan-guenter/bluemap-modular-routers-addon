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
GLASSENTIAL_ADDON_IDENTITIES = {
    (
        166_871,
        "9df99ffba26b1dd5a38452fb020e9a931b6a16a4ab4c374d85dad91cb9437e60",
    ),
    (
        166_916,
        "1a6b5ec84cd6c1a1bb1f0f711ddec4d6cef4b493b80d8da4d1139ad8a4eba28c",
    ),
}
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


def verify_glassential(path: pathlib.Path) -> bool:
    payload = path.read_bytes()
    digest = hashlib.sha256(payload).hexdigest()
    if (len(payload), digest) not in GLASSENTIAL_ADDON_IDENTITIES:
        print(
            "BlueMap Glassential add-on 0.1.0-alpha.2 mismatch: "
            f"{len(payload)} bytes, SHA-256 {digest}",
            file=sys.stderr,
        )
        return False
    with zipfile.ZipFile(path) as archive:
        missing = sorted(GLASSENTIAL_ADDON_REQUIRED.difference(archive.namelist()))
    if missing:
        print(
            f"BlueMap Glassential add-on 0.1.0-alpha.2 missing entries: {missing}",
            file=sys.stderr,
        )
        return False
    print(
        "verified BlueMap Glassential add-on 0.1.0-alpha.2: "
        f"{len(payload)} bytes, SHA-256 {digest}"
    )
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
    if not verify_glassential(args.glassential_addon):
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
