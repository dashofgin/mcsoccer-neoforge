package com.mcsoccer.entity;

import com.mcsoccer.data.ModAttachments;
import com.mcsoccer.data.PlayerSoccerData;
import com.mcsoccer.item.ModItems;
import com.mcsoccer.network.KickAction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.projectile.ItemSupplier;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class SoccerBallEntity extends Entity implements ItemSupplier, GeoEntity {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.soccer_ball.idle");
    private static final RawAnimation ROLL_ANIM = RawAnimation.begin().thenLoop("animation.soccer_ball.rolling");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // Physics constants
    private static final double GRAVITY = 0.04;
    private static final double AIR_DRAG_XZ = 0.98;
    private static final double AIR_DRAG_Y = 0.98;
    private static final double GROUND_FRICTION = 0.82;
    private static final double BOUNCE_FACTOR = 0.55;
    private static final double MIN_MOTION = 0.003;

    // Legacy kick (left/right click)
    private static final double KICK_POWER = 1.2;
    private static final double KICK_UP = 0.3;
    private static final double PASS_POWER = 0.8;
    private static final double PASS_UP = 0.15;

    // Dribble
    private static final double DRIBBLE_SPEED = 0.2;
    private static final int DRIBBLE_COOLDOWN_TICKS = 3;

    private Player lastKicker = null;
    private int dribbleCooldown = 0;

    // Spin for curve shots
    private Vec3 spinForce = Vec3.ZERO;
    private int spinTicksRemaining = 0;

    // Knuckleball wobble
    private boolean knuckleballActive = false;
    private int knuckleballTicksRemaining = 0;
    private final Random random = new Random();

    // Multi-stage drag
    private int dragPhase = 0;
    private int dragPhaseMax = 0;
    private double dragStart = 1.0;
    private double dragEnd = 1.0;

    // Goal state - prevents physics while goal is being processed
    private boolean goalScored = false;
    private int goalFreezeTimer = 0;
    private Vec3 ejectVelocity = null;

    public SoccerBallEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = false;
    }

    public SoccerBallEntity(Level level, double x, double y, double z) {
        this(ModEntities.SOCCER_BALL.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No synched data needed for this entity
    }

    @Override
    public void tick() {
        super.tick();

        // If goal was scored, freeze ball for a period
        if (goalScored) {
            setDeltaMovement(Vec3.ZERO);
            if (goalFreezeTimer > 0) {
                goalFreezeTimer--;
            } else {
                goalScored = false;
                if (ejectVelocity != null) {
                    setDeltaMovement(ejectVelocity);
                    ejectVelocity = null;
                }
            }
            return;
        }

        if (dribbleCooldown > 0) dribbleCooldown--;

        Vec3 motion = getDeltaMovement();

        // Apply gravity
        motion = motion.add(0, -GRAVITY, 0);

        // Apply curve spin
        if (spinTicksRemaining > 0 && spinForce.lengthSqr() > 0.0001) {
            Vec3 horiz = new Vec3(motion.x, 0, motion.z);
            if (horiz.lengthSqr() > 0.001) {
                Vec3 norm = horiz.normalize();
                Vec3 perp = new Vec3(norm.z, 0, -norm.x);
                double magnitude = spinForce.length();
                motion = motion.add(perp.scale(magnitude));
            }
            spinForce = spinForce.scale(0.93);
            spinTicksRemaining--;
        }

        // Apply knuckleball wobble
        if (knuckleballActive && knuckleballTicksRemaining > 0) {
            if (tickCount % 3 == 0) {
                double swerveX = (random.nextDouble() - 0.5) * 0.12;
                double swerveY = (random.nextDouble() - 0.5) * 0.02;
                double swerveZ = (random.nextDouble() - 0.5) * 0.12;
                motion = motion.add(swerveX, swerveY, swerveZ);
            }
            knuckleballTicksRemaining--;
            if (knuckleballTicksRemaining <= 0) {
                knuckleballActive = false;
            }
        }

        // Apply multi-stage drag (overrides normal air drag during kick phases)
        if (dragPhase < dragPhaseMax) {
            double t = (double) dragPhase / dragPhaseMax;
            double drag = dragStart + (dragEnd - dragStart) * t;
            motion = new Vec3(motion.x * drag, motion.y, motion.z * drag);
            dragPhase++;
        }

        // Set motion before move so collision detection works
        setDeltaMovement(motion);
        move(MoverType.SELF, motion);

        // After move(), getDeltaMovement() reflects collision adjustments
        motion = getDeltaMovement();

        // Handle bouncing off walls
        if (horizontalCollision) {
            motion = new Vec3(-motion.x * BOUNCE_FACTOR, motion.y, -motion.z * BOUNCE_FACTOR);
            spinForce = Vec3.ZERO;
            spinTicksRemaining = 0;
            if (!level().isClientSide()) {
                level().playSound(null, getX(), getY(), getZ(),
                        SoundEvents.SLIME_BLOCK_HIT, SoundSource.NEUTRAL, 0.5F, 1.2F);
            }
        }

        // Handle bouncing off ground/ceiling
        if (verticalCollision) {
            if (onGround()) {
                double ySpeed = Math.abs(motion.y);
                if (ySpeed > 0.08) {
                    motion = new Vec3(
                            motion.x * GROUND_FRICTION,
                            -motion.y * BOUNCE_FACTOR,
                            motion.z * GROUND_FRICTION
                    );
                    if (!level().isClientSide()) {
                        level().playSound(null, getX(), getY(), getZ(),
                                SoundEvents.SLIME_BLOCK_STEP, SoundSource.NEUTRAL, 0.3F, 1.5F);
                    }
                } else {
                    motion = new Vec3(motion.x * GROUND_FRICTION, 0, motion.z * GROUND_FRICTION);
                }
            } else {
                motion = new Vec3(motion.x, -motion.y * BOUNCE_FACTOR, motion.z);
            }
        }

        // Air drag when airborne and not in multi-stage drag
        if (!onGround() && dragPhase >= dragPhaseMax) {
            motion = new Vec3(motion.x * AIR_DRAG_XZ, motion.y * AIR_DRAG_Y, motion.z * AIR_DRAG_XZ);
        }

        // Ground rolling friction
        if (onGround() && !verticalCollision) {
            motion = new Vec3(motion.x * GROUND_FRICTION, motion.y, motion.z * GROUND_FRICTION);
        }

        // Stop if very slow
        if (motion.lengthSqr() < MIN_MOTION * MIN_MOTION) {
            motion = Vec3.ZERO;
            knuckleballActive = false;
            spinTicksRemaining = 0;
            dragPhase = dragPhaseMax;
        }

        setDeltaMovement(motion);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide()) {
            if (goalScored) return InteractionResult.PASS;

            if (player.isShiftKeyDown()) {
                ItemStack ball = new ItemStack(ModItems.SOCCER_BALL.get());
                if (!player.getInventory().add(ball)) {
                    player.drop(ball, false);
                }
                discard();
                return InteractionResult.SUCCESS;
            }
            Vec3 look = player.getLookAngle();
            setDeltaMovement(look.x * PASS_POWER, PASS_UP + look.y * 0.3, look.z * PASS_POWER);
            lastKicker = player;
            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.SLIME_BLOCK_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (goalScored) return false;
        if (source.getEntity() instanceof Player player) {
            Vec3 look = player.getLookAngle();
            setDeltaMovement(look.x * KICK_POWER, KICK_UP + look.y * 0.5, look.z * KICK_POWER);
            lastKicker = player;
            serverLevel.playSound(null, getX(), getY(), getZ(),
                    SoundEvents.SLIME_BLOCK_HIT, SoundSource.PLAYERS, 1.0F, 0.8F);
            return false;
        }
        return false;
    }

    /**
     * Advanced kick dispatch — called from network handler for keybind actions.
     */
    public void applyKick(Player player, KickAction action) {
        if (goalScored) return;

        Vec3 look = player.getLookAngle();
        Vec3 playerVelocity = player.getDeltaMovement();
        double playerSpeed = playerVelocity.horizontalDistance();
        double kickType = look.y;

        double xDir = look.x;
        double zDir = look.z;
        double hLen = Math.sqrt(xDir * xDir + zDir * zDir);
        if (hLen > 0) {
            xDir /= hLen;
            zDir /= hLen;
        }

        // Reset effects
        spinForce = Vec3.ZERO;
        spinTicksRemaining = 0;
        knuckleballActive = false;
        knuckleballTicksRemaining = 0;

        switch (action) {
            case LONG_PASS -> {
                double basePower = 2.0;
                double power = (basePower + playerSpeed * 0.4) * (0.9 + random.nextDouble() * 0.2);
                double hPower = power * 1.3;
                double vPower;
                if (kickType > 0.3) {
                    vPower = 0.8 + kickType * 0.6;
                    hPower *= 0.85;
                } else if (kickType > 0.0) {
                    vPower = 0.6 + kickType * 0.5;
                } else if (kickType > -0.3) {
                    vPower = 0.35;
                    hPower *= 1.15;
                } else {
                    vPower = 0.2;
                    hPower *= 1.3;
                }
                setDeltaMovement(xDir * hPower, vPower, zDir * hPower);
                setupDrag(20, 0.99, 0.96);
            }
            case SHORT_PASS -> {
                double basePower = 1.5;
                double power = (basePower + playerSpeed * 0.3) * (0.95 + random.nextDouble() * 0.1);
                double hPower = power;
                double vPower;
                if (kickType > 0.4) {
                    vPower = 0.4 + kickType * 0.2;
                    hPower *= 0.8;
                } else if (kickType > 0.1) {
                    vPower = 0.2 + kickType * 0.15;
                } else {
                    vPower = 0.08;
                    hPower *= 1.1;
                }
                setDeltaMovement(xDir * hPower, vPower, zDir * hPower);
                setupDrag(12, 0.96, 0.92);
            }
            case CURVE -> {
                double basePower = 1.5;
                double power = (basePower + playerSpeed * 0.5) * (0.9 + random.nextDouble() * 0.2);
                double hPower = power * 1.4;
                double curveMagnitude = power * 0.2;
                double vPower;
                if (kickType > 0.25) {
                    vPower = 0.8 + kickType * 0.6;
                    hPower *= 0.85;
                } else if (kickType > -0.05) {
                    vPower = 0.5 + kickType * 0.5;
                } else {
                    vPower = 0.25;
                    hPower *= 1.2;
                }
                double curvePush = 0.3 * curveMagnitude;
                double xForce = xDir * hPower + zDir * curvePush;
                double zForce = zDir * hPower - xDir * curvePush;
                setDeltaMovement(xForce, vPower, zForce);
                spinForce = new Vec3(curveMagnitude * 0.07, 0, curveMagnitude * 0.07);
                spinTicksRemaining = 25;
                setupDrag(18, 0.985, 0.96);
            }
            case KNUCKLEBALL -> {
                double basePower = 1.5;
                double power = (basePower + playerSpeed * 0.5) * (0.9 + random.nextDouble() * 0.2);
                double hPower = power * 1.4;
                double vPower;
                if (kickType > 0.3) {
                    vPower = 0.7 + kickType * 0.6;
                    hPower *= 0.9;
                } else if (kickType > 0.0) {
                    vPower = 0.5 + kickType * 0.5;
                } else if (kickType > -0.3) {
                    vPower = 0.25;
                    hPower *= 1.15;
                } else {
                    vPower = 0.12;
                    hPower *= 1.4;
                }
                setDeltaMovement(xDir * hPower, vPower, zDir * hPower);
                knuckleballActive = true;
                knuckleballTicksRemaining = 40;
                setupDrag(20, 0.985, 0.97);
            }
            default -> {
                // Standing/slide tackle handled in ModMessages directly
            }
        }

        lastKicker = player;

        Vec3 currentMotion = player.getDeltaMovement();
        player.setDeltaMovement(currentMotion.x * 0.4, currentMotion.y, currentMotion.z * 0.4);
        player.hurtMarked = true;
    }

    private void setupDrag(int ticks, double startDrag, double endDrag) {
        this.dragPhase = 0;
        this.dragPhaseMax = ticks;
        this.dragStart = startDrag;
        this.dragEnd = endDrag;
    }

    public void setLastKicker(Player player) {
        this.lastKicker = player;
    }

    /** Called by GoalBlockEntity to freeze ball after goal */
    public void onGoalScored() {
        this.goalScored = true;
        this.goalFreezeTimer = 60;
        this.ejectVelocity = null;
        setDeltaMovement(Vec3.ZERO);
        spinForce = Vec3.ZERO;
        spinTicksRemaining = 0;
        knuckleballActive = false;
        knuckleballTicksRemaining = 0;
        dragPhase = dragPhaseMax;
    }

    /** Set velocity to apply when freeze ends (ball ejected from goal) */
    public void setEjectVelocity(Vec3 velocity) {
        this.ejectVelocity = velocity;
    }

    public boolean isGoalFrozen() {
        return goalScored;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void push(Entity entity) {
        if (goalScored) return;
        if (entity instanceof Player player) {
            if (dribbleCooldown > 0) return;
            // Check canTouchBall
            if (!level().isClientSide()) {
                PlayerSoccerData data = player.getData(ModAttachments.PLAYER_SOCCER_DATA);
                if (!data.canTouchBall()) return;
            }
            double yaw = Math.toRadians(player.getYRot());
            double forwardX = -Math.sin(yaw);
            double forwardZ = Math.cos(yaw);
            setDeltaMovement(new Vec3(
                    forwardX * DRIBBLE_SPEED,
                    getDeltaMovement().y,
                    forwardZ * DRIBBLE_SPEED
            ));
            lastKicker = player;
            dribbleCooldown = DRIBBLE_COOLDOWN_TICKS;
        }
    }

    public Player getLastKicker() {
        return lastKicker;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.SOCCER_BALL.get());
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.SOCCER_BALL.get());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    // GeoEntity implementation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("movement", 5, state -> {
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001) {
                return state.setAndContinue(ROLL_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
