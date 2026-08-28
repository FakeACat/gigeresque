package mods.cybercat.gigeresque.common.entity;

import net.minecraft.core.particles.ParticleTypes;
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

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.Log;
import mods.cybercat.gigeresque.common.Simple;

public final class Alien extends Monster {
    public final Pathfinder pathfinder = new Pathfinder();

    public enum Movement { STANDING, RUNNING, CLIMBING }

    @Sync
    public Movement movement = Movement.STANDING;

    @Sync
    public final Vector3f forward = new Vector3f();
    @Sync
    public final Vector3f up = new Vector3f();

    @Sync
    public float verticalOffset;

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
            setNoGravity(false);

            var player = level().getNearestPlayer(this, 100);
            if (player == null) break followPlayer;

            pathfinder.level = level;
            pathfinder.distanceFunction = (x, y, z) -> (int) Math.sqrt(player.position().distanceToSqr(x + .5, y + .5, z + .5));

            pathfinder.workOnPath(getBlockX(), getBlockY(), getBlockZ());

            if (pathfinder.status != Pathfinder.Status.DONE || pathfinder.path.isEmpty()) break followPlayer;

            var nextNode = pathfinder.path.peek();

            if (pathfinder.pickNavigationTypeAt(nextNode.x, nextNode.y, nextNode.z) != nextNode.navigation) {
                pathfinder.status = Pathfinder.Status.FAILED;
            }

            if (CommonMod.config.generalConfigs.enableDevparticles) for (var node : pathfinder.path) {
                level.sendParticles(ParticleTypes.BUBBLE, node.x + 0.5, node.y + 0.5, node.z + 0.5, 1, 0, 0, 0, 0);
            }

            switch (nextNode.navigation) {
                case NOT_NAVIGABLE -> Log.error("non-navigable node found in path");
                case WALKABLE -> {
                    movement = Movement.RUNNING;

                    var nodePosition = new Vec3(nextNode.x + .5, nextNode.y, nextNode.z + .5);
                    var distanceSquaredToNextNode = distanceToSqr(nodePosition);

                    if (distanceSquaredToNextNode > 8) pathfinder.status = Pathfinder.Status.FAILED;
                    if (distanceSquaredToNextNode < .25) pathfinder.path.pop();

                    var movementAttribute = getAttribute(Attributes.MOVEMENT_SPEED);
                    assert movementAttribute != null;
                    var speed = movementAttribute.getValue();

                    var direction = nodePosition.subtract(position()).multiply(1, 0, 1).normalize();

                    forward.set(direction.x, direction.y, direction.z);
                    up.set(0, 1, 0);
                    verticalOffset = 0;

                    setDeltaMovement(direction.scale(speed));
                }
                case CLIMBABLE -> {
                    movement = Movement.CLIMBING;
                    setNoGravity(true);

                    var centre = position().add(0, getBbHeight() / 2, 0);
                    var nodePosition = new Vec3(nextNode.x + .5, nextNode.y + .5, nextNode.z + .5);
                    var distanceSquaredToNextNode = centre.distanceToSqr(nodePosition);

                    if (distanceSquaredToNextNode > 8) pathfinder.status = Pathfinder.Status.FAILED;
                    if (distanceSquaredToNextNode < .25) pathfinder.path.pop();

                    var closestBlockCollision = Simple.closestBlockCollision(level(), centre, pathfinder.climbingReach + 1, 0.2);

                    if (closestBlockCollision == null) {
                        pathfinder.status = Pathfinder.Status.FAILED;
                        return;
                    }

                    var movementAttribute = getAttribute(Attributes.MOVEMENT_SPEED);
                    assert movementAttribute != null;
                    var speed = movementAttribute.getValue();

                    var direction = nodePosition.subtract(centre).normalize();

                    forward.set(direction.x, direction.y, direction.z);

                    {
                        var collisionToCentre = centre.subtract(closestBlockCollision);
                        verticalOffset = (float) collisionToCentre.length();

                        var collisionToCentreDirection = centre.subtract(closestBlockCollision).normalize();
                        up.set(collisionToCentreDirection.x, collisionToCentreDirection.y, collisionToCentreDirection.z);
                    }

                    setDeltaMovement(direction.scale(speed));
                }
            }
        }

        serverSync();
    }

    @SuppressWarnings("unchecked")
    public void serverSync() {
        assert !level().isClientSide;

        for (var synced : SYNCED_FIELDS) try {
            if (synced.field.getType().isEnum()) {
                entityData.set((EntityDataAccessor<Integer>) synced.accessor, ((Enum<?>) synced.field.get(this)).ordinal());
            } else {
                entityData.set((EntityDataAccessor<Object>) synced.accessor, synced.field.get(this), true);
            }
        } catch (IllegalAccessException e) {
            Log.error("forgot to make synced field '%s' public!", synced.field.getName());
        }
    }

    public void clientSync() {
        assert level().isClientSide;

        for (var synced : SYNCED_FIELDS) try {
            if (synced.field.getType().isEnum()) {
                synced.field.set(this, synced.field.getType().getEnumConstants()[(int) entityData.get(synced.accessor)]);
            } else if (synced.field.getType() == Vector3f.class) {
                var field = (Vector3f) synced.field.get(this);
                field.set((Vector3f) entityData.get(synced.accessor));
            } else {
                synced.field.set(this, entityData.get(synced.accessor));
            }
        } catch (IllegalAccessException e) {
            Log.error("forgot to make synced field '%s' public!", synced.field.getName());
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
            } else if (synced.field.getType() == float.class) {
                builder.define((EntityDataAccessor<Float>) synced.accessor, 0f);
            } else {
                Log.error("no default value for type '%s'", synced.field.getType().getSimpleName());
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

            EntityDataAccessor<?> accessor = null;

            if (field.getType().isEnum()) {
                accessor = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.INT);
            } else if (field.getType() == Vector3f.class) {
                accessor = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.VECTOR3);
            } else if (field.getType() == float.class) {
                accessor = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.FLOAT);
            } else {
                Log.error("type '%s' cannot be synced!", field.getName(), field.getType().getSimpleName());
            }

            if (accessor != null) SYNCED_FIELDS.add(new SyncedField(field, accessor));
        }
    }
}
