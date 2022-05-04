package com.github.hhhzzzsss.buildsync;

import com.github.hhhzzzsss.buildsync.commands.*;
import com.github.hhhzzzsss.buildsync.display.BossbarHandler;
import com.github.hhhzzzsss.buildsync.display.ParticleBoundary;
import com.github.hhhzzzsss.buildsync.fawe.DumpMonitor;
import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildSyncPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        PlotManager.init(this);

        getServer().getPluginManager().registerEvents(BossbarHandler.INSTANCE, this);

        new ParticleBoundary().runTaskTimer(this, 0, 1);
        new DumpMonitor(this).runTaskTimer(this, 20, 20);

        getCommand("select").setExecutor(new SelectCommand());
        getCommand("deselect").setExecutor(new DeselectCommand());
        getCommand("toggleboundary").setExecutor(new ToggleBoundaryCommand());
        getCommand("register").setExecutor(new RegisterCommand());
        getCommand("save").setExecutor(new SaveCommand());

        getLogger().info("Build Sync Plugin was enabled");
    }
    @Override
    public void onDisable() {
        getLogger().info("Build Sync Plugin was disabled");
    }
}
