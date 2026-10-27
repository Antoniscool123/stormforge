#!/usr/bin/env python3
"""Generates Stormforge's data-driven content (models, blockstates, loot, recipes, tags,
advancements, worldgen, lang) and placeholder pixel-art textures.

Run from the repository root:  python3 -I tools/gen_assets.py
Safe to re-run; it overwrites the files it owns and merges into lang/en_us.json.
"""
import json
import math
import os
import random

from PIL import Image

RES = "src/main/resources"
NS = "stormforge"


def w(path, data):
    path = os.path.join(RES, path)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(data, f, indent=2)
        f.write("\n")


def save(img, path):
    path = os.path.join(RES, path)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)


# ====================================================================================
# Content definitions
# ====================================================================================
# name -> (kind, texture/base). Textures live in assets/stormforge/textures/block/<texture>.png
BLOCKS = {
    "stormstone": ("cube", "stormstone"),
    "stormstone_stairs": ("stairs", "stormstone"),
    "stormstone_slab": ("slab", "stormstone"),
    "stormstone_wall": ("wall", "stormstone"),
    "polished_stormstone": ("cube", "polished_stormstone"),
    "storm_bricks": ("cube", "storm_bricks"),
    "storm_brick_stairs": ("stairs", "storm_bricks"),
    "storm_brick_slab": ("slab", "storm_bricks"),
    "storm_brick_wall": ("wall", "storm_bricks"),
    "chiseled_storm_bricks": ("cube", "chiseled_storm_bricks"),
    "storm_pillar": ("pillar", "storm_pillar"),
    "storm_glass": ("glass", "storm_glass"),
    "storm_lamp": ("cube", "storm_lamp"),
}
# slabs need the full-block model of their material for the "double" state
SLAB_FULL = {"stormstone_slab": "stormstone", "storm_brick_slab": "storm_bricks"}

ITEMS_GENERATED = [
    "tempest_ingot", "thunder_apple", "storm_vial",
    "tempest_helmet", "tempest_chestplate", "tempest_leggings", "tempest_boots",
]
ITEMS_HANDHELD = ["tempest_pickaxe", "tempest_axe", "tempest_shovel", "tempest_hoe"]

LANG = {
    "item.stormforge.tempest_ingot": "Tempest Ingot",
    "item.stormforge.tempest_pickaxe": "Tempest Pickaxe",
    "item.stormforge.tempest_axe": "Tempest Axe",
    "item.stormforge.tempest_shovel": "Tempest Shovel",
    "item.stormforge.tempest_hoe": "Tempest Hoe",
    "item.stormforge.tempest_helmet": "Tempest Helmet",
    "item.stormforge.tempest_chestplate": "Tempest Chestplate",
    "item.stormforge.tempest_leggings": "Tempest Leggings",
    "item.stormforge.tempest_boots": "Tempest Boots",
    "item.stormforge.thunder_apple": "Thunder Apple",
    "item.stormforge.storm_vial": "Storm Vial",
    "block.stormforge.stormstone": "Stormstone",
    "block.stormforge.stormstone_stairs": "Stormstone Stairs",
    "block.stormforge.stormstone_slab": "Stormstone Slab",
    "block.stormforge.stormstone_wall": "Stormstone Wall",
    "block.stormforge.polished_stormstone": "Polished Stormstone",
    "block.stormforge.storm_bricks": "Storm Bricks",
    "block.stormforge.storm_brick_stairs": "Storm Brick Stairs",
    "block.stormforge.storm_brick_slab": "Storm Brick Slab",
    "block.stormforge.storm_brick_wall": "Storm Brick Wall",
    "block.stormforge.chiseled_storm_bricks": "Chiseled Storm Bricks",
    "block.stormforge.storm_pillar": "Storm Pillar",
    "block.stormforge.storm_glass": "Stormglass",
    "block.stormforge.storm_lamp": "Storm Lamp",
    "effect.stormforge.static_charge": "Static Charge",
    "itemGroup.stormforge.main": "Stormforge",
}

# ====================================================================================
# Models, blockstates, item definitions
# ====================================================================================


def item_def(name, model):
    w(f"assets/{NS}/items/{name}.json", {"model": {"type": "minecraft:model", "model": model}})


