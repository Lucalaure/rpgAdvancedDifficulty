# RPG Advanced Difficulty

A Fabric mod for Minecraft 26.3 that combines two mods. (The 1.21.1 version is available as release v1.0.0.)

- **RpgDifficulty** (Globox_Z, MIT): mobs get stronger as the world ages, and optionally with distance from spawn.
- **Champions** (Crystal, GPLv3): any hostile mob can spawn as a *champion*, an elite with a tier (1–5), boosted stats, random affixes (Molten, Shielding, Sniper, …), a boss bar and extra loot.

## How they work together

Every mob spawn goes through a single pipeline:

1. **Difficulty scaling.** A difficulty factor is computed from world time (by default +10% for every hour the world has been running, up to 3×, on Normal; see Game difficulty), plus distance from spawn if `enableDistanceScaling` is on. A difficulty zone or dimension datapack can override it. The factor scales health, damage and armor.
2. **Champion roll.** The mob then rolls for a champion tier. With `championsScaleWithDifficulty` on, the difficulty factor also raises champion odds, and higher tiers gain the most:

   `tier weight × min(maxChampionChanceMultiplier, 1 + (factor − 1) × championChanceScaling × tier)`

   With the defaults on Normal, about 5.5% of hostile spawns are champions in a new world. Once the factor reaches 3.0 (after 20 hours of world time), about 21% are, and tier 3–5 champions are up to 12× as common (the `maxChampionChanceMultiplier` cap).
3. **Champion stats and affixes.** The tier's health and strength multipliers apply on top of the already-scaled stats. The champion then gets its random affixes (see below), which may include mob-specific ones such as Sniper for skeletons or Webslinger for spiders. Arrows and other projectiles from champions also deal the champion strength bonus.

A mob is processed only once. A flag saved on the mob (`RpgDifficultyApplied`) stops scaling from being applied twice.

## Game difficulty

The game difficulty (Easy, Normal, Hard) changes both systems. Peaceful uses the Easy values.

| | Easy | Normal | Hard |
| --- | --- | --- | --- |
| Growth speed | ×0.5 | ×1 | ×1.5 |
| Max strength (health/damage cap) | ×0.75 (2.25×) | ×1 (3×) | ×1.5 (4.5×) |
| Champion chance (all tiers) | ×0.5 | ×1 | ×1.5 |
| Extra per tier above 1 | ×0.8 each | ×1 | ×1.25 each |

On Hard, mobs top out at 4.5× health and damage (reached after about 23 hours of world time), and tier 5 champions are about 3.7× as common as on Normal (tier 1: 1.5×). On Easy, tier 5 champions are about 0.2× as common. All of these are in the *Game Difficulty* section of the difficulty config. Vanilla's own difficulty effects (like mobs hitting players harder on Hard) still apply on top.

## Champion tiers

| Tier | Base chance (Normal) | Health | Damage | Affix slots |
| --- | --- | --- | --- | --- |
| 1 | 2.8% | ×1.5 | ×1.5 | 1 |
| 2 | 1.5% | ×2.5 | ×1.8 | 2 |
| 3 | 0.8% | ×4 | ×2.2 | 3 |
| 4 | 0.3% | ×7 | ×3.5 | 4 |
| 5 | 0.1% | ×12 | ×5 | 8 |

Base chances are for a new world on Normal (tier weights 9450 / 280 / 150 / 80 / 30 / 10 in `champions_common.properties`). They rise over time and with game difficulty:

| | Any champion | Tier 1 | Tier 2 | Tier 3 | Tier 4 | Tier 5 |
| --- | --- | --- | --- | --- | --- | --- |
| Easy, new world | 2.4% | 1.4% | 0.6% | 0.3% | 0.08% | 0.02% |
| Easy, max (25 h) | 7.0% | 3.1% | 2.1% | 1.2% | 0.5% | 0.1% |
| Normal, new world | 5.5% | 2.8% | 1.5% | 0.8% | 0.3% | 0.1% |
| Normal, 10 h | 14.0% | 5.1% | 4.1% | 2.9% | 1.4% | 0.5% |
| Normal, max (20 h) | 21.1% | 7.0% | 6.3% | 4.7% | 2.3% | 0.9% |
| Hard, new world | 9.7% | 4.0% | 2.7% | 1.8% | 0.8% | 0.3% |
| Hard, 10 h | 30.4% | 7.7% | 8.3% | 7.6% | 4.5% | 2.3% |
| Hard, max (23 h) | 44.9% | 10.9% | 12.9% | 12.4% | 6.1% | 2.6% |

Champion creepers also explode bigger (radius × tier, capped by `maxCreeperExplosionPower`), and tier 4–5 creepers have a longer fuse.

## Champion loot

Champions drop their normal loot plus extra rolls from their tier's pool. Enchanted books are only one possible roll, and their strength scales with the tier: they're enchanted like an enchanting table at the listed levels, and only tier 5 books can have treasure enchantments such as Mending.

