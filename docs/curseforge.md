# CurseForge listing

Everything to enter when creating the project at https://authors.curseforge.com/ (Create Project → Minecraft → Mods).

## Project details

| Field | Value |
| --- | --- |
| Project name | RPG Advanced Difficulty |
| Summary | Mobs grow stronger as your world ages, and elite champions with deadly affixes rise to meet you. |
| Logo / avatar | `media/logo.png` (512×512) |
| Main category | Adventure and RPG |
| Additional categories | Mobs (under World Gen) |
| License | GNU General Public License v3 (GPLv3) |
| Source URL | https://github.com/Lucalaure/rpgAdvancedDifficulty |
| Issues URL | https://github.com/Lucalaure/rpgAdvancedDifficulty/issues |

If the summary is too long for the field, use: **Mobs grow stronger over time, and elite champions rise.**

The license has to be GPLv3 because the mod includes code from Champions (GPLv3).

## Description

Paste this into the description editor (switch it to Markdown):

```markdown
# RPG Advanced Difficulty

**The longer you play, the deadlier the night becomes.**

RPG Advanced Difficulty makes mobs grow stronger as your world ages, and turns some of them into **champions**: elite enemies with a tier, boosted stats, special abilities and better loot. The further into a world you get, and the harder your difficulty setting, the more often champions appear and the higher their tiers climb.

## Mobs that grow with your world
- Mobs get **+10% health, damage and armor for every hour** your world has been running, up to **3×** on Normal.
- Your **game difficulty matters**: Easy grows half as fast and caps lower, Hard grows 50% faster and caps higher.
- Optional **distance scaling** makes mobs stronger the further you travel from spawn.
- Stronger mobs drop more XP.
- A **damage limit** keeps any mob from hitting more than 5× its vanilla damage, however everything stacks.

## Champions
Any hostile mob can spawn as a **champion** of tier 1 to 5, with a coloured particle aura, a custom health bar showing its affixes, and up to 6× health.

Champions get **affixes**: special abilities that take up affix slots. Higher tiers have more slots and roll stronger affixes. There are **40 affixes**, including:
- **General:** Shielding, Molten, Arctic, Vampiric, Undying, Stormcaller, Enraged, Withering, Volatile, Reflection, Adaptive and more.
- **Mob-specific:** Sniper and Volley skeletons, Webslinger and Brood Mother spiders, invisible Stalker creepers, Blink and Thief endermen, Coven and Alchemist witches, Inferno and Barrage blazes, Splitter slimes, Warlord illagers, Berserker vindicators, Horde Caller zombies and more.

## Rewarding loot
Champions drop extra loot from a pool for their tier: materials, XP, golden apples, diamonds, enchanted gear, totems, netherite scrap and armor trims. Enchanted books get stronger with the tier, and only tier 5 champions can drop treasure enchantments like Mending.

## The Bestiary
A book button next to the recipe book opens the **bestiary**. Get close to a champion to record its affixes. It explains what each affix does, which mobs can have it and how many slots it takes, along with champion odds, loot and difficulty for your current game.

## And more
- **F3 readout** of the difficulty and champion chance where you stand.
- **Advancements**: Champion Slayer, Apex Hunter, and the challenge Know Thy Enemy for discovering every affix.
- **Commands** for testing: `/champion demo <affix>`, `/champion spawn <mob> <affixes>`, `/rpgdifficulty time add <hours>`.
- **Fully configurable** through Mod Menu: growth speed, caps, champion odds, affix slots, every affix on or off.
- Works with **[Bloodmoon Events](https://github.com/Lucalaure/BloodmoonEvents)**: Blood Moon hordes bring more and stronger champions.

## Requirements
- Fabric Loader and **Fabric API**
- **Cloth Config**
- Mod Menu (optional, for the in-game config screens)
- Java 25

Install on **both the client and the server**.

## Credits
Built on two great mods:
- [RpgDifficulty](https://github.com/Globox1997/RpgDifficulty) by Globox_Z (MIT)
- [Champions](https://github.com/crystalx375/Champions) by Crystal (GPLv3)

Don't install it together with RpgDifficulty or Champions; it already includes both.

Source code and issues: https://github.com/Lucalaure/rpgAdvancedDifficulty
```

## Uploading the file

| Field | Value |
| --- | --- |
| File | `rpgadvanceddifficulty-2.1.0+26.3.jar` (from the GitHub v2.1.0 release, or `build/libs/` after `./gradlew build`). Not the `-sources` jar. |
| Display name | RPG Advanced Difficulty 2.1.0 |
| Release type | Release (or Beta if you'd like feedback first) |
| Game version | 26.3 |
| Mod loader | Fabric |
| Java version | Java 25 |
| Environment | Client and Server |
| Changelog | The v2.1.0 release notes from GitHub |

### Relations

| Project | Relation |
| --- | --- |
| Fabric API | Required dependency |
| Cloth Config API | Required dependency |
| Mod Menu | Optional dependency |
| RpgDifficulty | Incompatible |
| Champions | Incompatible |

## Gallery screenshots

Good images for the gallery (take them with `./gradlew runClient`, F1 hides the HUD, F2 saves a screenshot):
- A champion with its health bar and affix names showing
- The bestiary open on an affix page and on the champion odds page
- A tier 5 champion with several affixes
- The F3 difficulty readout
