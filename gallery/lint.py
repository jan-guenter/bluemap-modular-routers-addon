#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated Modular Routers gallery without starting Minecraft."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import generate


ROOT = Path(__file__).resolve().parent


def fail(message: str) -> None:
    raise ValueError(message)


def main() -> int:
    expected = generate.generated_files()
    for relative, payload in expected.items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            fail(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )

    function_root = ROOT / f"datapack/data/{generate.NAMESPACE}/function"
    build = (function_root / "build.mcfunction").read_text(encoding="utf-8")
    verify = (function_root / "verify.mcfunction").read_text(encoding="utf-8")
    if len(re.findall(r"^setblock ", build, re.MULTILINE)) != 4:
        fail("build must contain exactly four logical placements")
    if len(re.findall(r"^data merge block ", build, re.MULTILINE)) != 3:
        fail("build must contain exactly three camouflage payload merges")
    if len(re.findall(r"^scoreboard players add #checked ", verify, re.MULTILINE)) != 8:
        fail("verify must contain exactly eight retained-state checks")
    if "scoreboard players add #builds mr_gallery 1" not in build:
        fail("one-build counter is missing")

    for _label, x, y, z, _block, _nbt in generate.PLACEMENTS:
        envelope = generate.ENVELOPE
        if not (
            envelope["min_x"] <= x <= envelope["max_x"]
            and envelope["min_y"] <= y <= envelope["max_y"]
            and envelope["min_z"] <= z <= envelope["max_z"]
        ):
            fail(f"placement escaped safe envelope: {(x, y, z)}")

    required = (
        'Upgrades:{Size:5,Items:[{Slot:0,id:"modularrouters:camouflage_upgrade"',
        '"modularrouters:camouflage":{Name:"minecraft:oak_log",Properties:{axis:"x"}}',
        'CamouflageName:{Name:"minecraft:glass"}',
        'Name:"minecraft:deepslate_brick_stairs"',
        'facing:"east",half:"top",shape:"straight",waterlogged:"false"',
    )
    for token in required:
        if token not in build:
            fail(f"missing exact persisted camouflage token: {token}")
    forbidden = ("BlockStateName", "Buffer", "Modules", "active=true", "Mimic:1b")
    for token in forbidden:
        if token in build:
            fail(f"out-of-scope gallery token present: {token}")

    print("Modular Routers gallery lint passed: 4 placements, 8 checks/phase")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except ValueError as error:
        print(f"lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