def gen_items():
    for n in ITEMS_GENERATED:
        item_def(n, f"{NS}:item/{n}")
        w(f"assets/{NS}/models/item/{n}.json",
          {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{n}"}})
    for n in ITEMS_HANDHELD:
        item_def(n, f"{NS}:item/{n}")
        w(f"assets/{NS}/models/item/{n}.json",
          {"parent": "minecraft:item/handheld", "textures": {"layer0": f"{NS}:item/{n}"}})


def stairs_blockstate(name):
    base = f"{NS}:block/{name}"
    rot = {  # (facing, half, shape) -> (x, y)
    }
    bottom_y = {
        "straight": {"east": 0, "south": 90, "west": 180, "north": 270},
        "outer_right": {"east": 0, "south": 90, "west": 180, "north": 270},
        "inner_right": {"east": 0, "south": 90, "west": 180, "north": 270},
        "outer_left": {"east": 270, "south": 0, "west": 90, "north": 180},
        "inner_left": {"east": 270, "south": 0, "west": 90, "north": 180},
    }
    top_y = {
        "straight": {"east": 0, "south": 90, "west": 180, "north": 270},
        "outer_left": {"east": 0, "south": 90, "west": 180, "north": 270},
        "inner_left": {"east": 0, "south": 90, "west": 180, "north": 270},
        "outer_right": {"east": 90, "south": 180, "west": 270, "north": 0},
        "inner_right": {"east": 90, "south": 180, "west": 270, "north": 0},
    }
    variants = {}
    for facing in ("east", "south", "west", "north"):
        for shape in ("straight", "inner_left", "inner_right", "outer_left", "outer_right"):
            model = base if shape == "straight" else base + ("_inner" if shape.startswith("inner") else "_outer")
            for half in ("bottom", "top"):
                x = 0 if half == "bottom" else 180
                y = (bottom_y if half == "bottom" else top_y)[shape][facing]
                entry = {"model": model}
                if x:
                    entry["x"] = x
                if y:
                    entry["y"] = y
                if x or y:
                    entry["uvlock"] = True
                variants[f"facing={facing},half={half},shape={shape}"] = entry
    return {"variants": variants}


def wall_blockstate(name):
    post, side, tall = (f"{NS}:block/{name}_post", f"{NS}:block/{name}_side", f"{NS}:block/{name}_side_tall")
    parts = [{"when": {"up": "true"}, "apply": {"model": post}}]
    for kind, model in (("low", side), ("tall", tall)):
        for direction, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
            apply = {"model": model, "uvlock": True}
            if y:
                apply["y"] = y
            parts.append({"when": {direction: kind}, "apply": apply})
    return {"multipart": parts}


def gen_blocks():
    for name, (kind, tex) in BLOCKS.items():
        t = f"{NS}:block/{tex}"
        if kind == "cube":
            w(f"assets/{NS}/blockstates/{name}.json", {"variants": {"": {"model": f"{NS}:block/{name}"}}})
            w(f"assets/{NS}/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": t}})
            item_def(name, f"{NS}:block/{name}")
        elif kind == "glass":
            w(f"assets/{NS}/blockstates/{name}.json", {"variants": {"": {"model": f"{NS}:block/{name}"}}})
            w(f"assets/{NS}/models/block/{name}.json", {
                "parent": "minecraft:block/cube_all",
                "textures": {"all": {"force_translucent": True, "sprite": t}}})
            item_def(name, f"{NS}:block/{name}")
        elif kind == "pillar":
            m = f"{NS}:block/{name}"
            w(f"assets/{NS}/blockstates/{name}.json", {"variants": {
                "axis=y": {"model": m},
                "axis=z": {"model": m, "x": 90},
                "axis=x": {"model": m, "x": 90, "y": 90}}})
            w(f"assets/{NS}/models/block/{name}.json", {
                "parent": "minecraft:block/cube_column",
                "textures": {"end": f"{NS}:block/{tex}_top", "side": t}})
            item_def(name, m)
        elif kind == "stairs":
            w(f"assets/{NS}/blockstates/{name}.json", stairs_blockstate(name))
            textures = {"bottom": t, "side": t, "top": t}
            w(f"assets/{NS}/models/block/{name}.json", {"parent": "minecraft:block/stairs", "textures": textures})
            w(f"assets/{NS}/models/block/{name}_inner.json", {"parent": "minecraft:block/inner_stairs", "textures": textures})
            w(f"assets/{NS}/models/block/{name}_outer.json", {"parent": "minecraft:block/outer_stairs", "textures": textures})
            item_def(name, f"{NS}:block/{name}")
        elif kind == "slab":
            textures = {"bottom": t, "side": t, "top": t}
            w(f"assets/{NS}/blockstates/{name}.json", {"variants": {
                "type=bottom": {"model": f"{NS}:block/{name}"},
                "type=double": {"model": f"{NS}:block/{SLAB_FULL[name]}"},
                "type=top": {"model": f"{NS}:block/{name}_top"}}})
            w(f"assets/{NS}/models/block/{name}.json", {"parent": "minecraft:block/slab", "textures": textures})
            w(f"assets/{NS}/models/block/{name}_top.json", {"parent": "minecraft:block/slab_top", "textures": textures})
            item_def(name, f"{NS}:block/{name}")
        elif kind == "wall":
            w(f"assets/{NS}/blockstates/{name}.json", wall_blockstate(name))
            textures = {"wall": t}
            w(f"assets/{NS}/models/block/{name}_post.json", {"parent": "minecraft:block/template_wall_post", "textures": textures})
            w(f"assets/{NS}/models/block/{name}_side.json", {"parent": "minecraft:block/template_wall_side", "textures": textures})
            w(f"assets/{NS}/models/block/{name}_side_tall.json", {"parent": "minecraft:block/template_wall_side_tall", "textures": textures})
            w(f"assets/{NS}/models/block/{name}_inventory.json", {"parent": "minecraft:block/wall_inventory", "textures": textures})
            item_def(name, f"{NS}:block/{name}_inventory")


