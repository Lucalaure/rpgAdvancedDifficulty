# CurseForge listing

Everything to enter when creating the project at https://authors.curseforge.com/ (Create Project → Minecraft → Mods).

## Project details

| Field | Value |
| --- | --- |
| Project name | RPG Advanced Difficulty |
| Summary | Mobs grow stronger as your world ages, and elite champions rise to meet you. |
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

**The longer you play, the deadlier your world becomes.**

Mobs grow stronger as your world ages, and some spawn as **champions**: elite enemies with tiers, special abilities and better loot.

## Who it's for
- **Survival players** who find the late game too easy once they have diamond gear.
- **Long-term worlds and SMP servers** that want a challenge that keeps growing instead of staying flat.
- **RPG and adventure modpacks** that want tougher, more varied fights that reward you for winning them.

It starts gentle, so early game stays familiar, and everything can be tuned in the config.

## Features
- **Scaling mobs:** +10% health, damage and armor per hour of world time, faster on Hard and slower on Easy.
- **Champions (tiers 1 to 5):** boosted stats, a health bar, and up to 8 affix slots.
- **40 affixes:** like Shielding, Vampiric, Undying, Stormcaller, invisible Stalker creepers, Sniper skeletons and Webslinger spiders.
- **Tiered loot:** higher tiers drop better rewards, up to nether stars and Mending books.
- **Bestiary:** a book you start with that records each affix you encounter and explains it.
- **Extras:** advancements, an F3 difficulty readout, testing commands, and full Mod Menu config.

## Requirements
Fabric API, Cloth Config and Java 25. Mod Menu is optional. Install on both client and server.

Don't use it with RpgDifficulty or Champions; it already includes both.

## Credits
Based on [RpgDifficulty](https://github.com/Globox1997/RpgDifficulty) by Globox_Z and [Champions](https://github.com/crystalx375/Champions) by Crystal. Source: [GitHub](https://github.com/Lucalaure/rpgAdvancedDifficulty)
```

## Uploading the file

| Field | Value |
| --- | --- |
| File | `rpgadvanceddifficulty-2.2.0+26.3.jar` (from the GitHub v2.2.0 release, or `build/libs/` after `./gradlew build`). Not the `-sources` jar. |
| Display name | RPG Advanced Difficulty 2.2.0 |
| Release type | Release (or Beta if you'd like feedback first) |
| Game version | 26.3 |
| Mod loader | Fabric |
| Java version | Java 25 |
| Environment | Client and Server |
| Changelog | The v2.2.0 release notes from GitHub |

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
