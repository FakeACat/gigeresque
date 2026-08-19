package mods.cybercat.gigeresque.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;

import mods.cybercat.gigeresque.common.Log;

public final class Alien extends Monster {
    public final Pathfinder pathfinder = new Pathfinder();

    public enum Movement { STANDING, RUNNING }

    @Sync
    public Movement movement = Movement.STANDING;

    @Sync
    public final Vector3f forward = new Vector3f();
    @Sync
    public final Vector3f up = new Vector3f();

    public Alien(EntityType<Alien> type, Level level) {
        super(type, level);

        pathfinder.standingWidth = getBbWidth();
        pathfinder.standingHeight = getBbHeight();

        pathfinder.canClimb = true;
        pathfinder.climbingWidth = 0.8f;
        pathfinder.climbingHeight = 0.8f;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            clientSync();
            return;
        }

        var level = (ServerLevel) level();

        followPlayer: {
            movement = Movement.STANDING;

            var player = level().getNearestPlayer(this, 100);
            if (player == null) break followPlayer;

            // maybe can just be set in the constructor? need to check if entities are recreated or just moved when
            // changing dimensions
            pathfinder.level = level;

            pathfinder.distanceFunction = (x, y, z) -> (int) Math.sqrt(player.position().distanceToSqr(x + .5, y + .5, z + .5));

            pathfinder.workOnPath(getBlockX(), getBlockY(), getBlockZ());

            if (pathfinder.status != Pathfinder.Status.DONE || pathfinder.path.isEmpty()) break followPlayer;

            var nextNode = pathfinder.path.peek();

            switch (nextNode.navigation) {
                case NOT_NAVIGABLE -> Log.error("non-navigable node found in path");
                case WALKABLE -> {
                    movement = Movement.RUNNING;

                    var nodePosition = new Vec3(nextNode.x + .5, nextNode.y, nextNode.z + .5);
                    var distanceSquaredToNextNode = distanceToSqr(nodePosition);

                    if (distanceSquaredToNextNode > 8) pathfinder.status = Pathfinder.Status.FAILED;

                    var movementAttribute = getAttribute(Attributes.MOVEMENT_SPEED);
                    assert movementAttribute != null;

                    var speed = movementAttribute.getValue();

                    if (distanceSquaredToNextNode < .5 * .5) {
                        pathfinder.path.pop();
                    }

                    var nodeOffsetFlattenedNormalised = nodePosition.subtract(position()).multiply(1, 0, 1).normalize();

                    forward.set(nodeOffsetFlattenedNormalised.x, nodeOffsetFlattenedNormalised.y, nodeOffsetFlattenedNormalised.z);
                    up.set(0, 1, 0);

                    setDeltaMovement(nodeOffsetFlattenedNormalised.scale(speed));
                }
                case CLIMBABLE -> {
                    Log.error("climbing movement not yet implemented");
                }
            }
        }

        serverSync();
    }

    @SuppressWarnings("unchecked")
    public void serverSync() {
        assert !level().isClientSide;

        for (var synced : SYNCED_FIELDS) {
            try {
                if (synced.field.getType().isEnum()) {
                    entityData.set((EntityDataAccessor<Integer>) synced.accessor, ((Enum<?>) synced.field.get(this)).ordinal());
                } else if (synced.field.getType() == Vector3f.class) {
                    entityData.set((EntityDataAccessor<Vector3f>) synced.accessor, (Vector3f) synced.field.get(this));
                }
            } catch (IllegalAccessException e) {
                Log.error("forgot to make synced field '%s' public!", synced.field.getName());
            }
        }
    }

    public void clientSync() {
        assert level().isClientSide;

        for (var synced : SYNCED_FIELDS) {
            try {
                if (synced.field.getType().isEnum()) {
                    synced.field.set(this, synced.field.getType().getEnumConstants()[(int) entityData.get(synced.accessor)]);
                } else if (synced.field.getType() == Vector3f.class) {
                    var field = (Vector3f) synced.field.get(this);
                    field.set((Vector3f) entityData.get(synced.accessor));
                }
            } catch (IllegalAccessException e) {
                Log.error("forgot to make synced field '%s' public!", synced.field.getName());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        for (var synced : SYNCED_FIELDS) {
            if (synced.field.getType().isEnum()) {
                builder.define((EntityDataAccessor<Integer>) synced.accessor, 0);
            } else if (synced.field.getType() == Vector3f.class) {
                builder.define((EntityDataAccessor<Vector3f>) synced.accessor, new Vector3f());
            }
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Sync {}

    public record SyncedField(
        Field field,
        EntityDataAccessor<?> accessor
    ) {}
    public static final ArrayList<SyncedField> SYNCED_FIELDS = new ArrayList<>();

    static {
        for (var field : Alien.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Sync.class)) continue;

            EntityDataAccessor<?> accessor;

            if (field.getType().isEnum()) {
                accessor = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.INT);
            } else if (field.getType() == Vector3f.class) {
                accessor = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.VECTOR3);
            } else {
                throw new AssertionError(
                    "field '" + field.getName() + "' is of type '" + field.getType().getSimpleName() + "' which cannot be synced!"
                );
            }

            SYNCED_FIELDS.add(new SyncedField(field, accessor));
        }
    }
}