def gen_equipment():
    w(f"assets/{NS}/equipment/tempest.json", {"layers": {
        "humanoid": [{"texture": f"{NS}:tempest"}],
        "humanoid_baby": [{"texture": f"{NS}:tempest"}],
        "humanoid_leggings": [{"texture": f"{NS}:tempest"}],
    }})


# ====================================================================================
# Loot tables, tags, recipes
# ====================================================================================


def gen_loot():
    for name, (kind, _) in BLOCKS.items():
        rid = f"{NS}:{name}"
        seq = f"{NS}:blocks/{name}"
        if kind == "glass":
            data = {"type": "minecraft:block", "pools": [{
                "rolls": 1, "condition": "minecraft:tool/can_silk_touch",
                "entries": [{"type": "minecraft:item", "name": rid}]}],
                "random_sequence": seq}
        elif kind == "slab":
            data = {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{
                "type": "minecraft:item", "name": rid,
                "modifier": [
                    {"type": "minecraft:set_count", "count": 2,
                     "condition": {"type": "minecraft:match_block", "blocks": rid, "state": {"type": "double"}}},
                    {"type": "minecraft:explosion_decay"}]}]}],
                "random_sequence": seq}
        else:
            data = {"type": "minecraft:block", "pools": [{
                "rolls": 1, "condition": {"type": "minecraft:survives_explosion"},
                "entries": [{"type": "minecraft:item", "name": rid}]}],
                "random_sequence": seq}
        w(f"data/{NS}/loot_table/blocks/{name}.json", data)


def tag(path, values):
    w(path, {"values": values})


def gen_tags():
    blocks = [f"{NS}:{n}" for n in BLOCKS]
    tag("data/minecraft/tags/block/mineable/pickaxe.json",
        [f"{NS}:storm_ore", f"{NS}:storm_block"] + blocks)
    tag("data/minecraft/tags/block/needs_iron_tool.json", [f"{NS}:storm_ore", f"{NS}:storm_block"])
    for group, suffix in (("stairs", "_stairs"), ("slabs", "_slab"), ("walls", "_wall")):
        vals = [f"{NS}:{n}" for n in BLOCKS if n.endswith(suffix)]
        tag(f"data/minecraft/tags/block/{group}.json", vals)
        tag(f"data/minecraft/tags/item/{group}.json", vals)
    tag("data/minecraft/tags/item/head_armor.json", [f"{NS}:tempest_helmet"])
    tag("data/minecraft/tags/item/chest_armor.json", [f"{NS}:tempest_chestplate"])
    tag("data/minecraft/tags/item/leg_armor.json", [f"{NS}:tempest_leggings"])
    tag("data/minecraft/tags/item/foot_armor.json", [f"{NS}:tempest_boots"])
    tag("data/minecraft/tags/item/pickaxes.json", [f"{NS}:tempest_pickaxe"])
    tag("data/minecraft/tags/item/axes.json", [f"{NS}:tempest_axe"])
    tag("data/minecraft/tags/item/shovels.json", [f"{NS}:tempest_shovel"])
    tag("data/minecraft/tags/item/hoes.json", [f"{NS}:tempest_hoe"])
    tag("data/minecraft/tags/item/swords.json", [f"{NS}:storm_blade"])
    tag(f"data/{NS}/tags/item/storm_repair.json", [f"{NS}:storm_shard"])
    tag(f"data/{NS}/tags/item/tempest_repair.json", [f"{NS}:tempest_ingot"])


def shaped(name, pattern, key, result, count=1, category="misc"):
    out = {"id": result}
    if count != 1:
        out["count"] = count
    w(f"data/{NS}/recipe/{name}.json",
      {"type": "minecraft:crafting_shaped", "category": category, "key": key, "pattern": pattern, "result": out})


def shapeless(name, ingredients, result, count=1, category="misc"):
    out = {"id": result}
    if count != 1:
        out["count"] = count
    w(f"data/{NS}/recipe/{name}.json",
      {"type": "minecraft:crafting_shapeless", "category": category, "ingredients": ingredients, "result": out})


