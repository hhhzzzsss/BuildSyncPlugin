package com.github.hhhzzzsss.buildsync.commands;

import com.github.hhhzzzsss.buildsync.display.BossbarHandler;
import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import com.github.hhhzzzsss.buildsync.plots.PlotUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender);
            PlotManager.setActivePlot(PlotUtils.getPlotCoord(player.getLocation()));
            BossbarHandler.INSTANCE.updateAllBossbars();
            player.sendMessage(ChatColor.GRAY + "Set active plot to " + ChatColor.DARK_AQUA + PlotManager.getActivePlot());
            return true;
        } else {
            throw new CommandException("Must be an ingame player");
        }
    }
}
