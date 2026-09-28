# RPG Advanced Difficulty

A Fabric mod for Minecraft 26.3 that combines two mods. (The 1.21.1 version is available as release v1.0.0.)

- **RpgDifficulty** (Globox_Z, MIT): mobs get stronger as the world ages, and optionally with distance from spawn.
- **Champions** (Crystal, GPLv3): any hostile mob can spawn as a *champion*, an elite with a tier (1–5), boosted stats, random affixes (Molten, Shielding, Magnetic, …), a boss bar and extra loot.

## How they work together

Every mob spawn goes through a single pipeline:

1. **Difficulty scaling.** A difficulty factor is computed from world time (by default +10% for every hour the world has been running, up to 3×), plus distance from spawn if `enableDistanceScaling` is on. A difficulty zone or dimension datapack can override it. The factor scales health, damage and armor.
2. **Champion roll.** The mob then rolls for a champion tier. With `championsScaleWithDifficulty` on, the difficulty factor also raises champion odds, and higher tiers gain the most:

   `tier weight × min(maxChampionChanceMultiplier, 1 + (factor − 1) × championChanceScaling × tier)`

   With the defaults, about 6% of eligible spawns are champions near spawn. Once the factor reaches 3.0 (about 20 hours of world time by default), about 19% are, and tier 4–5 champions are up to 8× as common.
3. **Champion stats and affixes.** The tier's health and strength multipliers apply on top of the already-scaled stats. The champion then gets its random affixes (see below), which may include mob-specific ones such as the zombie-only *Big* and *Speedy*. Arrows and other projectiles from champions also deal the champion strength bonus.

A mob is processed only once. A flag saved on the mob (`RpgDifficultyApplied`) stops scaling from being applied twice.

## Champion tiers

| Tier | Base chance | Health | Damage | Affixes |
| --- | --- | --- | --- | --- |
| 1 | 4.0% | ×1.5 | ×1.5 | 1 |
| 2 | 1.5% | ×2.5 | ×1.8 | 2 |
| 3 | 0.3% | ×4 | ×2.2 | 3 |
| 4 | 0.06% | ×7 | ×3.5 | 4 |
| 5 | 0.02% | ×12 | ×5 | 8 |

Champion creepers also explode bigger (radius × tier, capped by `maxCreeperExplosionPower`), and tier 4–5 creepers have a longer fuse.

## Affixes

Each champion rolls its tier's number of affixes at random from the affixes it is allowed to have.

### General affixes (any champion)

| Affix | Effect |
| --- | --- |
| Adaptive | Takes less and less damage from the same damage type in a row |
| Arctic | Fires homing projectiles that slow its target |
| Blinded | Its hits can blind the target for a few seconds |
| Dampening | Takes less damage from indirect attacks |
| Desecrating | Periodically spawns a cloud of harming under its target |
| Hasty | Much faster movement |
| Infested | Spawns silverfish when it attacks or is hit |
| Knocking | Extra knockback, and slows the target briefly |
| Lively | Regenerates 1 HP/sec, or 4 HP/sec when it has no target |
| Magnetic | Periodically pulls its target toward itself |
| Molten | Fires homing projectiles that burn, and is fire-resistant |
| Paralyzing | Small chance per hit to root the target in place |
| Plagued | Poisons nearby creatures |
| Reflection | Reflects part of the damage it takes back at the attacker |
| Shielding | Periodically becomes immune to all damage |

### Mob-specific affixes

These only roll on the listed mobs. Affixes in the same *group* can't roll together on one champion.

| Affix | Mobs | Group | Effect |
| --- | --- | --- | --- |
| Big | Zombies* | `zombie_build` | 1.3× size, +10 health, +2 damage, 30% slower |
| Speedy | Zombies* | `zombie_build` | 30% faster, −10 health |

\* Zombie, husk, drowned, zombie villager and zombified piglin (adults only).

Their stats are set in `champions_affixes.properties`, and both can be switched off there or in the Champions config screen.

### Adding a mob-specific affix

1. Create a class in `src/main/java/crystal/champions/affix/` that extends `Affix` (or `ZombieVariantAffix` for zombies).
2. Override `canApplyTo(Mob mob)` to limit it to your mob, for example `return mob instanceof Skeleton;`.
3. Put permanent stat changes in `onApply(Mob mob)`, which runs once when the champion is created. Put ongoing behavior in `onTick`, `onAttack` or `onHurt`.
4. Optionally return a name from `getExclusiveGroup()` so opposing variants can't roll together.
5. Add it to `FACTORY_LIST` in `AffixRegistry`, add a matching `rN` toggle (the next number) and default to `ChampionsConfigAffixes`, and bump that config's `VERSION`.
6. Add `"affix.<name>"` to `assets/champions/lang/en_us.json`. That is the name shown above the champion's health bar.

## Changes from the original mods

- Champions' separate spawn hooks were removed (the `initialize`, Wither/Dragon constructor and slime-split hooks). Champions are now rolled in the shared pipeline above. The original mods conflicted here: RpgDifficulty's "already strengthened" check skipped difficulty scaling for every champion.
- `max_boss_tier` in `champions_common` is now respected. The original mod ignored it. With the default of `0`, the Wither and Ender Dragon never become champions.
- RpgDifficulty's big/speed zombie variants are now the zombie-only champion affixes *Big* and *Speedy*. (In the original mod, big zombies also never got their bonus stats because of the same "already strengthened" check.)
- Creeper explosion power, after champion tier and difficulty scaling, is capped by `maxCreeperExplosionPower` (default 12; a vanilla creeper is 3).

## Configuration

| File | What it controls |
| --- | --- |
| `config/rpgdifficulty.json` | Time and (optional) distance scaling, bosses, and the champion scaling options (*Champions* category) |
| `config/Champions/champions_common.properties` | Tier weights, affix counts, per-tier health/strength growth, `max_boss_tier` |
| `config/Champions/champions_affixes.properties` | Enable or disable individual affixes, and each affix's settings (including the Big/Speedy zombie stats) |
| `config/Champions/champions_client.properties` | HUD colors and offsets |

With Mod Menu installed, the config button opens a hub that links to both config screens.

### Per-dimension difficulty (datapacks)

To give a dimension its own scaling settings, add a JSON file at `data/<namespace>/difficulty/<name>.json` in a datapack. Any of these settings can be overridden: `distanceCoordinatesX`, `distanceCoordinatesZ`, `increasingDistance`, `distanceFactor`, `increasingTime`, `timeFactor`, `maxFactorHealth`, `maxFactorDamage`, `maxFactorProtection`, `startingFactor`, `startingDistance`, `startingTime`. Distance settings only apply when `enableDistanceScaling` is on.

```json
{
    "dimension": "minecraft:the_nether",
    "increasingDistance": 300,
    "distanceFactor": 0.1,
    "increasingTime": 60,
    "timeFactor": 0.05,
    "maxFactorHealth": 3.0,
    "maxFactorDamage": 3.0,
    "maxFactorProtection": 1.5,
    "startingFactor": 1.0,
    "startingDistance": 0,
    "startingTime": 0
}
```

Mobs in the `c:bosses` entity tag use the boss settings from `rpgdifficulty.json`. World time keeps counting while the server runs with nobody online, so on multiplayer servers consider a lower `timeFactor`, or enable distance scaling instead.

### Difficulty zones (commands)

A zone gives every mob inside it a fixed difficulty factor, replacing time and distance scaling:

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