def cut(name, ingredient, result, count=1):
    out = {"id": result}
    if count != 1:
        out["count"] = count
    w(f"data/{NS}/recipe/{name}.json",
      {"type": "minecraft:stonecutting", "ingredient": ingredient, "result": out})


def gen_recipes():
    S = f"{NS}:storm_shard"
    T = f"{NS}:tempest_ingot"
    # materials
    shapeless("tempest_ingot", [S, S, "minecraft:iron_ingot"], T, 2)
    # tools
    stick = "minecraft:stick"
    shaped("tempest_pickaxe", ["XXX", " # ", " # "], {"X": T, "#": stick}, f"{NS}:tempest_pickaxe", category="equipment")
    shaped("tempest_axe", ["XX", "X#", " #"], {"X": T, "#": stick}, f"{NS}:tempest_axe", category="equipment")
    shaped("tempest_shovel", ["X", "#", "#"], {"X": T, "#": stick}, f"{NS}:tempest_shovel", category="equipment")
    shaped("tempest_hoe", ["XX", " #", " #"], {"X": T, "#": stick}, f"{NS}:tempest_hoe", category="equipment")
    # armor
    shaped("tempest_helmet", ["XXX", "X X"], {"X": T}, f"{NS}:tempest_helmet", category="equipment")
    shaped("tempest_chestplate", ["X X", "XXX", "XXX"], {"X": T}, f"{NS}:tempest_chestplate", category="equipment")
    shaped("tempest_leggings", ["XXX", "X X", "X X"], {"X": T}, f"{NS}:tempest_leggings", category="equipment")
    shaped("tempest_boots", ["X X", "X X"], {"X": T}, f"{NS}:tempest_boots", category="equipment")
    # consumables
    shaped("thunder_apple", ["SSS", "SAS", "SSS"], {"S": S, "A": "minecraft:apple"}, f"{NS}:thunder_apple")
    shapeless("storm_vial", ["minecraft:glass_bottle", S, "minecraft:sugar"], f"{NS}:storm_vial")
    # building blocks
    shaped("stormstone", ["SSS", "S#S", "SSS"], {"S": "minecraft:stone", "#": S}, f"{NS}:stormstone", 8, "building")
    shaped("polished_stormstone", ["##", "##"], {"#": f"{NS}:stormstone"}, f"{NS}:polished_stormstone", 4, "building")
    shaped("storm_bricks", ["##", "##"], {"#": f"{NS}:polished_stormstone"}, f"{NS}:storm_bricks", 4, "building")
    shaped("chiseled_storm_bricks", ["#", "#"], {"#": f"{NS}:storm_brick_slab"}, f"{NS}:chiseled_storm_bricks", 1, "building")
    shaped("storm_pillar", ["#", "#"], {"#": f"{NS}:storm_bricks"}, f"{NS}:storm_pillar", 2, "building")
    shaped("storm_glass", ["GGG", "GSG", "GGG"], {"G": "minecraft:glass", "S": S}, f"{NS}:storm_glass", 8, "building")
    shaped("storm_lamp", [" S ", "SGS", " S "], {"S": S, "G": "minecraft:glowstone"}, f"{NS}:storm_lamp", 1, "building")
    for base in ("stormstone", "storm_brick"):
        mat = f"{NS}:stormstone" if base == "stormstone" else f"{NS}:storm_bricks"
        shaped(f"{base}_stairs", ["#  ", "## ", "###"], {"#": mat}, f"{NS}:{base}_stairs", 4, "building")
        shaped(f"{base}_slab", ["###"], {"#": mat}, f"{NS}:{base}_slab", 6, "building")
        shaped(f"{base}_wall", ["###", "###"], {"#": mat}, f"{NS}:{base}_wall", 6, "building")
    # stonecutting
    ss, pol, br = f"{NS}:stormstone", f"{NS}:polished_stormstone", f"{NS}:storm_bricks"
    cut("stormstone_stairs_from_stormstone_stonecutting", ss, f"{NS}:stormstone_stairs")
    cut("stormstone_slab_from_stormstone_stonecutting", ss, f"{NS}:stormstone_slab", 2)
    cut("stormstone_wall_from_stormstone_stonecutting", ss, f"{NS}:stormstone_wall")
    cut("polished_stormstone_from_stormstone_stonecutting", ss, pol)
    cut("storm_bricks_from_stormstone_stonecutting", ss, br)
    cut("storm_bricks_from_polished_stormstone_stonecutting", pol, br)
    cut("storm_brick_stairs_from_storm_bricks_stonecutting", br, f"{NS}:storm_brick_stairs")
    cut("storm_brick_slab_from_storm_bricks_stonecutting", br, f"{NS}:storm_brick_slab", 2)
    cut("storm_brick_wall_from_storm_bricks_stonecutting", br, f"{NS}:storm_brick_wall")
    cut("chiseled_storm_bricks_from_storm_bricks_stonecutting", br, f"{NS}:chiseled_storm_bricks")
    cut("storm_pillar_from_storm_bricks_stonecutting", br, f"{NS}:storm_pillar")


