package crystal.champions.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;

/**
 * Custom particles for champions
 */
public class ChampionsParticle extends SingleQuadParticle {
    private final SpriteSet spriteProvider;

    public ChampionsParticle(ClientLevel world, double x, double y, double z,
                             double velocityX, double velocityY, double velocityZ,
                             SpriteSet spriteProvider, int hexColor) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.first());

        this.spriteProvider = spriteProvider;
        this.lifetime = 40 + this.random.nextInt(10);

        this.yd = velocityY * 1.1D;

        this.rCol = (hexColor >> 16 & 255) / 255.0F;
        this.gCol = (hexColor >> 8 & 255) / 255.0F;
        this.bCol = (hexColor & 255) / 255.0F;

        try {
            this.setSpriteFromAge(spriteProvider);
        } catch (Exception e) {
            this.remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteProvider);
        this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }
}
