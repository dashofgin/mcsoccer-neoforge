package com.mcsoccer.block;

import com.mcsoccer.entity.SoccerBallEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.List;

public class GoalBlockEntity extends BlockEntity {

    private int cooldown = 0;
    private static final int GOAL_COOLDOWN = 80; // 4 seconds
    private static final double DETECTION_RADIUS_SQ = 2.25; // 1.5^2
    private static final double EJECT_DISTANCE = 6.0;
    private static final double EJECT_SPEED = 0.8;

    public GoalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GOAL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GoalBlockEntity be) {
        if (be.cooldown > 0) {
            be.cooldown--;
            return;
        }

        AABB detectionBox = new AABB(pos).inflate(3.0, 2.0, 3.0);
        List<SoccerBallEntity> balls = level.getEntitiesOfClass(SoccerBallEntity.class, detectionBox);

        for (SoccerBallEntity ball : balls) {
            if (ball.isGoalFrozen()) continue;

            double dist = ball.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (dist < DETECTION_RADIUS_SQ) {
                Player scorer = ball.getLastKicker();
                String scorerName = scorer != null ? scorer.getName().getString() : "Unknown";

                // Get facing direction of the goal block
                Direction facing = state.getValue(GoalBlock.FACING);
                Vec3 forward = new Vec3(facing.getStepX(), 0, facing.getStepZ());

                // Freeze ball, position 6 blocks in front of goal (not up)
                ball.onGoalScored();
                ball.setPos(
                        pos.getX() + 0.5 + forward.x * EJECT_DISTANCE,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5 + forward.z * EJECT_DISTANCE
                );
                // Set forward velocity to apply after freeze ends
                ball.setEjectVelocity(new Vec3(forward.x * EJECT_SPEED, 0.05, forward.z * EJECT_SPEED));

                // Play sounds
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 2.0F, 1.0F);
                level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1.5F, 1.0F);

                // Update scoreboard
                if (scorer != null && level instanceof ServerLevel serverLevel) {
                    Scoreboard scoreboard = serverLevel.getScoreboard();
                    Objective objective = scoreboard.getObjective("mcsoccer_goals");
                    if (objective == null) {
                        objective = scoreboard.addObjective(
                                "mcsoccer_goals",
                                ObjectiveCriteria.DUMMY,
                                Component.literal("Soccer Goals"),
                                ObjectiveCriteria.RenderType.INTEGER,
                                true, null
                        );
                        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);
                    }
                    scoreboard.getOrCreatePlayerScore(scorer, objective).increment();
                }

                String message = scorer != null
                        ? "\u00A76\u00A7l\u26BD GOL! " + scorerName + " zdobył punkt! \u26BD"
                        : "\u00A76\u00A7l\u26BD GOL! \u26BD";

                level.players().forEach(player -> {
                    if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 4096) {
                        player.displayClientMessage(Component.literal(message), false);
                    }
                });

                be.cooldown = GOAL_COOLDOWN;
                break;
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
}
