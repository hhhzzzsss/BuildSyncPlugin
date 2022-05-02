package com.github.hhhzzzsss.buildsync.display;

import com.github.hhhzzzsss.buildsync.plots.Plot;
import com.github.hhhzzzsss.buildsync.plots.PlotCoord;
import com.github.hhhzzzsss.buildsync.plots.PlotManager;
import com.github.hhhzzzsss.buildsync.plots.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class BossbarHandler implements Listener {
    public static final BossbarHandler INSTANCE = new BossbarHandler();
    HashMap<UUID, BossBar> bossbarMap = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BossBar bossbar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        bossbarMap.put(uuid, bossbar);
        bossbar.setProgress(1.0);
        updateBossbar(player);
        bossbar.addPlayer(player);
        bossbar.setVisible(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        PlotCoord pcFrom = PlotUtils.getPlotCoord(event.getFrom());
        PlotCoord pcTo = PlotUtils.getPlotCoord(event.getTo());
        if (!pcFrom.equals(pcTo)) {
            updateBossbar(event.getPlayer(), pcTo);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BossBar bossbar = bossbarMap.get(uuid);
        if (bossbar != null) {
            bossbar.removeAll();
        }
        bossbarMap.remove(bossbar);
    }

    private void updateBossbar(Player player) {
        updateBossbar(player, PlotUtils.getPlotCoord(player.getLocation()));
    }

    private void updateBossbar(Player player, PlotCoord plotCoord) {
        UUID uuid = player.getUniqueId();
        BossBar bossbar = bossbarMap.get(uuid);
        if (bossbar == null) {
            System.err.println("Error: bossbar for player " + player.getName() + " was null!");
            return;
        }

        Plot plot = PlotManager.getPlot(plotCoord);
        String plotName;
        if (plot == null) {
            plotName = "Unregistered Plot";
        } else {
            plotName = plot.getName();
        }
        BarColor color;
        if (plotCoord.equals(PlotManager.getActivePlot())) {
            color = BarColor.BLUE;
        } else if (plot == null) {
            color = BarColor.WHITE;
        } else if (!plot.isSaved()) {
            color = BarColor.YELLOW;
        } else {
            color = BarColor.GREEN;
        }
        bossbar.setColor(color);
        bossbar.setTitle(ChatColor.DARK_AQUA + plotName + ChatColor.GRAY + " - " + plotCoord);
    }

    public void updateAllBossbars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateBossbar(player);
        }
    }
}