# ====================================================================================
# Advancements
# ====================================================================================


def adv(name, parent, icon, key, items, any_of=False, frame=None, xp=0, root=False):
    criteria = {}
    for crit, item in items.items():
        criteria[crit] = {"trigger": "minecraft:inventory_changed",
                          "conditions": {"items": [{"items": item}]}}
    names = list(items)
    data = {
        "criteria": criteria,
        "display": {
            "icon": {"id": icon},
            "title": {"translate": f"advancements.{NS}.{key}.title"},
            "description": {"translate": f"advancements.{NS}.{key}.description"},
        },
        "requirements": [names] if any_of else [[n] for n in names],
    }
    if root:
        data["display"]["background"] = "minecraft:gui/advancements/backgrounds/stone"
        data["display"]["show_toast"] = False
        data["display"]["announce_to_chat"] = False
    else:
        data["parent"] = f"{NS}:{parent}"
    if frame:
        data["display"]["frame"] = frame
    if xp:
        data["rewards"] = {"experience": xp}
    w(f"data/{NS}/advancement/{name}.json", data)


ADV_LANG = {}


def adv_text(key, title, desc):
    ADV_LANG[f"advancements.{NS}.{key}.title"] = title
    ADV_LANG[f"advancements.{NS}.{key}.description"] = desc


def gen_advancements():
    adv("root", None, f"{NS}:storm_shard", "root", {"shard": f"{NS}:storm_shard"}, root=True)
    adv_text("root", "Stormforge", "Mine Storm Ore or craft a Storm Shard")

    adv("forged", "root", f"{NS}:tempest_ingot", "forged", {"ingot": f"{NS}:tempest_ingot"})
    adv_text("forged", "Alloyed in Lightning", "Forge a Tempest Ingot from Storm Shards and iron")

    adv("caller", "root", f"{NS}:stormcaller_staff", "caller", {"staff": f"{NS}:stormcaller_staff"}, frame="goal")
    adv_text("caller", "Stormcaller", "Craft a Stormcaller Staff and call down the sky")

    adv("blade", "root", f"{NS}:storm_blade", "blade", {"blade": f"{NS}:storm_blade"})
    adv_text("blade", "Arc of Thunder", "Craft a Storm Blade, a sword that chains lightning")

    adv("feather", "root", f"{NS}:gale_feather", "feather", {"feather": f"{NS}:gale_feather"})
    adv_text("feather", "Windwalker", "Craft a Gale Feather and launch yourself skyward")

    adv("anchor", "feather", f"{NS}:rift_anchor", "anchor", {"anchor": f"{NS}:rift_anchor"})
    adv_text("anchor", "Never Lost", "Craft a Rift Anchor and snap back to where you planted it")

    adv("apple", "root", f"{NS}:thunder_apple", "apple", {"apple": f"{NS}:thunder_apple"})
    adv_text("apple", "Taste of Thunder", "Craft a Thunder Apple")

    adv("glasshouse", "root", f"{NS}:storm_glass", "glasshouse", {"glass": f"{NS}:storm_glass"})
    adv_text("glasshouse", "Glow in the Dark", "Craft Stormglass")

    adv("tools", "forged", f"{NS}:tempest_pickaxe", "tools", {
        "pickaxe": f"{NS}:tempest_pickaxe", "axe": f"{NS}:tempest_axe",
        "shovel": f"{NS}:tempest_shovel", "hoe": f"{NS}:tempest_hoe"}, any_of=True)
    adv_text("tools", "Tempest Tools", "Craft any Tempest tool")

    adv("armor", "forged", f"{NS}:tempest_chestplate", "armor", {
        "helmet": f"{NS}:tempest_helmet", "chestplate": f"{NS}:tempest_chestplate",
        "leggings": f"{NS}:tempest_leggings", "boots": f"{NS}:tempest_boots"},
        frame="challenge", xp=100)
    adv_text("armor", "Full Tempest", "Own a full set of Tempest armor. Lightning can no longer hurt you")


# ====================================================================================
# Worldgen
# ====================================================================================


