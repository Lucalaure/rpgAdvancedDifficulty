package net.rpgadvanceddifficulty.gametest;

import crystal.champions.IBullet;
import crystal.champions.IChampions;
import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixEvents;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.effects.CustomStatusEffects;
import crystal.champions.util.ChampionRank;
import crystal.champions.util.PrepareChampions;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.rpgadvanceddifficulty.DifficultyModes;
import net.rpgdifficulty.mixin.access.AbstractArrowAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Checks every champion affix and the /champion command in a real world: a champion with the affix
 * is spawned next to the player and the affix's actual effect is asserted.
 * Log lines start with "AFFIX TEST"; a failure names the affix and what went wrong.
 */
public class AffixClientGameTest implements FabricClientGameTest {
    private static final int SECOND = 20;

    private ClientGameTestContext context;
    private TestServerContext server;
    private double px, py, pz;

    @Override
    public void runTest(ClientGameTestContext context) {
        this.context = context;
        try (TestSingleplayerContext singleplayer = context.worldBuilder()
                .adjustSettings(settings -> settings.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL))
                .create()) {
            singleplayer.getConnection().waitForChunksRender();
            server = singleplayer.getServer();
            server.runCommand("gamerule minecraft:advance_time false");
            server.runCommand("time set night");
            server.runOnServer(s -> {
                ServerPlayer player = player(s);
                px = player.getX();
                py = player.getY();
                pz = player.getZ();
            });

            // General affixes
            run("hasty", this::testHasty);
            run("lively", this::testLively);
            run("shielding", this::testShielding);
            run("adaptive", this::testAdaptive);
            run("dampening", this::testDampening);
            run("reflection", this::testReflection);
            run("knocking", this::testKnocking);
            run("blinded + paralyzing", this::testBlindedAndParalyzing);
            run("arctic", this::testArctic);
            run("molten", this::testMolten);
            run("desecrating", this::testDesecrating);
            run("plagued", this::testPlagued);
            run("infected", this::testInfected);
            run("big", this::testBig);
            run("creeper fuse", this::testCreeperFuse);

            // Mob-specific affixes
            run("horde_caller", this::testHordeCaller);
            run("sniper", this::testSniper);
            run("volley", this::testVolley);
            run("frost_archer", this::testFrostArcher);
            run("stalker", this::testStalker);
            run("webslinger", this::testWebslinger);
            run("brood_mother", this::testBroodMother);
            run("pouncer", this::testPouncer);
            run("blink", this::testBlink);
            run("thief", this::testThief);
            run("alchemist", this::testAlchemist);
            run("coven", this::testCoven);
            run("inferno", this::testInferno);
            run("barrage", this::testBarrage);
            run("splitter", this::testSplitter);
            run("champion loot", this::testChampionLoot);
            run("loot tables", this::testLootTables);
            run("sticky", this::testSticky);
            run("warlord", this::testWarlord);
            run("berserker", this::testBerserker);
            run("sunproof", this::testSunproof);
            run("commands", this::testCommands);
            run("affix slots", this::testAffixSlots);
            run("game difficulty", this::testGameDifficulty);

            log("all affix checks passed");
        }
    }

    private interface Test {
        void run() throws Exception;
    }

    private void run(String name, Test test) {
        clearArea();
        try {
            test.run();
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError("AFFIX TEST FAILED - " + name + ": " + e, e);
        }
        log(name + " OK");
    }

    // --- General affixes

    private void testHasty() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "hasty", 2.0);
        server.waitFor(s -> champion(s, id).hasEffect(MobEffects.SPEED)
                && champion(s, id).getEffect(MobEffects.SPEED).getAmplifier() == 4, 2 * SECOND);
    }

    private void testLively() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "lively", 2.0);
        AtomicReference<Float> before = new AtomicReference<>();
        server.runOnServer(s -> {
            Mob mob = champion(s, id);
            mob.hurtServer(level(s), level(s).damageSources().generic(), 6.0f);
            before.set(mob.getHealth());
        });
        server.waitFor(s -> champion(s, id).getHealth() > before.get(), 3 * SECOND);
    }

    private void testShielding() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "shielding", 2.0);
        server.waitFor(s -> ((IChampions) champion(s, id)).champions$isShielding(), 20 * SECOND);
        server.runOnServer(s -> {
            Mob mob = champion(s, id);
            float health = mob.getHealth();
            boolean hurt = mob.hurtServer(level(s), level(s).damageSources().playerAttack(player(s)), 5.0f);
            check(!hurt && mob.getHealth() == health, "shielding: took damage while shielded");
        });
    }

    private void testAdaptive() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "adaptive", 2.0);
        server.runOnServer(s -> {
            Mob mob = champion(s, id);
            float first = hit(mob, level(s).damageSources().playerAttack(player(s)), 4.0f);
            float second = hit(mob, level(s).damageSources().playerAttack(player(s)), 4.0f);
            float third = hit(mob, level(s).damageSources().playerAttack(player(s)), 4.0f);
            check(second < first && third < second, "adaptive: repeated hits not reduced (" + first + ", " + second + ", " + third + ")");
            Zombie otherAttacker = EntityTypes.ZOMBIE.create(level(s), EntitySpawnReason.COMMAND);
            float reset = hit(mob, level(s).damageSources().mobAttack(otherAttacker), 4.0f);
            check(reset >= first - 0.01f, "adaptive: a new damage type did not reset (" + reset + " vs " + first + ")");
        });
    }

    private void testDampening() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "dampening", 2.0);
        server.runOnServer(s -> {
            Mob mob = champion(s, id);
            Entity projectile = EntityTypes.SNOWBALL.create(level(s), EntitySpawnReason.COMMAND);
            float melee = hit(mob, level(s).damageSources().playerAttack(player(s)), 4.0f);
            float indirect = hit(mob, level(s).damageSources().thrown(projectile, player(s)), 4.0f);
            check(melee > 2.5f, "dampening: melee damage was reduced (" + melee + ")");
            check(indirect < melee * 0.75f, "dampening: indirect damage not reduced (" + indirect + " vs " + melee + ")");
        });
    }

    private void testReflection() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "reflection", 2.0);
        server.runOnServer(s -> {
            ServerPlayer player = resetPlayer(s);
            Mob mob = champion(s, id);
            mob.damageCooldownTime = 0;
            mob.hurtServer(level(s), level(s).damageSources().playerAttack(player), 4.0f);
            check(player.getHealth() < player.getMaxHealth(), "reflection: attacker took no damage");

            resetPlayer(s);
            mob.damageCooldownTime = 20;
            mob.hurtServer(level(s), level(s).damageSources().playerAttack(player), 0.5f);
            check(player.getHealth() == player.getMaxHealth(), "reflection: reflected a hit that did no damage");
        });
    }

    private void testKnocking() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "knocking", 1.5);
        // More than 2 seconds since the player was last hurt: the first hit of a fight must work
        context.waitTicks(3 * SECOND);
        server.runOnServer(s -> {
            ServerPlayer player = resetPlayer(s);
            check(champion(s, id).doHurtTarget(level(s), player), "knocking: attack did not land");
            check(player.hasEffect(MobEffects.SLOWNESS), "knocking: no slowness on the first hit");
            // Vanilla zombie knockback is ~0.4; Knocking adds ~1.5 more
            check(player.getDeltaMovement().horizontalDistance() > 1.0, "knocking: no extra knockback (" + player.getDeltaMovement() + ")");
        });
    }

    private void testBlindedAndParalyzing() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "blinded,paralyzing", 1.5);
        for (int attempt = 0; attempt < 100; attempt++) {
            server.runOnServer(s -> champion(s, id).doHurtTarget(level(s), resetPlayer(s)));
            if (server.computeOnServer(s -> player(s).hasEffect(MobEffects.BLINDNESS) && player(s).hasEffect(CustomStatusEffects.STUN))) break;
        }
        server.runOnServer(s -> {
            check(player(s).hasEffect(MobEffects.BLINDNESS), "blinded: never blinded in 100 hits (20% chance each)");
            check(player(s).hasEffect(CustomStatusEffects.STUN), "paralyzing: never stunned in 100 hits (10% chance each)");
            check(player(s).getAttributeValue(Attributes.MOVEMENT_SPEED) < 0.001, "paralyzing: stunned player can still move");
        });
    }

    private void testArctic() {
        spawnChampion(EntityTypes.ZOMBIE, "arctic", 4.0);
        server.waitFor(s -> bullets(s).stream().anyMatch(b -> ((IBullet) b).champions$isArctic()), 10 * SECOND);
        server.waitFor(s -> player(s).hasEffect(MobEffects.SLOWNESS), 10 * SECOND);
    }

    private void testMolten() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "molten", 4.0);
        server.waitFor(s -> champion(s, id).hasEffect(MobEffects.FIRE_RESISTANCE), 2 * SECOND);
        server.waitFor(s -> bullets(s).stream().anyMatch(b -> ((IBullet) b).champions$isMolten()), 10 * SECOND);
        server.waitFor(s -> player(s).getRemainingFireTicks() > 0, 10 * SECOND);
    }

    private void testDesecrating() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "desecrating", 3.0);
        server.runOnServer(s -> {
            Mob mob = champion(s, id);
            mob.tickCount = 1000;
            affix("desecrating").onAttack(mob, mob);
            check(!level(s).getEntitiesOfClass(AreaEffectCloud.class, player(s).getBoundingBox().inflate(1)).isEmpty(),
                    "desecrating: no cloud under the target");
        });
        server.waitFor(s -> player(s).getHealth() < player(s).getMaxHealth(), 5 * SECOND);
    }

    private void testPlagued() {
        // A spider, since zombies are undead and immune to poison anyway
        UUID id = spawnChampion(EntityTypes.SPIDER, "plagued", 1.5);
        server.waitFor(s -> player(s).hasEffect(MobEffects.POISON), 5 * SECOND);
        server.runOnServer(s -> {
            Mob spider = champion(s, id);
            check(!spider.hasEffect(MobEffects.POISON), "plagued: the champion poisoned itself");
            check(!spider.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.POISON, 100)), "plagued: the champion can be poisoned");
        });
    }

    private void testInfected() {
        UUID id = spawnChampion(EntityTypes.ZOMBIE, "infected", 4.0);
        server.runOnServer(s -> {
            Mob mob = champion(s, id);
            Affix infected = affix("infected");

            mob.setTarget(null);
            mob.tickCount = 600;
            infected.onAttack(mob, mob);
            check(nearby(s, Silverfish.class).isEmpty(), "infected: spawned silverfish while not fighting");

            mob.setTarget(player(s));
            infected.onAttack(mob, mob);
            List<Silverfish> minions = nearby(s, Silverfish.class);
            check(!minions.isEmpty(), "infected: no silverfish spawned in combat");
            minions.forEach(m -> check(neverChampion(m), "infected: a silverfish minion can become a champion"));
        });
    }

    private void testBig() {
        UUID zombie = spawnChampion(EntityTypes.ZOMBIE, "big", 3.0);
        UUID skeleton = spawnChampion(EntityTypes.SKELETON, "big", -3.0);
        server.runOnServer(s -> {
            for (UUID id : List.of(zombie, skeleton)) {
                Mob big = champion(s, id);
                Mob normal = (Mob) big.getType().create(level(s), EntitySpawnReason.COMMAND);
                check(big.getBbHeight() > normal.getBbHeight() * 1.2f, "big: " + big.getType() + " is not bigger");
                check(big.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) < normal.getAttributeBaseValue(Attributes.MOVEMENT_SPEED),
                        "big: " + big.getType() + " is not slower");
            }
        });
    }

    private void testCreeperFuse() throws ReflectiveOperationException {
        UUID id = spawnChampion(EntityTypes.CREEPER, "hasty", 6.0, 5);
        context.waitTicks(SECOND);
        server.runOnServer(s -> {
            Field maxSwell = Creeper.class.getDeclaredField("maxSwell");
            maxSwell.setAccessible(true);
            int fuse = maxSwell.getInt(champion(s, id));
            check(fuse == 50, "creeper: tier 5 fuse should be 50 ticks, was " + fuse);
        });
    }

    // --- Mob-specific affixes

    private void testHordeCaller() {
        spawnChampion(EntityTypes.ZOMBIE, "horde_caller", 4.0);
        server.waitFor(s -> nearby(s, Zombie.class).size() >= 3, 2 * SECOND);
        context.waitTicks(SECOND);
        server.runOnServer(s -> {
            List<Zombie> zombies = nearby(s, Zombie.class);
            check(zombies.size() >= 3 && zombies.size() <= 4, "horde_caller: expected 2-3 extra zombies, found " + (zombies.size() - 1));
            long minions = zombies.stream().filter(this::neverChampion).count();
            check(minions >= 2, "horde_caller: called zombies can become champions");
        });
    }

    private void testSniper() throws ReflectiveOperationException {
        UUID sniper = spawnChampion(EntityTypes.SKELETON, "sniper", 5.0);
        UUID normal = spawnPlain(EntityTypes.SKELETON, -5.0);
        server.runOnServer(s -> {
            Field bowGoalField = AbstractSkeleton.class.getDeclaredField("bowGoal");
            bowGoalField.setAccessible(true);
            Field interval = RangedBowAttackGoal.class.getDeclaredField("attackIntervalMin");
            interval.setAccessible(true);
            int sniperInterval = interval.getInt(bowGoalField.get(champion(s, sniper)));
            check(sniperInterval == 60, "sniper: bow cooldown is " + sniperInterval + " ticks, expected 60");

            AbstractArrow sniperArrow = shoot(s, (AbstractSkeleton) champion(s, sniper));
            AbstractArrow normalArrow = shoot(s, (AbstractSkeleton) champion(s, normal));
            double sniperSpeed = sniperArrow.getDeltaMovement().length();
            double normalSpeed = normalArrow.getDeltaMovement().length();
            check(sniperSpeed > normalSpeed * 1.3, "sniper: arrow not faster (" + sniperSpeed + " vs " + normalSpeed + ")");
            double sniperDamage = ((AbstractArrowAccess) sniperArrow).rpgdifficulty$getBaseDamage();
            double normalDamage = ((AbstractArrowAccess) normalArrow).rpgdifficulty$getBaseDamage();
            check(sniperDamage >= normalDamage * 1.45, "sniper: arrow damage not +50% (" + sniperDamage + " vs " + normalDamage + ")");
        });
    }

    private void testVolley() {
        UUID id = spawnChampion(EntityTypes.SKELETON, "volley", 6.0);
        server.runOnServer(s -> {
            Mob mob = champion(s, id);
            mob.tickCount = 60;
            affix("volley").onAttack(mob, mob);
            int arrows = nearby(s, AbstractArrow.class).size();
            check(arrows == 3, "volley: fired " + arrows + " arrows, expected 3");
        });
    }

    private void testFrostArcher() {
        UUID id = spawnChampion(EntityTypes.STRAY, "frost_archer", 6.0);
        server.runOnServer(s -> {
            AbstractArrow arrow = shoot(s, (AbstractSkeleton) champion(s, id));
            check(arrow.entityTags().contains("champions.frost_arrow"), "frost_archer: arrow was not marked");
            ServerPlayer player = player(s);
            player.setTicksFrozen(0);
            AffixEvents.onProjectileHit(arrow, new EntityHitResult(player));
            check(player.getTicksFrozen() > player.getTicksRequiredToFreeze(), "frost_archer: player not frozen (" + player.getTicksFrozen() + ")");
        });
    }

    private void testStalker() {
        UUID id = spawnChampion(EntityTypes.CREEPER, "stalker", 6.0);
        server.waitFor(s -> champion(s, id).hasEffect(MobEffects.INVISIBILITY), SECOND);
        server.runOnServer(s -> ((Creeper) champion(s, id)).setSwellDir(1));
        server.waitFor(s -> !champion(s, id).hasEffect(MobEffects.INVISIBILITY), 5);
    }

    private void testWebslinger() {
        UUID id = spawnChampion(EntityTypes.SPIDER, "webslinger", 6.0);
        AtomicReference<BlockPos> webPos = new AtomicReference<>();
        server.runOnServer(s -> {
            Mob spider = champion(s, id);
            spider.tickCount = 80;
            affix("webslinger").onAttack(spider, spider);
            List<Snowball> webs = nearby(s, Snowball.class);
            check(webs.size() == 1 && webs.getFirst().getItem().is(Items.COBWEB), "webslinger: no cobweb thrown");
            AffixEvents.onProjectileHit(webs.getFirst(), new EntityHitResult(player(s)));
            webs.getFirst().discard();
            webPos.set(player(s).blockPosition());
            check(level(s).getBlockState(webPos.get()).is(Blocks.COBWEB), "webslinger: no cobweb placed on the target");
        });
        context.waitTicks(110);
        server.runOnServer(s -> check(level(s).getBlockState(webPos.get()).isAir(), "webslinger: cobweb did not disappear"));
    }

    private void testBroodMother() {
        UUID id = spawnChampion(EntityTypes.SPIDER, "brood_mother", 5.0);
        server.runOnServer(s -> {
            hit(champion(s, id), level(s).damageSources().playerAttack(player(s)), 2.0f);
            List<CaveSpider> brood = nearby(s, CaveSpider.class);
            check(brood.size() == 2, "brood_mother: spawned " + brood.size() + " cave spiders, expected 2");
            brood.forEach(spider -> check(neverChampion(spider), "brood_mother: a cave spider can become a champion"));
        });
    }

    private void testPouncer() {
        UUID id = spawnChampion(EntityTypes.SPIDER, "pouncer", 7.0);
        context.waitTicks(10);
        server.runOnServer(s -> {
            Mob spider = champion(s, id);
            spider.tickCount = 40;
            spider.setOnGround(true); // mobs without AI never update onGround in the test
            affix("pouncer").onAttack(spider, spider);
            Vec3 motion = spider.getDeltaMovement();
            check(motion.y > 0.4 && motion.horizontalDistance() > 0.5, "pouncer: did not leap (" + motion + ")");
        });
    }

    private void testBlink() {
        UUID id = spawnChampion(EntityTypes.ENDERMAN, "blink", 6.0);
        server.runOnServer(s -> {
            Mob enderman = champion(s, id);
            Vec3 before = enderman.position();
            hit(enderman, level(s).damageSources().playerAttack(player(s)), 1.0f);
            check(enderman.position().distanceTo(before) > 2.0, "blink: did not teleport");
            check(enderman.distanceTo(player(s)) < 4.0, "blink: did not teleport next to the attacker (" + enderman.distanceTo(player(s)) + ")");
        });
    }

    private void testThief() {
        UUID id = spawnChampion(EntityTypes.ENDERMAN, "thief", 1.5);
        server.runOnServer(s -> player(s).setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD)));
        for (int attempt = 0; attempt < 60; attempt++) {
            server.runOnServer(s -> champion(s, id).doHurtTarget(level(s), resetPlayer(s)));
            if (server.computeOnServer(s -> player(s).getMainHandItem().isEmpty())) break;
        }
        server.runOnServer(s -> {
            check(player(s).getMainHandItem().isEmpty(), "thief: never knocked the item away in 60 hits (25% chance each)");
            check(nearby(s, ItemEntity.class).stream().anyMatch(item -> item.getItem().is(Items.DIAMOND_SWORD)), "thief: dropped item not found");
        });
    }

    private void testAlchemist() {
        UUID id = spawnChampion(EntityTypes.WITCH, "alchemist", 5.0);
        server.runOnServer(s -> {
            Mob witch = champion(s, id);
            witch.tickCount = 100;
            affix("alchemist").onAttack(witch, witch);
            List<ThrownSplashPotion> potions = nearby(s, ThrownSplashPotion.class);
            check(potions.size() == 1, "alchemist: no potion thrown");
            PotionContents contents = potions.getFirst().getItem().get(DataComponents.POTION_CONTENTS);
            check(contents != null && contents.customEffects().stream().anyMatch(e -> e.is(MobEffects.WEAKNESS)
                    || e.is(MobEffects.MINING_FATIGUE) || e.is(MobEffects.LEVITATION)), "alchemist: potion has the wrong effect");
        });
    }

    private void testCoven() {
        UUID witch = spawnChampion(EntityTypes.WITCH, "coven", 5.0);
        UUID ally = spawnPlain(EntityTypes.ZOMBIE, 7.0);
        server.runOnServer(s -> {
            Mob zombie = champion(s, ally);
            zombie.setHealth(zombie.getMaxHealth() - 10);
            float before = zombie.getHealth();
            Mob mob = champion(s, witch);
            mob.tickCount = 40;
            affix("coven").onTick(mob);
            check(zombie.getHealth() > before, "coven: nearby hostile mob was not healed");
            // 1-3 followers arrive with it (the plain zombie ally is the only other monster we spawned)
            List<Mob> followers = nearby(s, Mob.class).stream()
                    .filter(m -> m != mob && m != zombie && (m instanceof Zombie || m instanceof AbstractSkeleton || m instanceof net.minecraft.world.entity.monster.spider.Spider))
                    .toList();
            check(followers.size() >= 1 && followers.size() <= 3, "coven: expected 1-3 followers, found " + followers.size());
            followers.forEach(f -> check(neverChampion(f), "coven: a follower can become a champion"));
            affix("coven").onTick(mob);
            check(nearby(s, Mob.class).size() == followers.size() + 2, "coven: summoned followers more than once");
        });
    }

    private void testInferno() {
        UUID id = spawnChampion(EntityTypes.BLAZE, "inferno", 5.0);
        server.runOnServer(s -> {
            Mob blaze = champion(s, id);
            SmallFireball fireball = new SmallFireball(level(s), blaze, new Vec3(0, -1, 0));
            BlockPos ground = BlockPos.containing(px + 10, py - 1, pz);
            AffixEvents.onProjectileHit(fireball, new BlockHitResult(Vec3.atCenterOf(ground).add(0, 0.5, 0), Direction.UP, ground, false));
            List<BlockPos> fires = BlockPos.betweenClosedStream(ground.offset(-2, 0, -2), ground.offset(2, 2, 2))
                    .filter(pos -> level(s).getBlockState(pos).is(Blocks.FIRE)).map(BlockPos::immutable).toList();
            check(fires.size() >= 5, "inferno: only " + fires.size() + " fire blocks placed");
            fires.forEach(pos -> level(s).removeBlock(pos, false));
        });
    }

    private void testBarrage() {
        UUID id = spawnChampion(EntityTypes.BLAZE, "barrage", 6.0);
        server.runOnServer(s -> {
            Mob blaze = champion(s, id);
            blaze.tickCount = 80;
            affix("barrage").onAttack(blaze, blaze);
            int fireballs = nearby(s, SmallFireball.class).size();
            check(fireballs == 5, "barrage: fired " + fireballs + " fireballs, expected 5");
        });
    }

    private void testSplitter() {
        UUID id = spawnChampion(EntityTypes.SLIME, "splitter,sticky", 5.0);
        server.runOnServer(s -> {
            Slime slime = (Slime) champion(s, id);
            slime.setSize(4, true);
            slime.hurtServer(level(s), level(s).damageSources().genericKill(), 10000.0f);
        });
        context.waitTicks(30);
        server.runOnServer(s -> {
            List<Slime> pieces = nearby(s, Slime.class);
            check(pieces.size() >= 4, "splitter: only " + pieces.size() + " pieces (expected at least 4)");
            for (Slime piece : pieces) {
                IChampions champion = (IChampions) piece;
                String affixes = champion.champions$getAffixesString();
                if (affixes.isEmpty()) {
                    check(neverChampion(piece), "splitter: a piece without an affix can still become a champion");
                } else {
                    check(affixes.equals("sticky"), "splitter: a piece inherited '" + affixes + "' (Splitter must not chain)");
                    check(champion.champions$getChampionTier() == 1, "splitter: a sticky piece is tier " + champion.champions$getChampionTier());
                    check(!champion.champions$dropsChampionLoot(), "splitter: a piece drops champion loot");
                }
            }
        });
    }

    private void testChampionLoot() {
        // Spiders: their own drops (string, spider eyes) don't overlap with champion loot
        UUID normal = spawnChampion(EntityTypes.SPIDER, "hasty", 3.0, 1);
        UUID noLoot = spawnChampion(EntityTypes.SPIDER, "hasty", -3.0, 1);
        server.runOnServer(s -> {
            ((IChampions) champion(s, noLoot)).champions$setDropsChampionLoot(false);
            for (UUID id : List.of(normal, noLoot)) {
                champion(s, id).hurtServer(level(s), level(s).damageSources().playerAttack(player(s)), 10000.0f);
            }
        });
        context.waitTicks(30);
        server.runOnServer(s -> {
            List<ItemEntity> championLoot = nearby(s, ItemEntity.class).stream()
                    .filter(item -> !item.getItem().is(Items.STRING) && !item.getItem().is(Items.SPIDER_EYE)).toList();
            check(!championLoot.isEmpty(), "champion loot: the tier 1 champion dropped no champion loot");
            check(championLoot.stream().allMatch(item -> item.getX() > px), "champion loot: the champion that shouldn't drop loot did");
        });
    }

    /** Rolls each tier's loot table many times and checks book strength scales with the tier. */
    private void testLootTables() {
        server.runOnServer(s -> {
            Mob mob = create(s, EntityTypes.SPIDER, 3.0);
            int[] maxLevel = new int[6];
            boolean[] treasure = new boolean[6];
            int[] books = new int[6];
            for (int tier = 1; tier <= 5; tier++) {
                LootTable table = s.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE,
                        Identifier.fromNamespaceAndPath("champions", "champions/tier_" + tier)));
                for (int i = 0; i < 500; i++) {
                    LootParams params = new LootParams.Builder(level(s))
                            .withParameter(LootContextParams.THIS_ENTITY, mob)
                            .withParameter(LootContextParams.ORIGIN, mob.position())
                            .withParameter(LootContextParams.DAMAGE_SOURCE, level(s).damageSources().playerAttack(player(s)))
                            .create(LootContextParamSets.ENTITY);
                    for (ItemStack stack : table.getRandomItems(params)) {
                        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
                        if (stored == null || stored.isEmpty()) continue;
                        books[tier]++;
                        for (var entry : stored.entrySet()) {
                            maxLevel[tier] = Math.max(maxLevel[tier], entry.getIntValue());
                            if (entry.getKey().is(EnchantmentTags.TREASURE)) treasure[tier] = true;
                        }
                    }
                }
            }
            log("loot books per 500 kills (tier 1-5): " + books[1] + ", " + books[2] + ", " + books[3] + ", " + books[4] + ", " + books[5]
                    + "; highest enchantment level: " + maxLevel[1] + ", " + maxLevel[2] + ", " + maxLevel[3] + ", " + maxLevel[4] + ", " + maxLevel[5]);
            check(books[1] > 10 && books[1] < 100, "loot: tier 1 dropped " + books[1] + " books in 500 kills (expected about 50)");
            check(maxLevel[1] <= 2, "loot: a tier 1 book had a level " + maxLevel[1] + " enchantment");
            for (int tier = 1; tier <= 4; tier++) {
                check(!treasure[tier], "loot: a tier " + tier + " book had a treasure enchantment");
            }
            check(maxLevel[4] >= 3, "loot: tier 4 books never above level " + maxLevel[4]);
            check(books[5] == 1000, "loot: tier 5 should always drop 2 books, got " + books[5] + " in 500");
            check(treasure[5], "loot: tier 5 books never had a treasure enchantment");
        });
    }

    private void testSticky() throws ReflectiveOperationException {
        UUID id = spawnChampion(EntityTypes.SLIME, "sticky", 1.2);
        server.runOnServer(s -> {
            Slime slime = (Slime) champion(s, id);
            slime.setSize(2, true);
            ServerPlayer player = resetPlayer(s);
            // Slimes attack by contact (dealDamage), which goes through the cube-mob hook
            Method dealDamage = AbstractCubeMob.class.getDeclaredMethod("dealDamage", LivingEntity.class);
            dealDamage.setAccessible(true);
            dealDamage.invoke(slime, player);
            check(player.getHealth() < player.getMaxHealth(), "sticky: slime contact did not hurt the player");
            check(player.hasEffect(MobEffects.SLOWNESS) && player.getEffect(MobEffects.SLOWNESS).getAmplifier() == 3,
                    "sticky: player not heavily slowed");
        });
    }

    private void testWarlord() {
        UUID warlord = spawnChampion(EntityTypes.PILLAGER, "warlord", 5.0);
        UUID ally = spawnPlain(EntityTypes.VINDICATOR, 8.0);
        server.runOnServer(s -> {
            Mob mob = champion(s, warlord);
            mob.tickCount = 20;
            affix("warlord").onTick(mob);
            check(champion(s, ally).hasEffect(MobEffects.STRENGTH), "warlord: nearby illager has no Strength");
            List<net.minecraft.world.entity.monster.illager.AbstractIllager> illagers = nearby(s, net.minecraft.world.entity.monster.illager.AbstractIllager.class);
            int followers = illagers.size() - 2;
            check(followers >= 2 && followers <= 3, "warlord: expected 2-3 extra illagers, found " + followers);
            illagers.stream().filter(i -> i != mob && i != champion(s, ally))
                    .forEach(i -> check(neverChampion(i), "warlord: a follower can become a champion"));
        });
    }

    private void testBerserker() throws ReflectiveOperationException {
        UUID id = spawnChampion(EntityTypes.VINDICATOR, "berserker", 5.0);
        server.runOnServer(s -> {
            Mob vindicator = champion(s, id);
            MeleeAttackGoal goal = null;
            for (WrappedGoal wrapped : vindicator.getGoalSelector().getAvailableGoals()) {
                if (wrapped.getGoal() instanceof MeleeAttackGoal melee) goal = melee;
            }
            check(goal != null, "berserker: vindicator has no melee goal");
            Method reset = MeleeAttackGoal.class.getDeclaredMethod("resetAttackCooldown");
            reset.setAccessible(true);
            Field ticks = MeleeAttackGoal.class.getDeclaredField("ticksUntilNextAttack");
            ticks.setAccessible(true);

            vindicator.setHealth(vindicator.getMaxHealth());
            reset.invoke(goal);
            int fullHealth = ticks.getInt(goal);
            vindicator.setHealth(vindicator.getMaxHealth() * 0.1f);
            reset.invoke(goal);
            int lowHealth = ticks.getInt(goal);
            check(lowHealth < fullHealth * 0.6, "berserker: cooldown " + lowHealth + " at low health vs " + fullHealth + " at full");
        });
    }

    private void testSunproof() {
        server.runCommand("time set noon");
        UUID proof = spawnChampion(EntityTypes.ZOMBIE, "sunproof", 4.0);
        UUID proofSkeleton = spawnChampion(EntityTypes.SKELETON, "sunproof", -4.0);
        UUID normal = spawnPlain(EntityTypes.ZOMBIE, 8.0);
        server.runOnServer(s -> {
            for (UUID id : List.of(proof, proofSkeleton, normal)) {
                champion(s, id).setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            }
        });
        server.waitFor(s -> champion(s, normal).isOnFire(), 15 * SECOND);
        context.waitTicks(3 * SECOND);
        server.runOnServer(s -> {
            check(!champion(s, proof).isOnFire(), "sunproof: zombie burned in daylight");
            check(!champion(s, proofSkeleton).isOnFire(), "sunproof: skeleton burned in daylight");
        });
        server.runCommand("time set night");
    }

    private void testCommands() {
        server.runCommand("champion demo sniper");
        server.runCommand("champion spawn minecraft:creeper stalker big");
        server.runCommand("champion spawn minecraft:pig sniper");
        context.waitTicks(5);
        server.runOnServer(s -> {
            List<AbstractSkeleton> skeletons = nearby(s, AbstractSkeleton.class);
            check(skeletons.size() == 1 && ((IChampions) skeletons.getFirst()).champions$getAffixesString().equals("sniper"),
                    "commands: /champion demo sniper did not spawn a sniper skeleton");
            List<Creeper> creepers = nearby(s, Creeper.class);
            check(creepers.size() == 1, "commands: /champion spawn creeper did not spawn one creeper");
            IChampions creeper = (IChampions) creepers.getFirst();
            // Stalker (3 slots) + Big (1 slot) = 4 slots, so the champion is tier 4
            check(creeper.champions$getChampionTier() == 4 && creeper.champions$hasAffix("stalker") && creeper.champions$hasAffix("big"),
                    "commands: creeper has tier " + creeper.champions$getChampionTier() + " and '" + creeper.champions$getAffixesString() + "'");
            check(nearby(s, Pig.class).isEmpty(), "commands: spawned a pig with a skeleton-only affix");
        });
    }

    // --- Tiers and difficulty

    private void testAffixSlots() {
        server.runOnServer(s -> {
            Zombie zombie = EntityTypes.ZOMBIE.create(level(s), EntitySpawnReason.COMMAND);
            boolean tier2Double = false, tier2Single = false, tier5GotFour = false;
            for (int i = 0; i < 400; i++) {
                for (int tier = 1; tier <= 5; tier++) {
                    ChampionRank rank = ChampionRank.RANKS.get(tier);
                    List<String> names = List.of(PrepareChampions.prepareAffixes(rank, zombie).split(","));
                    int used = names.stream().mapToInt(name -> affix(name).getSlots()).sum();
                    check(used <= rank.slots(), "affix slots: tier " + tier + " used " + used + " of " + rank.slots() + " slots " + names);
                    check(used == rank.slots(), "affix slots: tier " + tier + " left slots empty " + names);
                    if (tier == 2 && names.size() == 2) tier2Double = true;
                    if (tier == 2 && names.size() == 1) tier2Single = true;
                    if (tier == 5 && names.stream().anyMatch(name -> affix(name).getSlots() == 4)) tier5GotFour = true;
                }
            }
            check(tier2Double && tier2Single, "affix slots: tier 2 should get either two 1-slot or one 2-slot affix");
            check(tier5GotFour, "affix slots: tier 5 champions never got a 4-slot affix");
        });
    }

    private void testGameDifficulty() {
        double[] easy = new double[6], normal = new double[6], hard = new double[6];
        for (String mode : List.of("easy", "normal", "hard")) {
            server.runCommand("difficulty " + mode);
            context.waitTicks(2);
            double[] shares = server.computeOnServer(s -> {
                double[] weights = ChampionRank.tierWeights(0.0, 1.0, 8.0, Integer.MAX_VALUE, tier -> DifficultyModes.tierMultiplier(level(s), tier));
                double total = 0;
                for (double weight : weights) total += weight;
                double[] result = new double[weights.length];
                for (int i = 0; i < weights.length; i++) result[i] = weights[i] / total;
                return result;
            });
            double cap = server.computeOnServer(s -> DifficultyModes.cap(level(s)));
            double growth = server.computeOnServer(s -> DifficultyModes.growth(level(s)));
            switch (mode) {
                case "easy" -> { easy = shares; check(cap == 0.75 && growth == 0.5, "difficulty: easy multipliers " + growth + "/" + cap); }
                case "normal" -> { normal = shares; check(cap == 1.0 && growth == 1.0, "difficulty: normal multipliers " + growth + "/" + cap); }
                default -> { hard = shares; check(cap == 1.5 && growth == 1.5, "difficulty: hard multipliers " + growth + "/" + cap); }
            }
        }
        for (int tier = 1; tier <= 5; tier++) {
            check(easy[tier] < normal[tier] && normal[tier] < hard[tier], "difficulty: tier " + tier + " odds don't rise with difficulty");
        }
        // Higher tiers scale more: tier 5 grows more from Normal to Hard than tier 1 does
        check(hard[5] / normal[5] > hard[1] / normal[1] * 1.5, "difficulty: higher tiers don't scale more on Hard");
        server.runCommand("difficulty normal");
    }

    // --- Helpers

    private UUID spawnChampion(EntityType<? extends Mob> type, String affixes, double offsetX) {
        return spawnChampion(type, affixes, offsetX, 2);
    }

    /** Spawns a champion with exactly these affixes, offset along x from the player, targeting the player. */
    private UUID spawnChampion(EntityType<? extends Mob> type, String affixes, double offsetX, int tier) {
        return server.computeOnServer(s -> {
            Mob mob = create(s, type, offsetX);
            IChampions champion = (IChampions) mob;
            champion.champions$setChampionTier(tier);
            champion.champions$setAffixesString(affixes);
            champion.champions$getActiveAffixes().forEach(affix -> affix.onApply(mob));
            level(s).addFreshEntity(mob);
            mob.setHealth(mob.getMaxHealth());
            mob.setTarget(player(s));
            return mob.getUUID();
        });
    }

    /** A normal (non-champion) mob. */
    private UUID spawnPlain(EntityType<? extends Mob> type, double offsetX) {
        return server.computeOnServer(s -> {
            Mob mob = create(s, type, offsetX);
            ((IChampions) mob).champions$setChampionTier(IChampions.NEVER_CHAMPION);
            level(s).addFreshEntity(mob);
            return mob.getUUID();
        });
    }

    private Mob create(MinecraftServer s, EntityType<? extends Mob> type, double offsetX) {
        Mob mob = type.create(level(s), EntitySpawnReason.COMMAND);
        mob.snapTo(px + offsetX, py, pz, 90.0F, 0.0F);
        mob.finalizeSpawn(level(s), level(s).getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.COMMAND, null);
        mob.setNoAi(true);
        return mob;
    }

    private AbstractArrow shoot(MinecraftServer s, AbstractSkeleton skeleton) {
        List<AbstractArrow> before = nearby(s, AbstractArrow.class);
        skeleton.performRangedAttack(player(s), 1.0F);
        return nearby(s, AbstractArrow.class).stream().filter(arrow -> !before.contains(arrow)).findFirst()
                .orElseThrow(() -> new AssertionError("AFFIX TEST FAILED - " + skeleton.getType() + " did not shoot"));
    }

    /** Damage actually taken from one hit, ignoring the hurt cooldown. */
    private static float hit(Mob mob, DamageSource source, float amount) {
        mob.damageCooldownTime = 0;
        mob.setHealth(mob.getMaxHealth());
        float before = mob.getHealth();
        mob.hurtServer((ServerLevel) mob.level(), source, amount);
        return before - mob.getHealth();
    }

    /** Removes every entity except the player, and resets the player to where the test started. */
    private void clearArea() {
        server.runOnServer(s -> {
            for (Entity entity : level(s).getEntitiesOfClass(Entity.class, around(s), e -> !(e instanceof ServerPlayer))) {
                entity.discard();
            }
            ServerPlayer player = resetPlayer(s);
            player.removeAllEffects();
            player.clearFire();
            player.setTicksFrozen(0);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            player.teleportTo(px, py, pz);
        });
        context.waitTicks(2);
    }

    private ServerPlayer resetPlayer(MinecraftServer s) {
        ServerPlayer player = player(s);
        player.setHealth(player.getMaxHealth());
        player.damageCooldownTime = 0;
        player.setDeltaMovement(0, 0, 0);
        return player;
    }

    private boolean neverChampion(Mob mob) {
        return ((IChampions) mob).champions$getChampionTier() == IChampions.NEVER_CHAMPION;
    }

    private static Affix affix(String name) {
        Affix affix = AffixRegistry.ALL_AFFIXES.get(name);
        check(affix != null, name + ": not registered");
        return affix;
    }

    private List<ShulkerBullet> bullets(MinecraftServer s) {
        return nearby(s, ShulkerBullet.class);
    }

    private <T extends Entity> List<T> nearby(MinecraftServer s, Class<T> type) {
        return level(s).getEntitiesOfClass(type, around(s), Entity::isAlive);
    }

    private AABB around(MinecraftServer s) {
        return new AABB(px - 40, py - 20, pz - 40, px + 40, py + 20, pz + 40);
    }

    private static Mob champion(MinecraftServer s, UUID id) {
        Entity entity = level(s).getEntity(id);
        check(entity instanceof Mob, "entity " + id + " is gone");
        return (Mob) entity;
    }

    private static ServerPlayer player(MinecraftServer s) {
        return s.getPlayerList().getPlayers().getFirst();
    }

    private static ServerLevel level(MinecraftServer s) {
        return player(s).level();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("AFFIX TEST FAILED - " + message);
        }
    }

    private void log(String message) {
        System.out.println("AFFIX TEST: " + message);
    }
}
