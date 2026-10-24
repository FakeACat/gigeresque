package mods.cybercat.gigeresque.common.entity;

import net.minecraft.core.BlockPos;
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

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.Log;
import mods.cybercat.gigeresque.common.Simple;

public final class Alien extends Monster {
    public final Pathfinder pathfinder = new Pathfinder();

    public enum Movement {
        RUNNING,
        CLIMBING,
    }

    public static final EntityDataAccessor<Integer> MOVEMENT = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.INT);
    public Movement movement = Movement.RUNNING;

    public static final float ROTATION_SMOOTHING = .8f;

    public static final EntityDataAccessor<Vector3f> FORWARD = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.VECTOR3);
    public final Vector3f forward = new Vector3f();
    public final Vector3f clientPreviousForward = new Vector3f();

    public static final EntityDataAccessor<Vector3f> UP = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.VECTOR3);
    public final Vector3f up = new Vector3f();
    public final Vector3f clientPreviousUp = new Vector3f();

    public static final EntityDataAccessor<Float> VERTICAL_OFFSET = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.FLOAT);
    public float verticalOffset;
    public float clientPreviousVerticalOffset;

    public BlockPos currentPathfindingTarget;

    public Alien(EntityType<Alien> type, Level level) {
        super(type, level);

        if (level instanceof ServerLevel serverLevel) {
            pathfinder.level = serverLevel;

            pathfinder.standingWidth = getBbWidth();
            pathfinder.standingHeight = getBbHeight();

            pathfinder.canClimb = true;
            pathfinder.climbingWidth = 0.8f;
            pathfinder.climbingHeight = 0.8f;
        }
    }

    public void serverSync() {
        assert !level().isClientSide;

        entityData.set(MOVEMENT, movement.ordinal());
        entityData.set(FORWARD, forward, true);
        entityData.set(UP, up, true);
        entityData.set(VERTICAL_OFFSET, verticalOffset);
    }

    public void clientSync() {
        assert level().isClientSide;

        movement = Movement.values()[entityData.get(MOVEMENT)];
        forward.set(entityData.get(FORWARD));
        up.set(entityData.get(UP));
        verticalOffset = entityData.get(VERTICAL_OFFSET);
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(MOVEMENT, 0);
        builder.define(FORWARD, new Vector3f(1, 0, 0));
        builder.define(UP, new Vector3f(0, 1, 0));
        builder.define(VERTICAL_OFFSET, 0f);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            clientPreviousForward.set(forward);
            clientPreviousUp.set(up);
            clientPreviousVerticalOffset = verticalOffset;

            clientSync();

            forward.mul(ROTATION_SMOOTHING).add(new Vector3f(clientPreviousForward).mul(1 - ROTATION_SMOOTHING));
            up.mul(ROTATION_SMOOTHING).add(new Vector3f(clientPreviousUp).mul(1 - ROTATION_SMOOTHING));

            verticalOffset *= ROTATION_SMOOTHING;
            verticalOffset += clientPreviousVerticalOffset * (1 - ROTATION_SMOOTHING);
            return;
        }

        assert pathfinder.level == level();

        var player = level().getNearestPlayer(this, 100);
        if (player != null) navigateTo(player.blockPosition());

        // decide how we should be moving right now
        {
            var currentNavigation = pathfinder.pickNavigationTypeAt(getBlockX(), getBlockY(), getBlockZ());
            var nextNavigation = !pathfinder.path.isEmpty() && pathfinder.status == Pathfinder.Status.DONE
                ? pathfinder.path.peek().navigation
                : currentNavigation;

            switch (currentNavigation) {
                case NOT_NAVIGABLE -> movement = Movement.RUNNING;
                case WALKABLE -> {
                    switch (nextNavigation) {
                        case NOT_NAVIGABLE -> movement = Movement.RUNNING;
                        case WALKABLE -> movement = Movement.RUNNING;
                        case CLIMBABLE -> movement = Movement.CLIMBING;
                        case null -> {}
                    }
                }
                case CLIMBABLE -> movement = Movement.CLIMBING;
                case null -> {}
            }
        }

        // move based on movement mode
        switch (movement) {
            case RUNNING -> {
                setNoGravity(false);

                if (getDeltaMovement().x != 0 || getDeltaMovement().z != 0) {
                    forward.set(getDeltaMovement().x, 0, getDeltaMovement().z).normalize();
                }

                up.set(0, 1, 0);
            }
            case CLIMBING -> {
                setNoGravity(true);

                if (getDeltaMovement().lengthSqr() != 0) {
                    forward.set(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z).normalize();
                }

                var closestBlockCollision = Simple.closestBlockCollision(
                    level(),
                    position().add(0, getBbHeight() / 2, 0),
                    pathfinder.climbingReach + 1,
                    0.2
                );
                if (closestBlockCollision == null) {
                    setNoGravity(false);
                    Log.warn("unable to find closest block while climbing");
                } else {
                    var offset = position().subtract(closestBlockCollision);
                    up.set(offset.x, offset.y, offset.z).normalize();

                    var idealDistanceFromBlocks = (pathfinder.climbingWidth + pathfinder.climbingReach) / 2;
                    var distanceFromBlocks = offset.length();

                    var pullStrength = 0.1;

                    var distanceFromIdealDistanceFromBlocks = Math.abs(distanceFromBlocks - idealDistanceFromBlocks);
                    if (distanceFromIdealDistanceFromBlocks < pullStrength) {
                        pullStrength = distanceFromIdealDistanceFromBlocks;
                    }

                    if (distanceFromBlocks > idealDistanceFromBlocks) pullStrength *= -1;

                    setDeltaMovement(getDeltaMovement().scale(0.9).add(up.x * pullStrength, up.y * pullStrength, up.z * pullStrength));
                }
            }
            case null -> {}
        }

        serverSync();
    }

    public void navigateTo(BlockPos exactPos) {
        var allowedErrorForPathfindingTarget = Math.sqrt(blockPosition().distSqr(exactPos)) / 3;
        if (currentPathfindingTarget == null || Math.sqrt(exactPos.distSqr(currentPathfindingTarget)) > allowedErrorForPathfindingTarget) {
            currentPathfindingTarget = exactPos;
            pathfinder.status = Pathfinder.Status.FAILED;
        }

        var level = (ServerLevel) level();
        pathfinder.distanceFunction = (x, y, z) -> (int) Math.sqrt(currentPathfindingTarget.distToCenterSqr(x + .5, y + .5, z + .5));

        pathfinder.workOnPath(getBlockX(), getBlockY(), getBlockZ());

        // finish path if exact target was already closed
        if (pathfinder.status == Pathfinder.Status.RUNNING) {
            var maybeNodeAtExactPos = pathfinder.closedNodes.get(Simple.hash(exactPos.getX(), exactPos.getY(), exactPos.getZ()));
            if (maybeNodeAtExactPos != null) pathfinder.finishAt(maybeNodeAtExactPos);
        }

        if (pathfinder.status != Pathfinder.Status.DONE || pathfinder.path.isEmpty()) return;

        var nextNode = pathfinder.path.peek();

        if (pathfinder.pickNavigationTypeAt(nextNode.x, nextNode.y, nextNode.z) != nextNode.navigation) {
            pathfinder.status = Pathfinder.Status.FAILED;
        }

        if (CommonMod.config.generalConfigs.enableDevparticles) for (var node : pathfinder.path) {
            level.sendParticles(ParticleTypes.BUBBLE, node.x + 0.5, node.y + 0.5, node.z + 0.5, 1, 0, 0, 0, 0);
        }

        switch (movement) {
            case RUNNING -> {
                var nodePosition = new Vec3(nextNode.x + .5, nextNode.y, nextNode.z + .5);
                var distanceSquaredToNextNode = distanceToSqr(nodePosition);

                if (distanceSquaredToNextNode > 8) pathfinder.status = Pathfinder.Status.FAILED;
                if (distanceSquaredToNextNode < .25) pathfinder.path.pop();

                var movementAttribute = getAttribute(Attributes.MOVEMENT_SPEED);
                assert movementAttribute != null;
                var speed = movementAttribute.getValue();

                var direction = nodePosition.subtract(position()).multiply(1, 0, 1).normalize();
                var directionScaled = direction.scale(speed);
                setDeltaMovement(directionScaled.x, getDeltaMovement().y, directionScaled.z);
            }
            case CLIMBING -> {
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
                setDeltaMovement(direction.scale(speed));
            }
            case null -> {}
        }
    }
}
