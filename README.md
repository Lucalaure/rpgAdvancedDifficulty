# RPG Advanced Difficulty

A Fabric mod for Minecraft 26.3 that combines two mods. (The 1.21.1 version is available as release v1.0.0.)

- **RpgDifficulty** (Globox_Z, MIT): mobs get stronger with distance from spawn, world time and height.
- **Champions** (Crystal, GPLv3): any hostile mob can spawn as a *champion*, an elite with a tier (1–5), boosted stats, random affixes (Molten, Shielding, Magnetic, …), a boss bar and extra loot.

## How they work together

Every mob spawn goes through a single pipeline:

1. **Difficulty scaling.** RpgDifficulty computes a factor from distance, time and height, or from a difficulty zone or dimension datapack. It uses that factor to scale health, damage and armor, then applies the random-value and big/speed zombie variants.
2. **Champion roll.** The mob then rolls for a champion tier. With `championsScaleWithDifficulty` on, the difficulty factor also raises champion odds, and higher tiers gain the most:

   `tier weight × min(maxChampionChanceMultiplier, 1 + (factor − 1) × championChanceScaling × tier)`

   With the defaults, about 6% of eligible spawns are champions near spawn. Where the factor reaches 3.0, about 19% are, and tier 4–5 champions are up to 8× as common.
3. **Champion stats.** The tier's health and strength multipliers apply on top of the already-scaled stats. Arrows and other projectiles from champions also deal the champion strength bonus.

A mob is processed only once. A flag saved on the mob (`RpgDifficultyApplied`) stops scaling from being applied twice.

## Changes from the original mods

- Champions' separate spawn hooks were removed (the `initialize`, Wither/Dragon constructor and slime-split hooks). Champions are now rolled in the shared pipeline above. The original mods conflicted here: RpgDifficulty's "already strengthened" check skipped difficulty scaling for every champion.
- `max_boss_tier` in `champions_common` is now respected. The original mod ignored it. With the default of `0`, the Wither and Ender Dragon never become champions.
- Big zombies now get their bonus HP and damage. The old heuristic check silently skipped them.
- Creeper explosion power, after champion tier and difficulty scaling, is capped by `maxCreeperExplosionPower` (default 12; a vanilla creeper is 3).

## Configuration

| File | What it controls |
| --- | --- |
| `config/rpgdifficulty.json` | Distance/time/height scaling, zombie variants, bosses, and the champion scaling options (*Champions* category) |
| `config/Champions/champions_common.properties` | Tier weights, affix counts, per-tier health/strength growth, `max_boss_tier` |
| `config/Champions/champions_affixes.properties` | Enable or disable individual affixes |
| `config/Champions/champions_client.properties` | HUD colors and offsets |

With Mod Menu installed, the config button opens a hub that links to both config screens.

### Per-dimension difficulty (datapacks)

To give a dimension its own scaling settings, add a JSON file at `data/<namespace>/difficulty/<name>.json` in a datapack. Any of these settings can be overridden: `distanceCoordinatesX`, `distanceCoordinatesZ`, `increasingDistance`, `distanceFactor`, `increasingTime`, `timeFactor`, `heightDistance`, `heightFactor`, `maxFactorHealth`, `maxFactorDamage`, `maxFactorProtection`, `maxFactorSpeed`, `startingFactor`, `startingDistance`, `startingTime`, `startingHeight`, `positiveHeightIncreasion`, `negativeHeightIncreasion`.

```json
{
    "dimension": "minecraft:the_nether",
    "increasingDistance": 300,
    "distanceFactor": 0.1,
    "increasingTime": 60,
    "timeFactor": 0.05,
    "heightDistance": 30,
    "heightFactor": 0.1,
    "maxFactorHealth": 3.0,
    "maxFactorDamage": 3.0,
    "maxFactorProtection": 1.5,
    "maxFactorSpeed": 2.0,
    "startingFactor": 1.0,
    "startingDistance": 0,
    "startingTime": 0,
    "startingHeight": 62,
    "positiveHeightIncreasion": true,
    "negativeHeightIncreasion": true
}
```

Mobs in the `c:bosses` entity tag use the boss settings from `rpgdifficulty.json`. On multiplayer servers, setting `timeFactor` to 0 is recommended.

### Difficulty zones (commands)

A zone gives every mob inside it a fixed difficulty factor, replacing distance, time and height scaling:

```
/rpgdifficulty zone create box ~ ~ ~ ~10 ~10 ~10 2.5
/rpgdifficulty zone create box ~ ~ ~ ~10 ~10 ~10 2.5 ZoneName
/rpgdifficulty zone create sphere 100 64 200 30 1.8
/rpgdifficulty zone remove <uuid>
/rpgdifficulty zone remove here
/rpgdifficulty zone list
```

## Building

Requires JDK 25 or newer.

```bash
./gradlew build
```

The jar is written to `build/libs/`. At runtime it needs Fabric API and Cloth Config; Mod Menu is optional. The mod declares `provides: ["rpgdifficulty", "champions"]`, so Fabric refuses to load it alongside the original mods.

## License

This mod includes GPLv3 code from Champions, so the combined mod is distributed under **GPL-3.0-only**. RpgDifficulty's original MIT notice is kept in `LICENSE-RpgDifficulty-MIT`. The full GPLv3 text is in `LICENSE`.

Original mods:
- RpgDifficulty by Globox_Z: https://github.com/Globox1997/RpgDifficulty
- Champions by Crystal: https://github.com/crystalx375/Champions
