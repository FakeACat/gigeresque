package mods.cybercat.gigeresque.common.entity;

import com.mojang.serialization.Dynamic;
import mod.azure.azurelib.common.util.MoveAnalysis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.Function;

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.Constants;
import mods.cybercat.gigeresque.common.block.GigBlocks;
import mods.cybercat.gigeresque.common.entity.helper.AnimationDispatcher;
import mods.cybercat.gigeresque.common.entity.helper.AzureTicker;
import mods.cybercat.gigeresque.common.entity.helper.AzureVibrationUser;
import mods.cybercat.gigeresque.common.entity.helper.GigCommonMethods;
import mods.cybercat.gigeresque.common.entity.helper.managers.AlienNavigationManager;
import mods.cybercat.gigeresque.common.entity.helper.managers.ClimbingManager;
import mods.cybercat.gigeresque.common.entity.helper.managers.CrawlingManager;
import mods.cybercat.gigeresque.common.entity.helper.managers.SearchingManager;
import mods.cybercat.gigeresque.common.entity.helper.managers.StasisManager;
import mods.cybercat.gigeresque.common.entity.impl.blood.BloodEntity;
import mods.cybercat.gigeresque.common.sound.GigSounds;
import mods.cybercat.gigeresque.common.source.GigDamageSources;
import mods.cybercat.gigeresque.common.status.effect.GigStatusEffects;
import mods.cybercat.gigeresque.common.tags.GigTags;
import mods.cybercat.gigeresque.common.util.DamageSourceUtils;
import mods.cybercat.gigeresque.interfacing.AbstractAlien;
import mods.cybercat.gigeresque.interfacing.AnimationSelector;

public abstract class AlienEntity extends Monster implements VibrationSystem, AbstractAlien {

    public enum BloodType {
        NONE,
        ACID,
        GOO
    }

    public record GrowthOptions(
        int maxGrowthTimeTicks,
        @Nullable Function<AlienEntity, AlienEntity> createNext,
        boolean spawnFullyGrown
    ) {

        public static final GrowthOptions NO_GROWTH = new GrowthOptions(0, null, true);

        public static GrowthOptions adult(float growthMultiplier) {
            return new GrowthOptions((int) ((float) 6000 / growthMultiplier), null, true);
        }

        public static GrowthOptions immature(float growthMultiplier, Function<AlienEntity, AlienEntity> createNext) {
            return new GrowthOptions((int) ((float) 6000 / growthMultiplier), createNext, false);
        }

    }

    public record Options(
        BloodType bloodType,
        int bloodDiameter,
        boolean canClimb,
        float slapItemChance,
        boolean healsOnHit,
        boolean canCrawl,
        GrowthOptions growth,
        boolean completelyBlocksMovement
    ) {

        public static Options standardAlien(
            int bloodDiameter,
            boolean canClimb,
            float slapItemChance,
            boolean canCrawl,
            GrowthOptions growthOptions
        ) {
            return new Options(
                BloodType.ACID,
                bloodDiameter,
                canClimb,
                slapItemChance,
                true,
                canCrawl,
                growthOptions,
                false
            );
        }

        public static Options gooMutant(
            int bloodDiameter,
            boolean canClimb,
            float slapItemChance,
            boolean healsOnHit
        ) {
            return new Options(
                CommonMod.config.entityConfigs.gooMutantBloodType,
                bloodDiameter,
                canClimb,
                slapItemChance,
                healsOnHit,
                false,
                GrowthOptions.NO_GROWTH,
                false
            );
        }

        public static Options neomorph(
            int bloodDiameter,
            float slapItemChance,
            boolean healsOnHit,
            GrowthOptions growthOptions
        ) {
            return new Options(
                CommonMod.config.entityConfigs.neomorphBloodType,
                bloodDiameter,
                false,
                slapItemChance,
                healsOnHit,
                false,
                growthOptions,
                false
            );
        }
    }

    // TODO(acats) make final
    public Options options;

