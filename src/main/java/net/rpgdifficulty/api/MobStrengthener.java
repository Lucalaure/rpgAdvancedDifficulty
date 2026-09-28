package net.rpgdifficulty.api;

import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.rpgadvanceddifficulty.ChampionSpawner;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.EntityAccess;
import net.rpgdifficulty.data.DifficultyLoader;
import net.rpgdifficulty.mixin.access.AbstractArrowAccess;
import net.rpgdifficulty.zone.DifficultyZone;
import net.rpgdifficulty.zone.DifficultyZonePersistentState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class MobStrengthener {

    private static final RandomSource random = RandomSource.create();

    // Bosses are untouched by the dimension check
    // If entity != null, must be AbstractArrow and will only set the damage
    public static void changeAttributes(Mob mobEntity, ServerLevel world, @Nullable AbstractArrow persistentProjectileEntity, boolean isBossMob) {
        if (isBossMob && !RpgDifficultyMain.CONFIG.affectBosses) {
            // Bosses skip difficulty scaling but may still roll as a champion
            if (persistentProjectileEntity == null && !isStrengthened(mobEntity)) {
                setStrengthened(mobEntity);
                ChampionSpawner.tryMakeChampion(mobEntity, 1.0D);
            }
            return;
        }
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(BuiltInRegistries.ENTITY_TYPE.getKey(mobEntity.getType()).toString())) {
            if (mobEntity.isBaby() && mobEntity instanceof AgeableMob && !RpgDifficultyMain.CONFIG.affectAnimalBabies) {
                return;
            }

            DifficultyZone difficultyZone = DifficultyZonePersistentState.get(world.getServer())
                    .findZone(world.dimension().identifier().toString(), mobEntity.getX(), mobEntity.getY(), mobEntity.getZ())
                    .orElse(null);

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(world.dimension().identifier().toString())) {
                map = DifficultyLoader.dimensionDifficulty.get(world.dimension().identifier().toString());
            }

            // Factor
            double mobHealthFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double mobProtectionFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            double dynamicFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;

            // Entity Values
            double mobHealth = mobEntity.getAttributeBaseValue(Attributes.MAX_HEALTH);
            // Check if hasAttributes necessary
            double mobDamage = 0.0F;
            double mobProtection = 0.0F;
            boolean hasAttackDamageAttribute = mobEntity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE);
            boolean hasArmorAttribute = mobEntity.getAttributes().hasAttribute(Attributes.ARMOR);
            if (hasAttackDamageAttribute) {
                mobDamage = mobEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            }
            if (hasArmorAttribute) {
                mobProtection = mobEntity.getAttributeBaseValue(Attributes.ARMOR);
            }

            if (difficultyZone != null) {
                mobHealthFactor = difficultyZone.getFactor();
                mobDamageFactor = difficultyZone.getFactor();
                mobProtectionFactor = difficultyZone.getFactor();
            } else {

                // Distance, Time
                int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : world.getRespawnData().pos().getX();
                int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : world.getRespawnData().pos().getZ();

                float worldSpawnDistance = Mth.sqrt((float) mobEntity.distanceToSqr(spawnX, mobEntity.getY(), spawnZ));
                int worldTime = (int) world.getGameTime();

                // Value Editing
                // Distance
                if (RpgDifficultyMain.CONFIG.enableDistanceScaling && (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                    if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance)) {
                        worldSpawnDistance = 0;
                    } else {
                        worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                    }
                    int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                    if (!isBossMob && RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && mobEntity.level().dimension() != Level.OVERWORLD) {
                        spawnDistanceDivided = 0;
                    }
                    if (isBossMob) {
                        mobHealthFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                        mobProtectionFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                        mobDamageFactor += spawnDistanceDivided * RpgDifficultyMain.CONFIG.bossDistanceFactor;
                    } else {
                        mobHealthFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                        mobDamageFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                        mobProtectionFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
                    }

                }
                // Time
                if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                    if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200) {
                        worldTime = 0;
                    } else {
                        worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
                    }
                    int timeDivided = worldTime / ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) * 1200);
                    if (!isBossMob && RpgDifficultyMain.CONFIG.excludeTimeInOtherDimension && mobEntity.level().dimension() != Level.OVERWORLD) {
                        timeDivided = 0;
                    }
                    if (isBossMob) {
                        mobHealthFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                        mobProtectionFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                        mobDamageFactor += timeDivided * RpgDifficultyMain.CONFIG.bossTimeFactor;
                    } else {
                        mobHealthFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                        mobDamageFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                        mobProtectionFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
                    }
                }
                // Dynamic Boss Modification
                if (isBossMob && RpgDifficultyMain.CONFIG.dynamicBossModification) {
                    List<Player> list = Lists.newArrayList();

                    for (Player player : world.players()) {
                        if (new AABB(mobEntity.blockPosition()).inflate(RpgDifficultyMain.CONFIG.bossDistance).contains(player.getX(), player.getY(), player.getZ())) {
                            if (!player.isSpectator() && player.isAlive()) {
                                list.add(player);
                            }
                        }
                    }
                    for (int i = 0; i < list.size(); ++i) {
                        dynamicFactor += RpgDifficultyMain.CONFIG.dynamicBossModificator;
                    }
                    mobHealthFactor *= dynamicFactor;
                }

                // Cutoff
                double maxFactorHealth = map != null ? (double) map.get("maxFactorHealth") : RpgDifficultyMain.CONFIG.maxFactorHealth;
                double maxFactorDamage = map != null ? (double) map.get("maxFactorDamage") : RpgDifficultyMain.CONFIG.maxFactorDamage;
                double maxFactorProtection = map != null ? (double) map.get("maxFactorProtection") : RpgDifficultyMain.CONFIG.maxFactorProtection;

                if (isBossMob) {
                    maxFactorHealth = RpgDifficultyMain.CONFIG.bossMaxFactor;
                }

                if (mobHealthFactor > maxFactorHealth) {
                    mobHealthFactor = maxFactorHealth;
                }
                if (mobDamageFactor > maxFactorDamage) {
                    mobDamageFactor = maxFactorDamage;
                }
                if (mobProtectionFactor > maxFactorProtection) {
                    mobProtectionFactor = maxFactorProtection;
                }
            }

            // round factor
            mobHealthFactor = Math.round(mobHealthFactor * 100.0D) / 100.0D;
            mobProtectionFactor = Math.round(mobProtectionFactor * 100.0D) / 100.0D;
            mobDamageFactor = Math.round(mobDamageFactor * 100.0D) / 100.0D;

            // Setter
            mobHealth *= mobHealthFactor;
            mobDamage *= mobDamageFactor;
            mobProtection *= mobProtectionFactor;

            // Randomness
            if (RpgDifficultyMain.CONFIG.allowRandomValues) {
                if (random.nextFloat() <= ((float) RpgDifficultyMain.CONFIG.randomChance / 100F)) {
                    float randomFactor = (float) RpgDifficultyMain.CONFIG.randomFactor / 100F;
                    mobHealth = mobHealth * (1 - randomFactor + (random.nextDouble() * randomFactor * 2F));
                    mobDamage = mobDamage * (1 - randomFactor + (random.nextDouble() * randomFactor * 2F));

                    // round value
                    mobHealth = Math.round(mobHealth * 100.0D) / 100.0D;
                    mobDamage = Math.round(mobDamage * 100.0D) / 100.0D;
                }
            }

            AttributeSupplier mobEntityDefaultAttributes = getDefaultAttributes(mobEntity);

            // Test purpose
            if (RpgDifficultyMain.CONFIG.hudTesting) {
                if (persistentProjectileEntity != null) {
                    RpgDifficultyMain.LOGGER.info(BuiltInRegistries.ENTITY_TYPE.getKey(persistentProjectileEntity.getType()).toString() + "; DamageFactor: " + mobDamageFactor);
                } else {
                    RpgDifficultyMain.LOGGER.info(BuiltInRegistries.ENTITY_TYPE.getKey(mobEntity.getType()).toString() + "; HealthFactor: " + mobHealthFactor + "; DamageFactor: " + mobDamageFactor + "; Health: "
                            + mobHealth + ";  Old Health: " + mobEntity.getHealth() + "; Default HP: "
                            + (mobEntityDefaultAttributes != null ? mobEntityDefaultAttributes.getBaseValue(Attributes.MAX_HEALTH) : "-"));
                }
            }

            if (persistentProjectileEntity != null) {
                persistentProjectileEntity.setBaseDamage(((AbstractArrowAccess) persistentProjectileEntity).rpgdifficulty$getBaseDamage() * mobDamageFactor * ChampionSpawner.getStrengthMultiplier(mobEntity));
            } else
                // Check if mob already has increased strength
                if (mobEntityDefaultAttributes != null && !isStrengthened(mobEntity)) {
                    // Set Values
                    mobEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(mobHealth);
                    mobEntity.heal(mobEntity.getMaxHealth());
                    if (hasAttackDamageAttribute) {
                        mobEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(mobDamage);
                    }
                    if (hasArmorAttribute) {
                        mobEntity.getAttribute(Attributes.ARMOR).setBaseValue(mobProtection);
                    }

                    setMobHealthMultiplier(mobEntity, (float) mobHealthFactor);
                    setStrengthened(mobEntity);

                    // Variant and champion rolls on top of the scaled stats, more likely the harder it gets
                    ChampionSpawner.tryApplyVariant(mobEntity, mobHealthFactor);
                    ChampionSpawner.tryMakeChampion(mobEntity, mobHealthFactor);
                }
        }
    }

    public static double getDamageFactor(Entity entity) {
        if (!RpgDifficultyMain.CONFIG.excludedEntity.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString())) {

            if (entity.level().getServer() != null) {
                DifficultyZone difficultyZone = DifficultyZonePersistentState.get(entity.level().getServer())
                        .findZone(entity.level().dimension().identifier().toString(), entity.getX(), entity.getY(), entity.getZ())
                        .orElse(null);

                if (difficultyZone != null) {
                    return difficultyZone.getFactor();
                }
            }

            HashMap<String, Object> map = null;

            if (!DifficultyLoader.dimensionDifficulty.isEmpty() && DifficultyLoader.dimensionDifficulty.containsKey(entity.level().dimension().identifier().toString())) {
                map = DifficultyLoader.dimensionDifficulty.get(entity.level().dimension().identifier().toString());
            }

            double mobDamageFactor = map != null ? (double) map.get("startingFactor") : RpgDifficultyMain.CONFIG.startingFactor;
            int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : ((ServerLevel) entity.level()).getRespawnData().pos().getX();
            int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : ((ServerLevel) entity.level()).getRespawnData().pos().getZ();

            float worldSpawnDistance = Mth.sqrt((float) entity.distanceToSqr(spawnX, entity.getY(), spawnZ));
            int worldTime = (int) entity.level().getGameTime();

            if (RpgDifficultyMain.CONFIG.enableDistanceScaling && (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance) != 0) {
                if ((int) worldSpawnDistance <= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance)) {
                    worldSpawnDistance = 0;
                } else {
                    worldSpawnDistance -= (map != null ? (int) map.get("startingDistance") : RpgDifficultyMain.CONFIG.startingDistance);
                }
                int spawnDistanceDivided = (int) worldSpawnDistance / (map != null ? (int) map.get("increasingDistance") : RpgDifficultyMain.CONFIG.increasingDistance);
                if (RpgDifficultyMain.CONFIG.excludeDistanceInOtherDimension && entity.level().dimension() != Level.OVERWORLD) {
                    spawnDistanceDivided = 0;
                }
                mobDamageFactor += spawnDistanceDivided * (map != null ? (double) map.get("distanceFactor") : RpgDifficultyMain.CONFIG.distanceFactor);
            }
            if ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) != 0) {
                if (worldTime <= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200) {
                    worldTime = 0;
                } else {
                    worldTime -= (map != null ? (int) map.get("startingTime") : RpgDifficultyMain.CONFIG.startingTime) * 1200;
                }
                int timeDivided = worldTime / ((map != null ? (int) map.get("increasingTime") : RpgDifficultyMain.CONFIG.increasingTime) * 1200);
                mobDamageFactor += timeDivided * (map != null ? (double) map.get("timeFactor") : RpgDifficultyMain.CONFIG.timeFactor);
            }

            double maxFactor = map != null ? (double) map.get("maxFactorDamage") : RpgDifficultyMain.CONFIG.maxFactorDamage;
            if (mobDamageFactor > maxFactor) {
                mobDamageFactor = maxFactor;
            }
            mobDamageFactor *= RpgDifficultyMain.CONFIG.creeperExplosionFactor;
            if (mobDamageFactor < 1.0F) {
                mobDamageFactor = 1.0F;
            }
            // round factor
            mobDamageFactor = Math.round(mobDamageFactor * 100.0D) / 100.0D;

            return mobDamageFactor;
        }
        return 1.0D;
    }

    public static int getXpToDropAddition(Mob mobEntity, ServerLevel world, int original) {
        if (RpgDifficultyMain.CONFIG.extraXp) {
            float xpFactor = getMobHealthMultiplier(mobEntity);

            float maxXPFactor = RpgDifficultyMain.CONFIG.maxXPFactor;
            if (xpFactor > maxXPFactor) {
                xpFactor = maxXPFactor;
            }
            return (int) (original * xpFactor);
        } else {
            return original;
        }
    }

    public static void dropMoreLoot(Mob mobEntity, ServerLevel world, LootTable lootTable, LootParams lootContextParameterSet) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot) {
            float level = getMobHealthMultiplier(mobEntity);
            if (level > 0.01f) {
                float dropChance = level * RpgDifficultyMain.CONFIG.moreLootChance;
                if (dropChance > RpgDifficultyMain.CONFIG.maxLootChance)
                    dropChance = RpgDifficultyMain.CONFIG.maxLootChance;

                if (mobEntity.level().getRandom().nextFloat() <= dropChance) {
                    List<ItemStack> list = lootTable.getRandomItems(lootContextParameterSet);
                    for (ItemStack itemStack : list) {
                        if (mobEntity.level().getRandom().nextFloat() < RpgDifficultyMain.CONFIG.chanceForEachItem) {
                            continue;
                        }
                        itemStack.grow((int) (itemStack.getCount() * dropChance));
                        mobEntity.spawnAtLocation(world, itemStack);
                    }
                }
            }
        }

    }

    // DefaultAttributes.getSupplier/hasSupplier are public in 26.x (replaces the old DefaultAttributeRegistry accessor)
    @SuppressWarnings("unchecked")
    @Nullable
    public static AttributeSupplier getDefaultAttributes(LivingEntity livingEntity) {
        if (!DefaultAttributes.hasSupplier(livingEntity.getType())) {
            return null;
        }
        return DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) livingEntity.getType());
    }

    public static void setMobHealthMultiplier(Mob mobEntity, float multiplier) {
        ((EntityAccess) mobEntity).setMobHealthMultiplier(multiplier);
    }

    public static float getMobHealthMultiplier(Mob mobEntity) {
        return ((EntityAccess) mobEntity).getMobHealthMultiplier();
    }

    public static boolean isStrengthened(Mob mobEntity) {
        return ((EntityAccess) mobEntity).isStrengthened();
    }

    public static void setStrengthened(Mob mobEntity) {
        ((EntityAccess) mobEntity).setStrengthened(true);
    }

}