def gen_worldgen():
    w(f"data/{NS}/worldgen/feature/storm_geode.json", {
        "type": "minecraft:geode",
        "blocks": {
            "filling_provider": {"id": "minecraft:air"},
            "inner_layer_provider": {"id": f"{NS}:storm_block"},
            "alternate_inner_layer_provider": {"id": f"{NS}:storm_lamp"},
            "middle_layer_provider": {"id": f"{NS}:storm_bricks"},
            "outer_layer_provider": {"id": f"{NS}:stormstone"},
            "inner_placements": ["minecraft:lightning_rod"],
            "cannot_replace": "#minecraft:features_cannot_replace",
            "invalid_blocks": "#minecraft:geode_invalid_blocks",
        },
        "layers": {},
        "crack": {"generate_crack_chance": 0.8},
        "invalid_blocks_threshold": 1,
        "outer_wall_distance": {"type": "minecraft:uniform", "min_inclusive": 4, "max_inclusive": 6},
        "use_alternate_layer0_chance": 0.15,
    })
    w(f"data/{NS}/worldgen/placed_feature/storm_geode.json", {
        "feature": f"{NS}:storm_geode",
        "placement": [
            {"type": "minecraft:rarity_filter", "chance": 48},
            {"type": "minecraft:in_square"},
            {"type": "minecraft:height_range", "height": {
                "type": "minecraft:uniform",
                "min_inclusive": {"above_bottom": 8}, "max_inclusive": {"absolute": 20}}},
            {"type": "minecraft:biome"},
        ],
    })


# ====================================================================================
# Textures
# ====================================================================================
PAL = {
    "d": (30, 45, 110, 255), "c": (70, 110, 230, 255), "b": (70, 110, 230, 255),
    "w": (170, 220, 255, 255), "y": (255, 230, 90, 255), "o": (230, 170, 40, 255),
    "h": (110, 80, 50, 255), "k": (60, 45, 30, 255), "g": (80, 170, 60, 255),
}


def from_rows(rows):
    assert len(rows) == 16, len(rows)
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, r in enumerate(rows):
        assert len(r) == 16, (y, r, len(r))
        for x, ch in enumerate(r):
            if ch in PAL:
                im.putpixel((x, y), PAL[ch])
    return im


def block_noise(base, var, rng, size=16):
    im = Image.new("RGBA", (size, size))
    for y in range(size):
        for x in range(size):
            v = rng.randint(-var, var)
            im.putpixel((x, y), (base[0] + v, base[1] + v, base[2] + v, 255))
    return im


def clamp(c):
    return tuple(max(0, min(255, v)) for v in c)


def gen_block_textures():
    rng = random.Random(11)
    d = f"assets/{NS}/textures/block"

    st = block_noise((86, 98, 124), 9, rng)
    for _ in range(14):
        x, y = rng.randrange(16), rng.randrange(16)
        st.putpixel((x, y), (118, 150, 205, 255))
    save(st, f"{d}/stormstone.png")

    pol = block_noise((104, 116, 142), 3, rng)
    for i in range(16):
        for (x, y, c) in ((i, 0, (150, 165, 195)), (0, i, (150, 165, 195)), (i, 15, (70, 80, 104)), (15, i, (70, 80, 104))):
            pol.putpixel((x, y), c + (255,))
    save(pol, f"{d}/polished_stormstone.png")

    br = block_noise((98, 110, 136), 4, rng)
    mortar = (58, 66, 90, 255)
    for y in (3, 7, 11, 15):
        for x in range(16):
            br.putpixel((x, y), mortar)
    for row, xs in ((0, (7,)), (1, (3, 11)), (2, (7,)), (3, (3, 11))):
        for x in xs:
            for y in range(row * 4, row * 4 + 3):
                br.putpixel((x, y), mortar)
    save(br, f"{d}/storm_bricks.png")

    ch = pol.copy()
    for (x, y) in [(9, 3), (8, 4), (7, 5), (7, 6), (8, 7), (9, 7), (8, 8), (7, 9), (6, 10), (7, 11), (8, 12)]:
        ch.putpixel((x, y), (255, 230, 90, 255))
        ch.putpixel((x + 1, y), (170, 220, 255, 255))
    save(ch, f"{d}/chiseled_storm_bricks.png")

    side = block_noise((100, 112, 138), 3, rng)
    for y in range(16):
        for x in (0, 1, 14, 15):
            side.putpixel((x, y), (70, 80, 106, 255))
        for x in (5, 10):
            side.putpixel((x, y), (126, 140, 170, 255))
    save(side, f"{d}/storm_pillar.png")
    top = pol.copy()
    for y in range(4, 12):
        for x in range(4, 12):
            top.putpixel((x, y), (92, 104, 130, 255))
    save(top, f"{d}/storm_pillar_top.png")

    gl = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            edge = x in (0, 15) or y in (0, 15)
            if edge:
                gl.putpixel((x, y), (140, 195, 255, 255))
            else:
                a = 70 + (40 if (x + y) % 8 == 0 else 0)
                gl.putpixel((x, y), (190, 225, 255, a))
    for i in range(3, 7):
        gl.putpixel((i, i), (255, 255, 255, 190))
    save(gl, f"{d}/storm_glass.png")

    lamp = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            dist = math.hypot(x - 7.5, y - 7.5)
            v = max(0.0, 1.0 - dist / 11.0)
            c = (int(170 + 85 * v), int(205 + 50 * v), 255, 255)
            lamp.putpixel((x, y), c)
    for i in range(16):
        for (x, y) in ((i, 0), (i, 15), (0, i), (15, i), (i, 7), (7, i)):
            lamp.putpixel((x, y), (90, 130, 215, 255))
    for (x, y) in ((8, 8), (9, 8), (8, 9), (9, 9)):
        lamp.putpixel((x, y), (255, 240, 140, 255))
    save(lamp, f"{d}/storm_lamp.png")


