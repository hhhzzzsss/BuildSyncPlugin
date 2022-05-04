package com.github.hhhzzzsss.buildsync.commands;

import com.github.hhhzzzsss.buildsync.plots.PlotCoord;
import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlotCoord coord = PlotManager.getActivePlot();
        if (coord == null) {
            throw new CommandException("No plot is currently selected");
        }
        Component component = Component.text("Saving ", NamedTextColor.GRAY)
                .append(Component.text(PlotManager.getPlot(coord).getName(), NamedTextColor.DARK_AQUA))
                .append(Component.text(" to schematic...", NamedTextColor.GRAY));
        sender.sendMessage(component);
        PlotManager.savePlot(coord);
        return true;
    }
}
