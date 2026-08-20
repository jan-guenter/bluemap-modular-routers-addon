#!/usr/bin/env python3
"""Verify the exact All the Mons 1.2.0 Modular Routers artifact."""

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


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar", required=True, type=pathlib.Path)
    args = parser.parse_args()
    payload = args.jar.read_bytes()
    digest = hashlib.sha256(payload).hexdigest()
    if len(payload) != EXPECTED_SIZE or digest != EXPECTED_SHA256:
        print(
            f"pinned artifact mismatch: {len(payload)} bytes, SHA-256 {digest}",
            file=sys.stderr,
        )
        return 1
    with zipfile.ZipFile(args.jar) as archive:
        missing = sorted(REQUIRED.difference(archive.namelist()))
    if missing:
        print(f"pinned artifact missing entries: {missing}", file=sys.stderr)
        return 1
    print(f"verified Modular Routers 13.2.7: {len(payload)} bytes, SHA-256 {digest}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
