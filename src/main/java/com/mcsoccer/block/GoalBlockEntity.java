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

    public GoalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GOAL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GoalBlockEntity be) {
        if (be.cooldown > 0) {
            be.cooldown--;
            return;
        }

        // Scan for soccer ball entities in a 4-block radius around the goal
        AABB detectionBox = new AABB(pos).inflate(4.0, 3.0, 4.0);
        List<SoccerBallEntity> balls = level.getEntitiesOfClass(SoccerBallEntity.class, detectionBox);

        for (SoccerBallEntity ball : balls) {
            // Check if ball is within 1.5 blocks of the goal (close enough = goal scored)
            double dist = ball.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (dist < 2.25) { // 1.5^2
                // GOAL!
                Player scorer = ball.getLastKicker();
                String scorerName = scorer != null ? scorer.getName().getString() : "Unknown";

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

                // Broadcast goal message to all nearby players
                level.players().forEach(player -> {
                    if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 4096) { // 64 blocks
                        player.displayClientMessage(
                                Component.literal("\u00A76\u00A7l\u26BD GOAL by " + scorerName + "! \u26BD"), true);
                    }
                });

                // Reset ball position (move it up and stop it)
                ball.setPos(pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5);
                ball.setDeltaMovement(0, 0, 0);

                // Cooldown to prevent spam (3 seconds = 60 ticks)
                be.cooldown = 60;
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