HELMET = [
    "................", "................", "....dddddddd....", "...dccccccccd...",
    "..dccwwwwwwccd..", "..dcwccccccwcd..", "..dcc......ccd..", "..dcc......ccd..",
    "..dcc......ccd..", "..ddd......ddd..", "................", "................",
    "................", "................", "................", "................"]
CHEST = [
    "................", "..ddd......ddd..", ".dccd.dddd.dccd.", ".dcccddccddcccd.",
    ".dcccccccccccd..", "..dcccwyycccd...", "..dcccwyycccd...", "...dcccccccd....",
    "...dccwwwwcd....", "...dcccccccd....", "...dcccccccd....", "...ddddddddd....",
    "................", "................", "................", "................"]
LEGS = [
    "................", "...dddddddddd...", "...dccccccccd...", "...dccwwwwccd...",
    "...dccdddcccd...", "...dccd..dccd...", "...dccd..dccd...", "...dccd..dccd...",
    "...dccd..dccd...", "...dccd..dccd...", "...dddd..dddd...", "................",
    "................", "................", "................", "................"]
BOOTS = [
    "................", "................", "................", "................",
    "................", "................", "...dccd..dccd...", "...dccd..dccd...",
    "...dccd..dccd...", "..dcccd..dcccd..", ".dccccd..dccccd.", ".dddddd..dddddd.",
    "................", "................", "................", "................"]
INGOT = [
    "................", "................", "................", "................",
    "...dddddddddd...", "..dccccccccccd..", ".dcwwwwwwwwwwcd.", ".dcwyyyyyyyywcd.",
    ".dcwyyyyyyyywcd.", ".dcwwwwwwwwwwcd.", "..dccccccccccd..", "...dddddddddd...",
    "................", "................", "................", "................"]


def head_pixels(kind):
    """Pixel coordinates (x, y) of each tool head, anchored on a handle ending at (11, 5)."""
    if kind == "pickaxe":
        return [(7, 1), (8, 2), (9, 3), (10, 4), (11, 5), (12, 6), (13, 7), (14, 8),
                (8, 1), (9, 2), (10, 3), (11, 4), (12, 5), (13, 6), (14, 7)]
    if kind == "axe":
        return [(10, 3), (11, 3), (12, 4), (13, 5), (14, 6), (11, 4), (12, 5), (13, 6), (12, 6), (13, 7),
                (11, 5), (12, 7), (10, 4)]
    if kind == "shovel":
        pts = []
        for y in range(1, 7):
            for x in range(10, 15):
                if (x - 12) ** 2 / 4.0 + (y - 3.5) ** 2 / 7.0 <= 1.0:
                    pts.append((x, y))
        return pts
    if kind == "hoe":
        return [(10, 3), (11, 3), (12, 3), (13, 4), (12, 4), (11, 4), (13, 5), (12, 5), (11, 5)]
    raise ValueError(kind)


def tool_icon(kind):
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for i in range(10):
        x, y = 2 + i, 13 - i
        im.putpixel((x, y), PAL["h"])
        if x + 1 < 16:
            im.putpixel((x + 1, y), PAL["k"])
    pts = head_pixels(kind)
    for (x, y) in pts:
        im.putpixel((x, y), PAL["c"])
    for (x, y) in pts:
        if (x, y - 1) not in pts:
            im.putpixel((x, y), PAL["w"])
        elif (x, y + 1) not in pts or (x + 1, y) not in pts:
            im.putpixel((x, y), PAL["d"])
    return im


