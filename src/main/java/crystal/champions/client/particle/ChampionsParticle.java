package crystal.champions.client.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

/**
 * Custom particles for champions
 */
public class ChampionsParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    public ChampionsParticle(ClientWorld world, double x, double y, double z,
                             double velocityX, double velocityY, double velocityZ,
                             SpriteProvider spriteProvider, int hexColor) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.spriteProvider = spriteProvider;
        this.maxAge = 40 + this.random.nextInt(10);

        this.velocityY = velocityY * 1.1D;

        this.red = (hexColor >> 16 & 255) / 255.0F;
        this.green = (hexColor >> 8 & 255) / 255.0F;
        this.blue = (hexColor & 255) / 255.0F;

        try {
            this.setSpriteForAge(spriteProvider);
        } catch (Exception e) {
            this.markDead();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        this.alpha = 1.0f - ((float) this.age / (float) this.maxAge);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}