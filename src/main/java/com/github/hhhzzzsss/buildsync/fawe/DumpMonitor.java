package com.github.hhhzzzsss.buildsync.fawe;

import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DumpMonitor extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final Path DUMP_PATH;
    private DumpLoader dumpLoader = null;

    public DumpMonitor(JavaPlugin plugin) {
        this.plugin = plugin;
        DUMP_PATH = plugin.getDataFolder().toPath().resolve("REGION_DUMP");
    }

    @Override
    public void run() {
        // I'm going to assume file moving is atomic, so I won't check for incomplete file
        if (Files.exists(DUMP_PATH) &&
                PlotManager.getActivePlot() != null &&
                (dumpLoader == null || dumpLoader.isFinished())
        ) {
            Bukkit.broadcast(Component.text("Loading region dump...").color(NamedTextColor.GRAY));
            dumpLoader = new DumpLoader(plugin, DUMP_PATH, PlotManager.getActivePlot(), new BukkitWorld(Bukkit.getWorlds().get(0)));
            Bukkit.getScheduler().runTaskAsynchronously(plugin, dumpLoader);
        }
    }
}