def apple_icon():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            dist = math.hypot(x - 7.5, y - 9.0)
            if dist <= 5.8:
                edge = dist > 4.6
                im.putpixel((x, y), (214, 156, 20, 255) if edge else (255, 218, 70, 255))
    for (x, y) in ((5, 7), (6, 6), (5, 8)):
        im.putpixel((x, y), (255, 250, 200, 255))
    for (x, y) in ((8, 6), (9, 7), (8, 8), (9, 9), (8, 10)):
        im.putpixel((x, y), (80, 150, 255, 255))
    for (x, y) in ((8, 3), (8, 4), (8, 2)):
        im.putpixel((x, y), PAL["k"])
    for (x, y) in ((9, 2), (10, 2), (10, 3)):
        im.putpixel((x, y), PAL["g"])
    return im


def vial_icon():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(6, 15):
        for x in range(16):
            dist = math.hypot(x - 7.5, y - 10.0)
            if dist <= 5.0:
                im.putpixel((x, y), (215, 235, 255, 255) if dist > 4.0 else (80, 170, 255, 255))
    for y in range(3, 7):
        for x in (6, 7, 8, 9):
            im.putpixel((x, y), (215, 235, 255, 255) if x in (6, 9) else (150, 200, 245, 255))
    for x in (6, 7, 8, 9):
        im.putpixel((x, 2), PAL["h"])
        im.putpixel((x, 3), PAL["k"]) if x in (6, 9) else None
    for (x, y) in ((7, 8), (8, 9), (7, 10), (8, 11)):
        im.putpixel((x, y), (255, 235, 100, 255))
    for (x, y) in ((5, 9), (5, 10)):
        im.putpixel((x, y), (255, 255, 255, 255))
    return im


def armor_texture(rng, leggings):
    im = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    for y in range(32):
        for x in range(64):
            v = rng.randint(-8, 8)
            base = (62 + v, 100 + v, 220 + v // 2)
            if x % 8 == 0 or y % 8 == 0:
                base = (36, 56, 130)
            elif (x + y) % 11 == 0:
                base = (120, 180, 255)
            im.putpixel((x, y), clamp(base) + (255,))
    # lightning accents
    for cx, cy in ((12, 6), (44, 6), (20, 22), (32, 22), (50, 22)):
        for i, (dx, dy) in enumerate(((1, 0), (0, 1), (-1, 2), (0, 3), (1, 4))):
            x, y = cx + dx, cy + dy
            if 0 <= x < 64 and 0 <= y < 32:
                im.putpixel((x, y), (255, 230, 90, 255))
    if leggings:
        for x in range(16, 40):
            im.putpixel((x, 16), (255, 230, 90, 255))
    return im


def effect_icon():
    im = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
    for y in range(18):
        for x in range(18):
            dist = math.hypot(x - 8.5, y - 8.5)
            if dist <= 8.0:
                im.putpixel((x, y), (28, 44, 110, 255) if dist > 6.5 else (50, 90, 200, 255))
    bolt = [(10, 3), (9, 4), (8, 5), (8, 6), (9, 7), (10, 7), (9, 8), (8, 9), (7, 10), (8, 11), (9, 12), (8, 13), (7, 14)]
    for (x, y) in bolt:
        im.putpixel((x, y), (255, 235, 100, 255))
        im.putpixel((x + 1, y), (200, 240, 255, 255))
    return im


def gen_item_textures():
    d = f"assets/{NS}/textures/item"
    save(from_rows(INGOT), f"{d}/tempest_ingot.png")
    save(from_rows(HELMET), f"{d}/tempest_helmet.png")
    save(from_rows(CHEST), f"{d}/tempest_chestplate.png")
    save(from_rows(LEGS), f"{d}/tempest_leggings.png")
    save(from_rows(BOOTS), f"{d}/tempest_boots.png")
    for kind in ("pickaxe", "axe", "shovel", "hoe"):
        save(tool_icon(kind), f"{d}/tempest_{kind}.png")
    save(apple_icon(), f"{d}/thunder_apple.png")
    save(vial_icon(), f"{d}/storm_vial.png")
    rng = random.Random(5)
    save(armor_texture(rng, False), f"assets/{NS}/textures/entity/equipment/humanoid/tempest.png")
    save(armor_texture(rng, True), f"assets/{NS}/textures/entity/equipment/humanoid_leggings/tempest.png")
    save(effect_icon(), f"assets/{NS}/textures/mob_effect/static_charge.png")


# ====================================================================================
def gen_lang():
    path = os.path.join(RES, f"assets/{NS}/lang/en_us.json")
    lang = json.load(open(path)) if os.path.exists(path) else {}
    lang.update(LANG)
    lang.update(ADV_LANG)
    w(f"assets/{NS}/lang/en_us.json", dict(sorted(lang.items())))


def main():
    gen_items()
    gen_blocks()
    gen_equipment()
    gen_loot()
    gen_tags()
    gen_recipes()
    gen_advancements()
    gen_worldgen()
    gen_block_textures()
    gen_item_textures()
    gen_lang()
    print("generated")


if __name__ == "__main__":
    main()