    public static final EntityDataAccessor<BlockPos> HOME_BLOCKPOS = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BLOCK_POS
    );

    public static final EntityDataAccessor<Boolean> FLEEING_FIRE = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    public static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.INT
    );

    public static final EntityDataAccessor<Boolean> IS_STASIS = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    private static final EntityDataAccessor<Boolean> IS_CRAWLING = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    public static final EntityDataAccessor<Boolean> WAKING_UP = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    protected static final EntityDataAccessor<Integer> CLIENT_ANGER_LEVEL = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.INT
    );

    protected static final EntityDataAccessor<Integer> GROWTH_TIME_TICKS = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.INT
    );

    protected static final EntityDataAccessor<Boolean> IS_HISSING = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    protected static final EntityDataAccessor<Boolean> IS_SEARCHING = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    protected static final EntityDataAccessor<Boolean> IS_EXECUTION = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    protected static final EntityDataAccessor<Boolean> IS_HEADBITE = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    public static final EntityDataAccessor<Integer> STASIS_TICK = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.INT
    );

    private static final EntityDataAccessor<Boolean> IS_CLIMBING = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.BOOLEAN
    );

    private static final EntityDataAccessor<Vector3f> CLIMBING_FORWARD_DIR = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.VECTOR3
    );

    private static final EntityDataAccessor<Vector3f> CLIMBING_UP_DIR = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.VECTOR3
    );

    private static final EntityDataAccessor<Float> CLIMBING_DIST_FROM_BLOCK = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.FLOAT
    );

    public static final EntityDataAccessor<Float> CARRYING_DAMAGE = SynchedEntityData.defineId(
        AlienEntity.class,
        EntityDataSerializers.FLOAT
    );

    protected int delayBeforeEating = 0;

    protected boolean triggeredAttackAnimation = false;

    private final DynamicGameEventListener<Listener> dynamicGameEventListener;

    public int wakeupCounter = 0;

    protected final User vibrationUser;

    private Data vibrationData;

    public BlockPos savedNestWebCross;

    public SearchingManager searchingManager;

    public final AnimationDispatcher animationDispatcher;

    public final MoveAnalysis moveAnalysis;

    public final CrawlingManager crawlingManager;

    public final StasisManager stasisManager;

    // NOTE(acats) this is a little unintuitive so maybe worth changing? it is a functional interface that, when called,
    // is supposed to set the attacking animation to whatever is correct for the current context. there is probably a
    // better way to do this
    // TODO(acats) make final
    public AnimationSelector<AlienEntity> animationSelector;

    private final AlienNavigationManager navigationManager;

    private int healCounter;

    public final ClimbingManager climbingManager;

    // TODO(acats) remove once cocoons are added
    public boolean growsIntoRunner = false;

    public AlienEntity(
        EntityType<? extends AlienEntity> entityType,
        Level level,
        AnimationSelector<AlienEntity> animationSelector,
        Options options
    ) {
        super(entityType, level);
        this.noCulling = true;
        this.crawlingManager = new CrawlingManager(this, IS_CRAWLING);
        this.searchingManager = new SearchingManager(this, IS_SEARCHING);
        this.stasisManager = new StasisManager(this, IS_STASIS, STASIS_TICK);
        this.vibrationUser = new AzureVibrationUser(this);
        this.vibrationData = new Data();
        this.dynamicGameEventListener = new DynamicGameEventListener<>(new Listener(this));
        this.navigationManager = new AlienNavigationManager(this);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.climbingManager = new ClimbingManager(
            this,
            IS_CLIMBING,
            CLIMBING_FORWARD_DIR,
            CLIMBING_UP_DIR,
            CLIMBING_DIST_FROM_BLOCK
        );
        this.animationDispatcher = new AnimationDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
        this.animationSelector = animationSelector;
        this.options = options;
    }

    public static boolean checkMonsterSpawnRules(
        @NotNull EntityType<? extends Monster> type,
        ServerLevelAccessor level,
        @NotNull MobSpawnType spawnType,
        @NotNull BlockPos pos,
        @NotNull RandomSource random
    ) {
        return level.getDifficulty() != Difficulty.PEACEFUL
            && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(level, pos, random))
            && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor level, @NotNull BlockPos pos, RandomSource random) {
        if (level.getBrightness(LightLayer.SKY, pos) > random.nextInt(32)) {
            return false;
        } else {
            DimensionType dimensiontype = level.dimensionType();
            int i = dimensiontype.monsterSpawnBlockLightLimit();
            if (i < 15 && level.getBrightness(LightLayer.BLOCK, pos) > i) {
                return false;
            } else {
                int j = level.getLevel().isThundering()
                    ? level.getMaxLocalRawBrightness(
                        pos,
                        10
                    )
                    : level.getMaxLocalRawBrightness(pos);
                return j <= dimensiontype.monsterSpawnLightTest().sample(random);
            }
        }
    }

    @Override
    public float maxUpStep() {
        return 2.0f;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 150) {
            this.remove(RemovalReason.KILLED);
            super.tickDeath();
            this.dropExperience(this);
        }
    }

    @Override
    public int getMaxAirSupply() {
        return 4800;
    }

    @Override
    public boolean isFleeing() {
        return this.entityData.get(FLEEING_FIRE);
    }

    @Override
    public void setFleeingStatus(boolean fleeing) {
        this.entityData.set(FLEEING_FIRE, fleeing);
    }

    @Override
    public void setWakingUpStatus(boolean passout) {
        this.entityData.set(WAKING_UP, passout);
    }

    @Override
    public boolean isWakingUp() {
        return this.entityData.get(WAKING_UP);
    }

    @Override
    public boolean isExecuting() {
        return entityData.get(IS_EXECUTION);
    }

    @Override
    public void setIsExecuting(boolean isExecuting) {
        entityData.set(IS_EXECUTION, isExecuting);
    }

    @Override
    public boolean isBiting() {
        return entityData.get(IS_HEADBITE);
    }

    @Override
    public void setIsBiting(boolean isBiting) {
        entityData.set(IS_HEADBITE, isBiting);
    }

    @Override
    public boolean isHissing() {
        return entityData.get(IS_HISSING);
    }

    @Override
    public void setIsHissing(boolean isHissing) {
        entityData.set(IS_HISSING, isHissing);
    }

    public int growthTimeTicks() {
        return entityData.get(GROWTH_TIME_TICKS);
    }

    public void setGrowthTimeTicks(int ticks) {
        entityData.set(GROWTH_TIME_TICKS, ticks);
    }

    public float getCarryDamage() {
        return entityData.get(CARRYING_DAMAGE);
    }

    public void setCarryDamage(float damage) {
        entityData.set(CARRYING_DAMAGE, damage);
    }

    public BlockPos getHomeBlock() {
        return this.entityData.get(HOME_BLOCKPOS);
    }

    public void setHomeBlock(BlockPos pos) {
        this.entityData.set(HOME_BLOCKPOS, pos);
    }

    public boolean hasHomeBlock() {
        return !getHomeBlock().equals(BlockPos.ZERO);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLEEING_FIRE, false);
        builder.define(STATE, 0);
        builder.define(CLIENT_ANGER_LEVEL, 0);
        builder.define(GROWTH_TIME_TICKS, 0);
        builder.define(WAKING_UP, false);
        builder.define(IS_HISSING, false);
        builder.define(IS_EXECUTION, false);
        builder.define(IS_HEADBITE, false);
        // MOVED
        builder.define(IS_STASIS, false);
        builder.define(IS_SEARCHING, false);
        builder.define(IS_CRAWLING, false);
        builder.define(STASIS_TICK, 0);
        builder.define(HOME_BLOCKPOS, BlockPos.ZERO);
        builder.define(IS_CLIMBING, false);
        builder.define(CLIMBING_FORWARD_DIR, new Vector3f(0, 0, 0));
        builder.define(CLIMBING_UP_DIR, new Vector3f(0, 0, 0));
        builder.define(CLIMBING_DIST_FROM_BLOCK, 0.0f);
        builder.define(CARRYING_DAMAGE, 0.0f);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData)
            .resultOrPartial(
                CommonMod.LOGGER::error
            )
            .ifPresent(tag -> compound.put("listener", tag));
        compound.putInt("growthTimeTicks", growthTimeTicks());
        compound.putBoolean("wakingup", this.isWakingUp());
        compound.putBoolean("isHissing", this.isHissing());
        compound.putBoolean("isExecuting", this.isExecuting());
        compound.putBoolean("isHeadBite", this.isBiting());
        compound.putFloat("carry_damage", this.getCarryDamage());
        BlockPos homeBlock = this.getHomeBlock();
        if (homeBlock != null) {
            NbtUtils.writeBlockPos(homeBlock);
        }
        stasisManager.save(compound);
        searchingManager.save(compound);
        crawlingManager.save(compound);
        compound.putBoolean("growsIntoRunner", growsIntoRunner);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        crawlingManager.load(compound);
        searchingManager.load(compound);
        stasisManager.load(compound);
        if (compound.contains("listener", 10))
            Data.CODEC.parse(
                new Dynamic<>(NbtOps.INSTANCE, compound.getCompound("listener"))
            )
                .resultOrPartial(
                    CommonMod.LOGGER::error
                )
                .ifPresent(data -> this.vibrationData = data);
        setGrowthTimeTicks(compound.getInt("growthTimeTicks")); // TODO(acats) translate old value
        this.setIsHissing(compound.getBoolean("isHissing"));
        this.setIsBiting(compound.getBoolean(("isHeadBite")));
        this.setIsExecuting(compound.getBoolean("isExecuting"));
        this.setIsExecuting(compound.getBoolean("isHeadBite"));
        this.setWakingUpStatus(compound.getBoolean("wakingup"));
        this.setCarryDamage(compound.getFloat("carry_damage"));
        if (compound.contains("homeBlock")) {
            this.setHomeBlock(
                NbtUtils.readBlockPos(compound, "homeBlock").orElse(BlockPos.ZERO)
            );
        }
        growsIntoRunner = compound.getBoolean("growsIntoRunner"); // TODO(acats) translate old value (hostId)
    }

    @Override
    protected boolean canRide(@NotNull Entity vehicle) {
        return false;
    }

    @Override
    public int calculateFallDamage(float fallDistance, float damageMultiplier) {
        if (fallDistance <= 15)
            return 0;
        return super.calculateFallDamage(fallDistance, damageMultiplier);
    }

    @Override
    public int getMaxFallDistance() {
        return 9;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.setAirSupply(this.getMaxAirSupply());
        searchingManager.tick();
        stasisManager.tick();
        climbingManager.tick();

        this.setAirSupply(this.getMaxAirSupply());
        if (level() instanceof ServerLevel serverLevel && this.isAlive()) {
            if (CommonMod.config.generalConfigs.enablePeacefulModeRemoval && level().getDifficulty() == Difficulty.PEACEFUL) {
                this.remove(RemovalReason.DISCARDED);
            }

            if (growthTimeTicks() < options.growth.maxGrowthTimeTicks) {
                setGrowthTimeTicks(growthTimeTicks() + 1);
            } else if (options.growth.createNext == null) {
                // unique case where the growth multiplier is decreased so the alien ends up with a growth time greater
                // than the maximum
                setGrowthTimeTicks(options.growth.maxGrowthTimeTicks);
            } else {
                var newEntity = options.growth.createNext.apply(this);
                assert newEntity != null;
                newEntity.setPos(getX(), getY(), getZ());
                newEntity.growsIntoRunner = growsIntoRunner;
                level().addFreshEntity(newEntity);
                if (hasCustomName()) {
                    newEntity.setCustomName(getCustomName());
                }
                for (var effect : getActiveEffects()) {
                    addEffect(new MobEffectInstance(effect));
                }
                remove(Entity.RemovalReason.DISCARDED);
            }

            if (!this.isVehicle()) {
                this.setCarryDamage(0.0f);
            }

            if (this.getHealth() != this.getMaxHealth() && this.getTarget() == null && this.tickCount % 20 == 0) {
                healCounter++;
                if (CommonMod.config.generalConfigs.enableLogging) {
                    CommonMod.LOGGER.warn(
                        "Current Health: {} and Max Health: {} of {}",
                        this.getHealth(),
                        this.getMaxHealth(),
                        this.getDisplayName().getString()
                    );
                }
                if (healCounter >= 20 && healCounter > this.lastHurt) {
                    var healAmount = 3.5833F;
                    if (
                        this.level()
                            .getBlockStatesIfLoaded(this.getBoundingBox().inflate(5))
                            .anyMatch(state -> state.is(GigTags.NEST_BLOCKS))
                    ) {
                        healAmount *= 1.5F;
                    }
                    if (CommonMod.config.generalConfigs.enableLogging) {
                        CommonMod.LOGGER.warn("Now healing for {}", healAmount);
                    }
                    this.heal(healAmount);
                    healCounter = 0;
                    this.lastHurt = 0;
                }
            }

            if (this.isExecuting()) {
                this.navigation.stop();
            }

            AzureTicker.tick(serverLevel, this.vibrationData, this.vibrationUser);
        }

        if (this.tickCount % 10 == 0) {
            this.refreshDimensions();
        }

        moveAnalysis.update();
        if (options.canCrawl) {
            crawlingManager.tick();
        }
    }

    @Override
    protected void jumpInLiquid(@NotNull TagKey<Fluid> fluid) {}

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public void checkDespawn() {}

    @Override
    public void die(@NotNull DamageSource source) {
        bleed(source);
        super.die(source);
    }

    @Override
    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        if (this.level() instanceof ServerLevel serverLevel)
            biConsumer.accept(this.dynamicGameEventListener, serverLevel);
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isUnderWater()) {
            moveRelative(0.1F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    /*
     * SOUNDS
     */
    @Override
    public @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return GigSounds.ALIEN_HURT.get();
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return GigSounds.ALIEN_DEATH.get();
    }

    @Override
    protected @NotNull SoundEvent getSwimSplashSound() {
        return SoundEvents.DOLPHIN_SPLASH;
    }

    @Override
    protected @NotNull SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        if (options.completelyBlocksMovement) {
            return this.isAlive();
        }
        return super.canBeCollidedWith();
    }

    public void grabTarget(Entity entity) {
        if (
            entity == this.getTarget() && !entity.hasPassenger(
                this
            ) && entity.getInBlockState().getBlock() != GigBlocks.NEST_RESIN_WEB_CROSS
        ) {
            entity.startRiding(this, true);
            this.setAggressive(false);
            if (entity instanceof ServerPlayer player)
                player.connection.send(new ClientboundSetPassengersPacket(entity));
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        var multiplier = 1.0f;
        if (source == this.damageSources().onFire())
            multiplier = 2.0f;
        if (source == damageSources().inWall())
            return false;
        if (this.isVehicle() && !this.level().isClientSide) {
            this.setCarryDamage(this.getCarryDamage() + amount);
            if (this.getCarryDamage() > 15.0f) {
                this.ejectPassengers();
                this.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 10, false, false));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 10, false, false));
                GigCommonMethods.setAnimation(this.animationDispatcher::sendUnkidnap);
            }
        }
        if (isAlive() && amount > 8F) {
            bleed(source);
        }
        // adaptive armour
        // maybe revisit to account for lots of small hits?
        if (source != this.damageSources().genericKill()) {
            var safeAmount = Math.max(amount, 1);
            var adjustedAmount = safeAmount > 50 ? safeAmount / (float) Math.log10(safeAmount) : safeAmount;
            return super.hurt(source, adjustedAmount);
        }
        return super.hurt(source, amount * multiplier);
    }

    @Override
    public @NotNull Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public @NotNull User getVibrationUser() {
        return this.vibrationUser;
    }

    public void drop(LivingEntity target, ItemStack itemStack) {
        if (itemStack.isEmpty())
            return;

        var d = target.getEyeY() - 0.3f;
        var itemEntity = new ItemEntity(target.level(), target.getX(), d, target.getZ(), itemStack);
        itemEntity.setPickUpDelay(40);
        float g = Mth.sin(this.getXRot() * ((float) Math.PI / 180));
        float h = Mth.cos(this.getXRot() * ((float) Math.PI / 180));
        float i = Mth.sin(this.getYRot() * ((float) Math.PI / 180));
        float j = Mth.cos(this.getYRot() * ((float) Math.PI / 180));
        float k = this.random.nextFloat() * ((float) Math.PI * 2);
        float l = 0.02f * this.random.nextFloat();
        itemEntity.setDeltaMovement(
            (-i * h * 0.3f) + Math.cos(k) * l,
            -g * 0.3f + 0.1f * 0.1f,
            (j * h * 0.3f) + Math.sin(k) * l
        );
        target.level().addFreshEntity(itemEntity);
    }

    public void shootAcid(LivingEntity target, LivingEntity attacker) {
        if (attacker.hasLineOfSight(target)) {
            var acidProjectile = GigEntities.ACID_PROJECTILE.get().create(this.level());
            if (acidProjectile != null) {
                // Position the projectile in front of the attacker
                final var attackDirection = attacker.getViewVector(1.0F); // Get view vector
                acidProjectile.setPos(
                    attacker.getX() + attackDirection.x * 2,
                    attacker.getY(0.5), // Adjust vertical position
                    attacker.getZ() + attackDirection.z * 2
                );

                // Calculate the direction vector toward the target (from attacker to target)
                double dx = target.getX() - acidProjectile.getX();
                double dy = target.getY(0.5) - acidProjectile.getY(); // Aim for the center of the target
                double dz = target.getZ() - acidProjectile.getZ();

                // Set the projectile's velocity towards the target
                float velocity = 1.0F; // Initial speed
                float inaccuracy = 0.1F; // Lower value = better aim
                acidProjectile.shoot(dx, dy, dz, velocity, inaccuracy);

                // Spawn the projectile into the world
                attacker.level().addFreshEntity(acidProjectile);
            }
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (player.getItemInHand(hand).is(Items.BUCKET) && !player.level().isClientSide()) {
            player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            player.addEffect(new MobEffectInstance(GigStatusEffects.ACID, 100, 0), this);
            if (player instanceof ServerPlayer serverPlayer) {
                var advancement = serverPlayer.server.getAdvancements().get(Constants.modResource("dontdothat"));
                if (advancement != null && !serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                    for (
                        var s : serverPlayer.getAdvancements()
                            .getOrStartProgress(
                                advancement
                            )
                            .getRemainingCriteria()
                    ) {
                        serverPlayer.getAdvancements().award(advancement, s);
                    }
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (player.getItemInHand(hand).is(Items.GLASS_BOTTLE) && !player.level().isClientSide()) {
            player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            player.hurt(GigDamageSources.of(player.level(), GigDamageSources.ACID), CommonMod.config.alienblockConfigs.acidDamage);

            if (player instanceof ServerPlayer serverPlayer) {
                var advancement = serverPlayer.server.getAdvancements().get(Constants.modResource("dontacidbottle"));
                if (advancement != null && !serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                    for (
                        var s : serverPlayer.getAdvancements()
                            .getOrStartProgress(
                                advancement
                            )
                            .getRemainingCriteria()
                    ) {
                        serverPlayer.getAdvancements().award(advancement, s);
                    }
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isWithinMeleeAttackRange(@NotNull LivingEntity entity) {
        if (this.getBoundingBox().inflate(1.25).intersects(entity.getBoundingBox())) {
            return true;
        }

        return super.isWithinMeleeAttackRange(entity);
    }

    @Override
    public boolean dampensVibrations() {
        return true;
    }

    public void setMoveControl(MoveControl moveControl) {
        this.moveControl = moveControl;
    }

    public void setNavigation(PathNavigation navigation) {
        this.navigation = navigation;
    }

    @Override
    public void updateSwimming() {
        if (!level().isClientSide) {
            if (isEffectiveAi() && isUnderWater() && !this.level().getBlockState(this.blockPosition().above()).isAir()) {
                navigationManager.switchToWater(this);
                setSwimming(true);
            } else {
                navigationManager.switchToGround(this);
                setSwimming(false);
            }
        }
    }

    protected void runLungeAnimation() {
        animationDispatcher.sendLunge();
    }

    protected void checkAndPerformEating(ItemEntity target) {
        if (target == null)
            return;
        if (growthTimeTicks() < 200)
            return;

        if (isWithinEatingRange(target)) {
            lookAt(target, 10.0F, 10.0F);
            if (delayBeforeEating > 0) {
                delayBeforeEating--;

                if (delayBeforeEating == 5 && !triggeredAttackAnimation) {
                    animationDispatcher.sendChomp();
                    triggeredAttackAnimation = true;
                }
            } else {
                ItemStack item = target.getItem();
                if (item.is(GigTags.POTIONS)) {
                    playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
                } else {
                    playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                }
                swing(InteractionHand.MAIN_HAND);
                if (item.has(DataComponents.FOOD)) {
                    var foodComponent = item.get(DataComponents.FOOD);
                    setGrowthTimeTicks(growthTimeTicks() + foodComponent.nutrition() * 400);
                    item.finishUsingItem(level(), this);
                    item.shrink(1);
                } else {
                    setGrowthTimeTicks(growthTimeTicks() + 400);
                    if (item.is(GigTags.POTIONS)) {
                        item.finishUsingItem(level(), this);
                    }
                    item.consume(1, this);
                }
                triggeredAttackAnimation = false;
                delayBeforeEating = 20;
            }
        } else {
            delayBeforeEating--;
            this.triggeredAttackAnimation = false;
        }
    }

    public boolean isWithinEatingRange(@NotNull ItemEntity entity) {
        for (
            var testPos : BlockPos.betweenClosed(
                this.blockPosition()
                    .relative(this.getDirection(), 1)
                    .above(-1)
                    .relative(this.getDirection().getClockWise(), -1),
                this.blockPosition().relative(this.getDirection(), 3).above(1).relative(this.getDirection().getClockWise(), 1)
            )
        ) {
            if (entity.blockPosition().equals(testPos)) {
                return true;
            }
        }
        return this.getBoundingBox().intersects(entity.getBoundingBox().inflate(1.5F));
    }

    public Vec3 center() {
        return position().add(0, getBbHeight() / 2, 0);
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        if (isUnderWater() || climbingManager.climbing || crawlingManager.isCrawling()) {
            return EntityDimensions.scalable(0.75f, 0.75f);
        }
        return super.getDefaultDimensions(pose);
    }

    public void bleed(DamageSource source) {
        if (options.bloodType == BloodType.NONE) {
            return;
        }

        if (level().isClientSide()) {
            return;
        }

        // not sure why we're checking for this, keeping it from the old bleed code, maybe they should be moved to
        // isDamageSourceNotPuncturing?
        if (source == damageSources().genericKill() || source == damageSources().generic()) {
            return;
        }

        if (DamageSourceUtils.isDamageSourceNotPuncturing(source, damageSources())) {
            return;
        }

        var bloodEntityType = switch (options.bloodType) {
            case NONE -> null; // bloodType should not be NONE at this point
            case ACID -> GigEntities.ACID.get();
            case GOO -> GigEntities.GOO.get();
        };
        assert bloodEntityType != null;

        if (options.bloodDiameter == 1) {
            BloodEntity.place(bloodEntityType, level(), blockPosition());
            return;
        }

        var radius = (options.bloodDiameter - 1) / 2;
        for (int i = 0; i < options.bloodDiameter; i++) {
            int x = level().getRandom().nextInt(options.bloodDiameter) - radius;
            int z = level().getRandom().nextInt(options.bloodDiameter) - radius;
            BloodEntity.place(bloodEntityType, level(), blockPosition().offset(x, 0, z));
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (
            options.slapItemChance > 0 &&
                target instanceof LivingEntity living &&
                !level().isClientSide &&
                getRandom().nextFloat() < options.slapItemChance
        ) {
            // TODO(acats) tail attack for item slap

            if (target instanceof Player player) {
                player.drop(player.getInventory().getSelected(), false);
                player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
            } else if (living instanceof Mob mob) {
                drop(mob, mob.getMainHandItem());
                mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
            }
            living.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
        }

        if (options.healsOnHit) {
            heal(1.0833f);
        }
        return super.doHurtTarget(target);
    }

    public boolean growing() {
        return growthTimeTicks() < options.growth.maxGrowthTimeTicks;
    }

    public float growthProgress() {
        if (options.growth.maxGrowthTimeTicks == 0) {
            return 1;
        }
        return (float) growthTimeTicks() / options.growth.maxGrowthTimeTicks;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor level,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        if (spawnType != MobSpawnType.NATURAL && options.growth.spawnFullyGrown) {
            setGrowthTimeTicks(options.growth.maxGrowthTimeTicks);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

}
