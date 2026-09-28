package crystal.champions.client.render;

import crystal.champions.client.mixin.ClientWorldAccessor;
import crystal.champions.client.net.ChampionDisplayInfo;
import crystal.champions.config.ChampionsConfigClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Map;
import java.util.UUID;

import static crystal.champions.client.net.ClientPacket.activeChampions;
import static crystal.champions.client.net.ClientPacket.activeChampionsCl;
import static crystal.champions.client.render.ChampionsColor.getColor;
import static crystal.champions.client.render.ChampionsRender.renderChampion;

public abstract class ChampionHudRender implements HudRenderCallback {

    private UUID targetUuid = null;
    private long lastUpdateAt = 0;

    /**
     * Отрисовка на клиент сайде
     *
     * @param context Здесь мы рисуем все что до этого сделали
     */
    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        final float delta = tickCounter.getTickDelta(true);

        if (client.player == null || client.options.hudHidden) return;
        ChampionData bestChampion = findBestChampion(client, delta);
        ChampionData bestChampionCl = findBestChampionCl(client, delta);

        final int cX = client.getWindow().getScaledWidth() / 2;
        final int y = 12;

        if (bestChampionCl != null) { renderChampion(context, cX, y, bestChampionCl, client); }
        else if (bestChampion != null) { renderChampion(context, cX, y, bestChampion, client); }
    }

    /**
     * When looking render
     */
    private ChampionData findBestChampionCl(MinecraftClient client, float delta) {
        ChampionsConfigClient config = ChampionsConfigClient.get();

        final long now = System.currentTimeMillis();
        ClientLook at = performRaycast(client, delta);

        if (at != null && at.check() && activeChampionsCl.containsKey(at.uuid())) {
            targetUuid = at.uuid();
            lastUpdateAt = now;
        }

        if (targetUuid != null) {
            ChampionDisplayInfo info = activeChampionsCl.get(targetUuid);
            if (info != null && (now - lastUpdateAt < config.cacheClient) && info.health() > 0) {
                return dataWrite(info);
            } else {
                activeChampionsCl.remove(targetUuid);
                targetUuid = null;
            }
        }
        return null;
    }

    /**
     * Box render
     */
    private ChampionData findBestChampion(MinecraftClient client, float delta) {
        ChampionsConfigClient config = ChampionsConfigClient.get();

        if (config.onlyForView || client.world == null || client.player == null) return null;
        ChampionData best = null;
        final long now = System.currentTimeMillis();

        for (Map.Entry<UUID, ChampionDisplayInfo> entry : activeChampions.entrySet()) {
            UUID uuid = entry.getKey();
            ChampionDisplayInfo info = entry.getValue();
            Entity targetEntity = ((ClientWorldAccessor) client.world).getEntityManager().getLookup().get(uuid);

            final boolean cache = now - info.lastUpdate() > config.cacheServer;
            final boolean falseRaycast = !performRaycastPos(client, targetEntity, delta);
            final boolean alwaysRender = config.alwaysRenderBox;
            final boolean alive = info.health() <= 0;

            if (cache || alive || (falseRaycast && !alwaysRender)) {
                activeChampions.remove(entry.getKey());
                continue;
            }

            if (info.tier() > 10) {
                best = dataWriteBoss(info);
            } else if (best == null || info.tier() > best.tier()) {
                best = dataWrite(info);
            }
        }
        return best;
    }

    /**
     * Use when box
     */
    private boolean performRaycastPos(MinecraftClient client, Entity target, float delta) {
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null || client.world == null) return false;
        if (target == null) return false;

        Vec3d startPos = cameraEntity.getCameraPosVec(delta);
        Vec3d endPos = target.getBoundingBox().getCenter();
        BlockHitResult blockHit = client.world.raycast(new RaycastContext(
                startPos,
                endPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                cameraEntity
        ));

        if (blockHit.getType() != HitResult.Type.MISS) {
            final double blockDistSq = blockHit.getPos().squaredDistanceTo(startPos);
            final double entityDistSq = endPos.squaredDistanceTo(startPos);
            return blockDistSq >= entityDistSq;
        }
        return true;
    }

    /**
     * Use when looking
     */
    private ClientLook performRaycast(MinecraftClient client, float delta) {
        Entity camera = client.getCameraEntity();
        if (camera == null || client.world == null) return null;

        Vec3d pos = camera.getCameraPosVec(delta);
        Vec3d rotation = camera.getRotationVec(delta);
        Vec3d endPos = pos.add(rotation.x * 80.0, rotation.y * 80.0, rotation.z * 80.0);

        BlockHitResult blockHit = client.world.raycast(new RaycastContext(
                pos,
                endPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                camera
        ));

        final double sq = blockHit.getType() != HitResult.Type.MISS
                ? blockHit.getPos().squaredDistanceTo(pos)
                : 80.0 * 80.0;

        Box box = camera.getBoundingBox().stretch(rotation.multiply(80.0)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(
                camera,
                pos,
                endPos,
                box,
                entity -> !entity.isSpectator() && entity.canHit(),
                sq
        );

        if (entityHitResult != null && entityHitResult.getEntity() != null) {
            return new ClientLook(true, System.currentTimeMillis(), entityHitResult.getEntity().getUuid());
        }

        return new ClientLook(false, System.currentTimeMillis(), null);
    }

    // Records
    public record ChampionData(
            Text name,
            int tier,
            String affixes,
            float percent,
            int color
    ) { }

    private record ClientLook(
            boolean check,
            long lastUpdate,
            UUID uuid
    ) {}


    private ChampionData dataWrite(ChampionDisplayInfo info) {
        return new ChampionData(
                info.name(),
                info.tier(),
                info.affixes(),
                info.health() / info.maxHealth(),
                getColor(info.tier())
        );
    }

    private ChampionData dataWriteBoss(ChampionDisplayInfo info) {
        return new ChampionData(
                info.name(),
                info.tier() - 10,
                info.affixes(),
                info.health() / info.maxHealth(),
                getColor(info.tier() - 10)
        );
    }
}