| Tier | Rolls | Pool (weight) | Books |
| --- | --- | --- | --- |
| 1 | 1 | Iron ingots 1–3 (20), gold ingots 1–3 (15), XP bottles 1–3 (15), emeralds 1–2 (10) | Weight 7 (about 1 in 10), levels 5–10 |
| 2 | 2 | Iron 2–5 (15), gold 2–5 (12), XP bottles 2–5 (15), emeralds 2–4 (12), lapis 4–10 (8), golden apple (5), diamond (4) | Weight 10, levels 10–18 |
| 3 | 2 | XP bottles 4–8 (15), emeralds 3–6 (12), diamonds 1–2 (10), golden apples 1–2 (8), ender pearls 1–3 (8), random enchanted iron gear (6, levels 15–25) | Weight 20, levels 18–25 |
| 4 | 3 | Diamonds 2–4 (12), emeralds 5–10 (10), XP bottles 6–12 (12), 2 golden apples (8), netherite scrap (5), totem of undying (3), random enchanted diamond gear (8, levels 20–30), random armor trim template (5) | Weight 24, levels 25–30 |
| 5 | — | Wither skeleton skull or nether star; 1–2 armor trim templates; 3 rolls of diamonds 3–6 (12), netherite scrap 1–3 (8), golden apples 2–4 (8), totem (5), enchanted golden apple (2) | Always 2, level 30, treasure possible |

*Random gear* is one of: sword, axe, pickaxe, shovel, helmet, chestplate, leggings, boots (iron at tier 3, diamond at tier 4), bow or crossbow.

The loot tables are built in `ChampionsLootTable.java`; run `./gradlew runDatagen` after changing it to regenerate the JSON in `src/main/generated`. Split slime pieces never drop champion loot. The bestiary has a *Champion loot* page summarising this.

## Affixes

Each champion rolls its tier's number of affixes at random from the affixes it is allowed to have.

**Affix slots:** each champion tier has a slot budget (tier 1: 1, tier 2: 2, tier 3: 3, tier 4: 4, tier 5: 8), and each affix takes up 1–4 slots (the *Slots* column). Affixes are picked until the slots are full, so a tier 2 champion gets either two 1-slot affixes or one 2-slot affix, and a 3-slot affix can only appear on tier 3 or higher. Bigger affixes are picked more often when they fit: each affix's weight is `1 + 0.25 × (slots − 1)`, so 2-, 3- and 4-slot affixes are 1.25×, 1.5× and 1.75× as likely as a 1-slot one. Slot costs (`<affix>_slots`) and the weight bonus (`affix_slot_weight_bonus`) are in `champions_affixes.properties`, and the tier budgets (`tierN_affix_slots`) in `champions_common.properties`; both are also in the Champions config screen.

### General affixes (any champion)

| Affix | Slots | Effect |
| --- | --- | --- |
| Adaptive | 2 | Takes less and less damage from the same damage type in a row |
| Arctic | 3 | Fires homing projectiles that slow its target |
| Big | 1 | 1.3× size, +10 health, +2 damage, 30% slower |
| Blinded | 1 | Its hits can blind the target for a few seconds |
| Dampening | 1 | Takes half damage from indirect attacks (projectiles, explosions, potions) |
| Desecrating | 3 | Periodically spawns a cloud of harming under its target |
| Hasty | 1 | Much faster movement |
| Infested | 2 | Spawns silverfish every so often while fighting |
| Knocking | 1 | Extra knockback, and slows the target briefly |
| Lively | 1 | Regenerates 1 HP/sec, or 4 HP/sec when it has no target |
| Molten | 3 | Fires homing projectiles that burn, and is fire-resistant |
| Paralyzing | 3 | Small chance per hit to root the target in place |
| Plagued | 2 | Poisons nearby creatures, and is immune to poison itself |
| Reflection | 3 | Hurts and pushes back anyone who damages it |
| Shielding | 4 | Periodically becomes immune to all damage |
| Stormcaller | 4 | Every 6 s calls 1–3 lightning bolts around its target; immune to lightning itself |
| Undying | 4 | The first time it would die, revives at 50% health like a totem, with 2 s of invulnerability |
| Vampiric | 3 | Heals for 50% of the damage it deals (melee and projectiles) |
| Enraged | 3 | Below 40% health, gets Strength and Speed and gives off red particles |
| Withering | 3 | Its hits and projectiles apply Wither II for 4 s |
| Volatile | 3 | Explodes 1.5 s after dying (smoke and hiss warning); doesn't break blocks |

### Mob-specific affixes

These only roll on the listed mobs.

