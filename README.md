# RPG Advanced Difficulty

A Fabric mod for Minecraft 26.3 that combines two mods. (The 1.21.1 version is available as release v1.0.0.)

- **RpgDifficulty** (Globox_Z, MIT): mobs get stronger as the world ages, and optionally with distance from spawn.
- **Champions** (Crystal, GPLv3): any hostile mob can spawn as a *champion*, an elite with a tier (1–5), boosted stats, random affixes (Molten, Shielding, Sniper, …), a boss bar and extra loot.

## How they work together

Every mob spawn goes through a single pipeline:

1. **Difficulty scaling.** A difficulty factor is computed from world time (by default +10% for every hour the world has been running, up to 3×), plus distance from spawn if `enableDistanceScaling` is on. A difficulty zone or dimension datapack can override it. The factor scales health, damage and armor.
2. **Champion roll.** The mob then rolls for a champion tier. With `championsScaleWithDifficulty` on, the difficulty factor also raises champion odds, and higher tiers gain the most:

   `tier weight × min(maxChampionChanceMultiplier, 1 + (factor − 1) × championChanceScaling × tier)`

   With the defaults, about 6% of eligible spawns are champions near spawn. Once the factor reaches 3.0 (about 20 hours of world time by default), about 19% are, and tier 4–5 champions are up to 8× as common.
3. **Champion stats and affixes.** The tier's health and strength multipliers apply on top of the already-scaled stats. The champion then gets its random affixes (see below), which may include mob-specific ones such as Sniper for skeletons or Webslinger for spiders. Arrows and other projectiles from champions also deal the champion strength bonus.

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
| Big | 1.3× size, +10 health, +2 damage, 30% slower |
| Blinded | Its hits can blind the target for a few seconds |
| Dampening | Takes half damage from indirect attacks (projectiles, explosions, potions) |
| Desecrating | Periodically spawns a cloud of harming under its target |
| Hasty | Much faster movement |
| Infested | Spawns silverfish every so often while fighting |
| Knocking | Extra knockback, and slows the target briefly |
| Lively | Regenerates 1 HP/sec, or 4 HP/sec when it has no target |
| Molten | Fires homing projectiles that burn, and is fire-resistant |
| Paralyzing | Small chance per hit to root the target in place |
| Plagued | Poisons nearby creatures |
| Reflection | Hurts and pushes back anyone who damages it |
| Shielding | Periodically becomes immune to all damage |

### Mob-specific affixes

These only roll on the listed mobs.

| Affix | Mobs | Effect |
| --- | --- | --- |
| Horde Caller | Zombies* | The first time it targets a player, 2–3 more zombies of its kind join the fight |
| Sunproof | Zombies*, skeletons† | Doesn't burn in daylight |
| Sniper | Skeletons† | Fires every 3 s instead of every 1–2 s, but arrows are 50% faster, perfectly aimed and deal +50% damage |
| Volley | Skeletons† | Every 3 s fires a spread of 3 arrows |
| Frost Archer | Strays | Its arrows freeze the target like powder snow |
| Stalker | Creepers | Invisible (effect particles still show) until it starts to hiss |
| Webslinger | Spiders‡ | Every 4 s throws a cobweb that traps the target where it lands; webs vanish after 5 s |
| Brood Mother | Spiders (not cave spiders) | Spawns 2 cave spiders when hurt (max 6 nearby) |
| Pouncer | Spiders‡ | Leaps at targets 4–12 blocks away |
| Blink | Endermen | Teleports behind its attacker after being hit |
| Thief | Endermen | 25% chance per hit to knock the item out of your hand |
| Alchemist | Witches | Every 5 s also throws a potion of Weakness, Mining Fatigue or Levitation |
| Coven | Witches | Every 2 s heals hostile mobs within 8 blocks |
| Inferno | Blazes, ghasts | Fireballs leave a 3×3 patch of fire (needs the `mob_griefing` gamerule) |
| Barrage | Blazes, ghasts | Every 4 s an extra burst: 5 small fireballs (blaze) or 2 large ones (ghast) |
| Splitter | Slimes, magma cubes | Splits into 2 extra pieces; each piece is a tier 1 champion with one of its affixes |
| Sticky | Slimes, magma cubes | Its hits give Slowness IV for 3 s |
| Warlord | Illagers | Other illagers within 16 blocks get Strength |
| Berserker | Vindicators | Attacks faster as its health drops (up to ~3× as often) |

\* Zombie, husk, drowned, zombie villager and zombified piglin. † Skeleton, stray, bogged and wither skeleton (anything that uses the skeleton bow logic). ‡ Includes cave spiders.

Minions summoned by affixes (Horde Caller zombies, Brood Mother cave spiders, Infested silverfish) never become champions themselves.

Every affix can be switched off in `champions_affixes.properties` or the Champions config screen. Big's stats are set there too.

## Testing commands

Operators (permission level 2) can spawn champions to try affixes out:

