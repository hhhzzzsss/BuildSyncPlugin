package com.github.hhhzzzsss.buildsync.commands;

import com.github.hhhzzzsss.buildsync.display.BossbarHandler;
import com.github.hhhzzzsss.buildsync.plots.PlotCoord;
import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import com.github.hhhzzzsss.buildsync.plots.PlotUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.naming.Name;

public class RegisterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlotCoord coord = PlotManager.getActivePlot();
        if (coord == null) {
            throw new CommandException("No plot is currently selected");
        }
        String name = String.join(" ", args);
        PlotManager.registerPlot(coord, name);
        BossbarHandler.INSTANCE.updateAllBossbars();
        Component component = Component.text("Registered plot ", NamedTextColor.GRAY)
                .append(Component.text(coord.toString(), NamedTextColor.DARK_AQUA))
                .append(Component.text(" under the name ", NamedTextColor.GRAY))
                .append(Component.text(name, NamedTextColor.DARK_AQUA));
        sender.sendMessage(component);
        return true;
    }
}
