package com.mcsoccer.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Scoreboard;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mcsoccer")
                .then(Commands.literal("reset")
                        .requires(source -> source.hasPermission(2)) // op level 2
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            Scoreboard scoreboard = source.getServer().getScoreboard();
                            Objective objective = scoreboard.getObjective("mcsoccer_goals");

                            if (objective != null) {
                                scoreboard.removeObjective(objective);
                            }

                            // Broadcast reset message
                            source.getServer().getPlayerList().getPlayers().forEach(player ->
                                    player.displayClientMessage(
                                            Component.literal("\u00A7e\u00A7l\u26BD Scoreboard reset! \u26BD"), false));

                            source.sendSuccess(() -> Component.literal("MC Soccer scoreboard reset."), true);
                            return 1;
                        }))
                .then(Commands.literal("hide")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            Scoreboard scoreboard = source.getServer().getScoreboard();
                            scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, null);
                            source.sendSuccess(() -> Component.literal("MC Soccer scoreboard hidden."), true);
                            return 1;
                        }))
                .then(Commands.literal("show")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            Scoreboard scoreboard = source.getServer().getScoreboard();
                            Objective objective = scoreboard.getObjective("mcsoccer_goals");
                            if (objective != null) {
                                scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);
                                source.sendSuccess(() -> Component.literal("MC Soccer scoreboard shown."), true);
                            } else {
                                source.sendFailure(Component.literal("No MC Soccer scoreboard exists yet. Score a goal first!"));
                            }
                            return 1;
                        }))
        );
    }
}
