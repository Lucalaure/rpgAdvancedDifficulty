# CurseForge listing setup

Everything to enter when creating the project at https://authors.curseforge.com/ (Create Project → Minecraft → Mods). The description to paste is in `curseforge.md`.

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

## Uploading the file

| Field | Value |
| --- | --- |
| File | `rpgadvanceddifficulty-2.5.0+1.21.1.jar` (from the GitHub v2.5.0+1.21.1 release, or `build/libs/` after `./gradlew build`). Not the `-sources` jar. |
| Display name | RPG Advanced Difficulty 2.5.0 |
| Release type | Release (or Beta if you'd like feedback first) |
| Game version | 1.21.1 |
| Mod loader | Fabric |
| Java version | Java 21 |
| Environment | Client and Server |
| Changelog | The v2.5.0+1.21.1 release notes from GitHub |

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
