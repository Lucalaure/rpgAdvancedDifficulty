package crystal.champions.mixin;

import crystal.champions.IChampions;
import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class EntityDataMixin extends Entity implements IChampions {
    /**
     * Записываем сюда все данные для их использования в чемпионах
     * По названию и так понятно я думаю
     */
    protected EntityDataMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Unique private int tier = 0;
    @Unique private String affixes = "";
    @Unique private String type = "";
    @Unique private int adaptCount = 0;
    @Unique private boolean isShielding = false;
    @Unique private boolean dropsChampionLoot = true;

    @Override
    public boolean champions$dropsChampionLoot() {
        return dropsChampionLoot;
    }

    @Override
    public void champions$setDropsChampionLoot(boolean drops) {
        dropsChampionLoot = drops;
    }

    /**
     * Устанавливаем сеттеры и геттеры
     */
    @Override
    public int champions$getChampionTier() {
        return tier;
    }
    @Override
    public void champions$setChampionTier(int t) {
        tier = t;
    }
    // affixes
    @Override
    public String champions$getAffixesString() {
        return affixes;
    }
    @Override
    public void champions$setAffixesString(String a) {
        affixes = a;
    }
    // adaptive
    @Override
    public String champions$getAdaptationType() {
        return type;
    }
    @Override
    public void champions$setAdaptationType(String t) {
        type = t;
    }
    @Override
    public int champions$getAdaptation() {
        return adaptCount;
    }
    @Override
    public void champions$setAdaptation(int count) {
        adaptCount = count;
    }
    // shield
    @Override
    public boolean champions$isShielding() {
        return isShielding;
    }

    @Override
    public void champions$setShielding(boolean value) {
        isShielding = value;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onChampionTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.level().isClientSide()) {
            this.champions$getActiveAffixes().forEach(affix -> {
                affix.onTick(entity);
                if (entity instanceof Mob mob) affix.onAttack(entity, mob);
                if (entity.tickCount % 40 == 0) {
                    entity.addEffect(new MobEffectInstance(
                            MobEffects.SPEED, 40, tier / 3, false, false, false));
                }
            });
        }
    }

    // Array
    @Override
    public List<Affix> champions$getActiveAffixes() {
        String raw = affixes;
        if (raw.isEmpty()) return Collections.emptyList();
        return Arrays.stream(raw.split(","))
                .map(AffixRegistry.ALL_AFFIXES::get).filter(Objects::nonNull).toList();
    }


    // NBT
    @Unique private static final String KEY_TIER = "tier";
    @Unique private static final String KEY_AFFIXES = "affixes";
    @Unique private static final String KEY_ADAPTATION_TYPE = "adaptationType";
    @Unique private static final String KEY_ADAPTATION_COUNT = "adaptationCount";
    @Unique private static final String KEY_LOOT = "championLoot";


    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeChampionData(ValueOutput nbt, CallbackInfo ci) {
        nbt.putInt(KEY_TIER, this.champions$getChampionTier());
        nbt.putString(KEY_AFFIXES, this.champions$getAffixesString());

        nbt.putString(KEY_ADAPTATION_TYPE, this.champions$getAdaptationType());
        nbt.putInt(KEY_ADAPTATION_COUNT, this.champions$getAdaptation());
        // The champion loot tables match on this (see ChampionsLootTableRegister)
        nbt.putBoolean(KEY_LOOT, this.dropsChampionLoot);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readChampionData(ValueInput nbt, CallbackInfo ci) {
        nbt.getInt(KEY_TIER).ifPresent(this::champions$setChampionTier);
        nbt.getString(KEY_AFFIXES).ifPresent(this::champions$setAffixesString);
        nbt.getString(KEY_ADAPTATION_TYPE).ifPresent(this::champions$setAdaptationType);
        nbt.getInt(KEY_ADAPTATION_COUNT).ifPresent(this::champions$setAdaptation);
        this.dropsChampionLoot = nbt.getBooleanOr(KEY_LOOT, true);
    }
}