package com.mcsoccer.block;

import com.mcsoccer.entity.SoccerBallEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.List;

public class GoalBlockEntity extends BlockEntity {

    private int cooldown = 0;
    private static final int GOAL_COOLDOWN = 80; // 4 seconds
    private static final double DETECTION_RADIUS_SQ = 2.25; // 1.5^2

    public GoalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GOAL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GoalBlockEntity be) {
        if (be.cooldown > 0) {
            be.cooldown--;
            return;
        }

        // Scan for soccer ball entities near the goal
        AABB detectionBox = new AABB(pos).inflate(3.0, 2.0, 3.0);
        List<SoccerBallEntity> balls = level.getEntitiesOfClass(SoccerBallEntity.class, detectionBox);

        for (SoccerBallEntity ball : balls) {
            // Skip balls that are already frozen from a goal
            if (ball.isGoalFrozen()) continue;

            // Check if ball is close enough to goal center
            double dist = ball.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (dist < DETECTION_RADIUS_SQ) {
                // GOAL!
                Player scorer = ball.getLastKicker();
                String scorerName = scorer != null ? scorer.getName().getString() : "Unknown";

                // Freeze the ball immediately to prevent double scoring
                ball.onGoalScored();
                ball.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);

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

                // Broadcast goal message
                String message = scorer != null
                        ? "\u00A76\u00A7l\u26BD GOAL! " + scorerName + " scores! \u26BD"
                        : "\u00A76\u00A7l\u26BD GOAL! \u26BD";

                level.players().forEach(player -> {
                    if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 4096) {
                        player.displayClientMessage(Component.literal(message), false);
                    }
                });

                // Set cooldown on this goal block
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
