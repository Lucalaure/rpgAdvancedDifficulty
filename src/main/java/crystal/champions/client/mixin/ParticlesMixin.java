package crystal.champions.client.mixin;

import crystal.champions.Champions;
import crystal.champions.client.net.ChampionDisplayInfo;
import crystal.champions.client.net.ClientPacket;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static crystal.champions.ChampionsColorServer.getColor;

@Mixin(LivingEntity.class)
public class ParticlesMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        UUID uuid = entity.getUUID();

        ChampionDisplayInfo info = ClientPacket.activeChampionsCl.get(uuid);

        if (info != null && entity.tickCount % 4 == 0) {
            spawnChampionParticles(entity, info.tier());
        }
    }

    @Unique
    private void spawnChampionParticles(LivingEntity entity, int tier) {
        int color = getColor(tier);

        entity.level().addParticle(
                Champions.CHAMPIONS_SPELL,
                entity.getRandomX(0.5),
                entity.getRandomY(),
                entity.getRandomZ(0.5),
                color,
                0.01,
                0
        );
    }
}