| Affix | Slots | Mobs | Effect |
| --- | --- | --- | --- |
| Horde Caller | 2 | Zombies* | The first time it targets a player, 2–3 more zombies of its kind join the fight |
| Sunproof | 1 | Zombies*, skeletons† | Doesn't burn in daylight |
| Sniper | 2 | Skeletons† | Fires every 3 s instead of every 1–2 s, but arrows are 50% faster, perfectly aimed and deal +50% damage |
| Volley | 2 | Skeletons† | Every 3 s fires a spread of 3 arrows |
| Frost Archer | 2 | Strays | Its arrows freeze the target like powder snow |
| Stalker | 3 | Creepers | Invisible (effect particles still show) until it starts to hiss |
| Webslinger | 2 | Spiders‡ | Every 4 s throws a cobweb that traps the target where it lands; webs vanish after 5 s |
| Brood Mother | 3 | Spiders (not cave spiders) | Spawns 2 cave spiders when hurt (max 6 nearby) |
| Pouncer | 1 | Spiders‡ | Leaps at targets 4–12 blocks away |
| Blink | 2 | Endermen | Teleports behind its attacker after being hit |
| Thief | 2 | Endermen | 25% chance per hit to knock the item out of your hand |
| Alchemist | 2 | Witches | Every 5 s also throws a potion of Weakness, Mining Fatigue or Levitation |
| Coven | 2 | Witches | Arrives with 1–3 extra monsters (zombie/skeleton/spider), and every 2 s heals hostile mobs within 8 blocks |
| Inferno | 2 | Blazes, ghasts | Fireballs leave a 3×3 patch of fire (needs the `mob_griefing` gamerule) |
| Barrage | 3 | Blazes, ghasts | Every 4 s an extra burst: 5 small fireballs (blaze) or 2 large ones (ghast) |
| Splitter | 2 | Slimes, magma cubes | Splits into 2 extra pieces; each piece has a 50% chance to keep one of its other affixes (never Splitter). Pieces never drop champion loot |
| Sticky | 1 | Slimes, magma cubes | Its hits give Slowness IV for 3 s |
| Warlord | 3 | Illagers | Arrives with 2–3 extra pillagers/vindicators, and other illagers within 16 blocks get Strength |
| Berserker | 2 | Vindicators | Attacks faster as its health drops (up to ~3× as often) |

\* Zombie, husk, drowned, zombie villager and zombified piglin. † Skeleton, stray, bogged and wither skeleton (anything that uses the skeleton bow logic). ‡ Includes cave spiders.

Minions summoned by affixes (Horde Caller, Coven and Warlord followers, Brood Mother cave spiders, Infested silverfish) never become champions themselves.

Every affix can be switched off in `champions_affixes.properties` or the Champions config screen. Big's stats are set there too.

## F3 readout

The F3 screen shows two extra lines (on by default, can be switched off in the F3 debug options, F3 + F6):

```
RPG Difficulty: 1.50x / 3.00x max (next increase in 1h 0m)
Champion chance: 9.6% of hostile spawns
```

This is what a normal mob spawning where you stand would get right now: the strength multiplier, the most it can reach on the current game difficulty, when it next goes up, and the chance of it being a champion. It's calculated on the server (including zones, dimension datapacks and server config) and synced once a second.

## Testing commands

Operators (permission level 2) can spawn champions to try affixes out:

| Command | What it does |
| --- | --- |
| `/champion list` | Lists every enabled affix and which mobs can have it |
| `/champion demo <affix>` | Spawns a fitting mob with only that affix, 3 blocks in front of you |
| `/champion spawn <mob> <affix> [affix...]` | Spawns any mob with any combination of affixes (space or comma separated). The tier is the lowest one with enough slots for all the affixes (tier 5 if they need more than 8) |

To test difficulty growth without waiting, operators can change the world age (the time that drives scaling; the time of day is unaffected):

| Command | What it does |
| --- | --- |
| `/rpgdifficulty time query` | World age in hours, plus the difficulty and champion chance where you stand |
| `/rpgdifficulty time add <hours>` | Skips the world age forward (negative values go back) |
| `/rpgdifficulty time set <hours>` | Sets the world age, e.g. `set 0` for a fresh world or `set 20` for full strength on Normal |

Mobs that already exist keep their stats; only new spawns use the new difficulty.

For example, `/champion demo webslinger` or `/champion spawn minecraft:skeleton sniper volley sunproof`. Affix names autocomplete, and the command refuses affixes the mob can't have.

## Bestiary

Players learn what each affix does through the **bestiary**, a book opened from the button to the right of the recipe book in the survival inventory.

- **Discovery:** when a player gets within 15 blocks of a champion (the range where its health bar and affixes appear), each of its affixes is added to that player's bestiary, with a "Bestiary updated" message above the hotbar.
- **Saved per player:** discoveries are stored on the player (per world), kept on death, and synced to their client.
- **Pages:**
  - an intro with discovery progress;
  - the champion tiers (read from the current config);
  - **Champion odds:** each tier's chance on your current game difficulty, calculated the same way the game rolls them;
  - **Champion loot:** what each tier can drop;
  - **Game difficulty:** what your current difficulty changes (growth speed, max strength, champion odds);
  - **Affix slots:** how affix slots work;
  - one page per affix, grouped by slot cost, with its name, how many slots it takes, which mobs can have it, and what it does. Undiscovered affixes show "???" but still show their slot cost.

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
4. Add `new Entry("<name>", <slots>, YourAffix::new)` to `FACTORY_LIST` in `AffixRegistry`, where `<slots>` is how many affix slots it takes (1 for minor, up to 4 for the strongest). The config toggle, the config screen entry, the bestiary page and `/champion` support come from that list automatically.
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
| `config/Champions/champions_common.properties` | Tier weights, affix slots per tier, per-tier health/strength growth, `max_boss_tier` |
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