| Command | What it does |
| --- | --- |
| `/champion list` | Lists every enabled affix and which mobs can have it |
| `/champion demo <affix>` | Spawns a fitting mob with only that affix, 3 blocks in front of you |
| `/champion spawn <mob> <affix> [affix...]` | Spawns any mob with any combination of affixes (space or comma separated). The tier is the number of affixes, up to 5 |

For example, `/champion demo webslinger` or `/champion spawn minecraft:skeleton sniper volley sunproof`. Affix names autocomplete, and the command refuses affixes the mob can't have.

## Bestiary

Players learn what each affix does through the **bestiary**, a book opened from the button to the right of the recipe book in the survival inventory.

- **Discovery:** when a player gets within 15 blocks of a champion (the range where its health bar and affixes appear), each of its affixes is added to that player's bestiary, with a "Bestiary updated" message above the hotbar.
- **Saved per player:** discoveries are stored on the player (per world), kept on death, and synced to their client.
- **Pages:** an intro with discovery progress, the champion tiers (read from the current config), then one page per affix with its name, which mobs can have it, and what it does. Undiscovered affixes show as "???".

Affix descriptions are the `affix.<name>.desc` keys in `assets/champions/lang/en_us.json`.

## Adding a mob-specific affix

1. Create a class in `src/main/java/crystal/champions/affix/` that extends `MobSpecificAffix`, passing its name, bestiary label, demo mob and which mobs can roll it:
   ```java
   super("sniper", "skeletons", EntityTypes.SKELETON, mob -> mob instanceof AbstractSkeleton);
   ```
2. Override the hooks it needs (see `Affix`):
   - `onApply(mob)`: once when the champion is created (permanent stat changes)
   - `onTick(entity)` / `onAttack(entity, mob)`: every tick (`mob.getTarget()` is its target)
   - `onHurt(champion, target)`: its melee or slime-contact hit landed
   - `onDamaged(champion, source, amount)`: it took a hit
   - `onProjectileSpawn(owner, projectile)` / `onProjectileHit(owner, projectile, hit)`: its arrows, fireballs, potions, etc.
3. Optionally return a name from `getExclusiveGroup()` so opposing affixes can't roll together.
4. Add `Map.entry("<name>", YourAffix::new)` to `FACTORY_LIST` in `AffixRegistry`. The config toggle, the config screen entry, the bestiary page and `/champion` support come from that list automatically.
5. Add `"affix.<name>"` (shown above the health bar) and `"affix.<name>.desc"` (bestiary text) to `assets/champions/lang/en_us.json`, plus `"champions.bestiary.mobs.<label>"` if you used a new label.
6. Add a check for it to `src/gametest/.../AffixClientGameTest.java`.

## Changes from the original mods

- Champions' separate spawn hooks were removed (the `initialize`, Wither/Dragon constructor and slime-split hooks). Champions are now rolled in the shared pipeline above. The original mods conflicted here: RpgDifficulty's "already strengthened" check skipped difficulty scaling for every champion.
- `max_boss_tier` in `champions_common` is now respected. The original mod ignored it. With the default of `0`, the Wither and Ender Dragon never become champions.
- RpgDifficulty's big zombie variant is now the *Big* champion affix, which works on any mob (it uses the scale attribute). (In the original mod, big zombies also never got their bonus stats because of the same "already strengthened" check.)
- Creeper explosion power, after champion tier and difficulty scaling, is capped by `maxCreeperExplosionPower` (default 12; a vanilla creeper is 3).

## Configuration

| File | What it controls |
| --- | --- |
| `config/rpgdifficulty.json` | Time and (optional) distance scaling, bosses, and the champion scaling options (*Champions* category) |
| `config/Champions/champions_common.properties` | Tier weights, affix counts, per-tier health/strength growth, `max_boss_tier` |
| `config/Champions/champions_affixes.properties` | Enable or disable individual affixes, and each affix's settings (including Big's stats) |
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

The jar is written to `build/libs/`.

`./gradlew runClientGameTest` runs the automated client tests in `src/gametest` (the game window opens for about a minute):

- `AffixClientGameTest` spawns a champion with each affix next to the player and checks its real effect in the world, plus the `/champion` commands. Results are logged as `AFFIX TEST: <affix> OK`.
- `BestiaryClientGameTest` saves screenshots of the champion HUD, the inventory bestiary button and the bestiary pages to `build/run/clientGameTest/screenshots/`. At runtime it needs Fabric API and Cloth Config; Mod Menu is optional. The mod declares `provides: ["rpgdifficulty", "champions"]`, so Fabric refuses to load it alongside the original mods.

## License

This mod includes GPLv3 code from Champions, so the combined mod is distributed under **GPL-3.0-only**. RpgDifficulty's original MIT notice is kept in `LICENSE-RpgDifficulty-MIT`. The full GPLv3 text is in `LICENSE`.

Original mods:
- RpgDifficulty by Globox_Z: https://github.com/Globox1997/RpgDifficulty
- Champions by Crystal: https://github.com/crystalx375/Champions
