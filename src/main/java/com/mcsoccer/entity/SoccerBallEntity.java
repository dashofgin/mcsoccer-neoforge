package com.mcsoccer.entity;

import com.mcsoccer.item.ModItems;
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

public class SoccerBallEntity extends Entity implements ItemSupplier {

    // Physics constants
    private static final double GRAVITY = 0.04;
    private static final double AIR_DRAG_XZ = 0.98;
    private static final double AIR_DRAG_Y = 0.98;
    private static final double GROUND_FRICTION = 0.82;
    private static final double BOUNCE_FACTOR = 0.55;
    private static final double MIN_MOTION = 0.003;

    // Kick constants
    private static final double KICK_POWER = 1.2;
    private static final double KICK_UP = 0.3;
    private static final double PASS_POWER = 0.8;
    private static final double PASS_UP = 0.15;

    // Dribble
    private static final double DRIBBLE_SPEED = 0.2;
    private static final int DRIBBLE_COOLDOWN_TICKS = 3;

    private Player lastKicker = null;
    private int dribbleCooldown = 0;

    // Goal state - prevents physics while goal is being processed
    private boolean goalScored = false;
    private int goalFreezeTimer = 0;

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
            }
            return;
        }

        if (dribbleCooldown > 0) dribbleCooldown--;

        Vec3 motion = getDeltaMovement();

        // Apply gravity
        motion = motion.add(0, -GRAVITY, 0);

        // Set motion before move so collision detection works
        setDeltaMovement(motion);
        move(MoverType.SELF, motion);

        // After move(), getDeltaMovement() reflects collision adjustments
        motion = getDeltaMovement();

        // Handle bouncing off walls
        if (horizontalCollision) {
            motion = new Vec3(-motion.x * BOUNCE_FACTOR, motion.y, -motion.z * BOUNCE_FACTOR);
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
                    // Bounce
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
                    // Settled on ground - apply strong ground friction
                    motion = new Vec3(motion.x * GROUND_FRICTION, 0, motion.z * GROUND_FRICTION);
                }
            } else {
                // Hit ceiling
                motion = new Vec3(motion.x, -motion.y * BOUNCE_FACTOR, motion.z);
            }
        }

        // Air drag when airborne
        if (!onGround()) {
            motion = new Vec3(motion.x * AIR_DRAG_XZ, motion.y * AIR_DRAG_Y, motion.z * AIR_DRAG_XZ);
        }

        // Ground rolling friction (always apply when on ground, even outside bounce)
        if (onGround() && !verticalCollision) {
            motion = new Vec3(motion.x * GROUND_FRICTION, motion.y, motion.z * GROUND_FRICTION);
        }

        // Stop if very slow
        if (motion.lengthSqr() < MIN_MOTION * MIN_MOTION) {
            motion = Vec3.ZERO;
        }

        setDeltaMovement(motion);
    }

    // Right-click = soft pass, sneak+right-click = pick up
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

    // Left-click = power kick
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

    /** Called by GoalBlockEntity to freeze ball after goal */
    public void onGoalScored() {
        this.goalScored = true;
        this.goalFreezeTimer = 60; // 3 seconds freeze
        setDeltaMovement(Vec3.ZERO);
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
            // Dribble: move ball in player's look direction (horizontal only)
            double yaw = Math.toRadians(player.getYRot());
            double forwardX = -Math.sin(yaw);
            double forwardZ = Math.cos(yaw);
            setDeltaMovement(new Vec3(
                    forwardX * DRIBBLE_SPEED,
                    getDeltaMovement().y, // preserve Y momentum
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
}
