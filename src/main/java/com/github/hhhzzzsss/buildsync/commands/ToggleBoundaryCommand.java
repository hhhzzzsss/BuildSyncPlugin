package com.github.hhhzzzsss.buildsync.commands;

import com.github.hhhzzzsss.buildsync.display.BossbarHandler;
import com.github.hhhzzzsss.buildsync.display.ParticleBoundary;
import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import com.github.hhhzzzsss.buildsync.plots.PlotUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleBoundaryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ParticleBoundary.enabled = !ParticleBoundary.enabled;
        if (ParticleBoundary.enabled) {
            sender.sendMessage(ChatColor.GRAY + "Enabled particle boundary");
        } else {
            sender.sendMessage(ChatColor.GRAY + "Disabled particle boundary");
        }
        return true;
    }
}
