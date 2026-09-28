package crystal.champions.client.render;

import crystal.champions.client.mixin.ClientWorldAccessor;
import crystal.champions.client.net.ChampionDisplayInfo;
import crystal.champions.config.ChampionsConfigClient;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

import static crystal.champions.client.net.ClientPacket.activeChampions;
import static crystal.champions.client.net.ClientPacket.activeChampionsCl;
import static crystal.champions.client.render.ChampionsColor.getColor;
import static crystal.champions.client.render.ChampionsRender.renderChampion;

public abstract class ChampionHudRender implements HudElement {

    private UUID targetUuid = null;
    private long lastUpdateAt = 0;

    /**
     * Отрисовка на клиент сайде
     *
     * @param context Здесь мы рисуем все что до этого сделали
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();

        final float delta = tickCounter.getGameTimeDeltaPartialTick(true);

        if (client.player == null || client.gui.hud.isHidden()) return;
        ChampionData bestChampion = findBestChampion(client, delta);
        ChampionData bestChampionCl = findBestChampionCl(client, delta);

        final int cX = context.guiWidth() / 2;
        final int y = 12;

        if (bestChampionCl != null) { renderChampion(context, cX, y, bestChampionCl, client); }
        else if (bestChampion != null) { renderChampion(context, cX, y, bestChampion, client); }
    }

    /**
     * When looking render
     */
    private ChampionData findBestChampionCl(Minecraft client, float delta) {
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
    private ChampionData findBestChampion(Minecraft client, float delta) {
        ChampionsConfigClient config = ChampionsConfigClient.get();

        if (config.onlyForView || client.level == null || client.player == null) return null;
        ChampionData best = null;
        final long now = System.currentTimeMillis();

        for (Map.Entry<UUID, ChampionDisplayInfo> entry : activeChampions.entrySet()) {
            UUID uuid = entry.getKey();
            ChampionDisplayInfo info = entry.getValue();
            Entity targetEntity = ((ClientWorldAccessor) client.level).getEntityManager().getEntityGetter().get(uuid);

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
    private boolean performRaycastPos(Minecraft client, Entity target, float delta) {
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null || client.level == null) return false;
        if (target == null) return false;

        Vec3 startPos = cameraEntity.getEyePosition(delta);
        Vec3 endPos = target.getBoundingBox().getCenter();
        BlockHitResult blockHit = client.level.clip(new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                cameraEntity
        ));

        if (blockHit.getType() != HitResult.Type.MISS) {
            final double blockDistSq = blockHit.getLocation().distanceToSqr(startPos);
            final double entityDistSq = endPos.distanceToSqr(startPos);
            return blockDistSq >= entityDistSq;
        }
        return true;
    }

    /**
     * Use when looking
     */
    private ClientLook performRaycast(Minecraft client, float delta) {
        Entity camera = client.getCameraEntity();
        if (camera == null || client.level == null) return null;

        Vec3 pos = camera.getEyePosition(delta);
        Vec3 rotation = camera.getViewVector(delta);
        Vec3 endPos = pos.add(rotation.x * 80.0, rotation.y * 80.0, rotation.z * 80.0);

        BlockHitResult blockHit = client.level.clip(new ClipContext(
                pos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                camera
        ));

        final double sq = blockHit.getType() != HitResult.Type.MISS
                ? blockHit.getLocation().distanceToSqr(pos)
                : 80.0 * 80.0;

        AABB box = camera.getBoundingBox().expandTowards(rotation.scale(80.0)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                camera,
                pos,
                endPos,
                box,
                entity -> !entity.isSpectator() && entity.isPickable(),
                sq
        );

        if (entityHitResult != null && entityHitResult.getEntity() != null) {
            return new ClientLook(true, System.currentTimeMillis(), entityHitResult.getEntity().getUUID());
        }

        return new ClientLook(false, System.currentTimeMillis(), null);
    }

    // Records
    public record ChampionData(
            Component name,
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