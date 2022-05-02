package com.github.hhhzzzsss.buildsync.commands;

import com.github.hhhzzzsss.buildsync.display.BossbarHandler;
import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeselectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlotManager.setActivePlot(null);
        BossbarHandler.INSTANCE.updateAllBossbars();
        sender.sendMessage(ChatColor.GRAY + "Set active plot to null");
        return true;
    }
}
