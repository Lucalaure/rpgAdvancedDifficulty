package crystal.champions.client.mixin;

import crystal.champions.Champions;
import crystal.champions.client.net.ChampionDisplayInfo;
import crystal.champions.client.net.ClientPacket;
import net.minecraft.entity.LivingEntity;
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
        UUID uuid = entity.getUuid();

        ChampionDisplayInfo info = ClientPacket.activeChampionsCl.get(uuid);

        if (info != null && entity.age % 4 == 0) {
            spawnChampionParticles(entity, info.tier());
        }
    }

    @Unique
    private void spawnChampionParticles(LivingEntity entity, int tier) {
        int color = getColor(tier);

        entity.getWorld().addParticle(
                Champions.CHAMPIONS_SPELL,
                entity.getParticleX(0.5),
                entity.getRandomBodyY(),
                entity.getParticleZ(0.5),
                color,
                0.01,
                0
        );
    }
}