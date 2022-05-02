package com.github.hhhzzzsss.buildsync.display;

import com.github.hhhzzzsss.buildsync.plots.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleBoundary extends BukkitRunnable {

    public static boolean enabled = true;

    @Override
    public void run() {
        if (!enabled) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location loc = player.getLocation();
            double nearestXBoundary = Math.round(loc.getX() / PlotUtils.PLOT_DIM) * PlotUtils.PLOT_DIM;
            double nearestZBoundary = Math.round(loc.getZ() / PlotUtils.PLOT_DIM) * PlotUtils.PLOT_DIM;
            double distX = Math.abs(loc.getX() - nearestXBoundary);
            double distZ = Math.abs(loc.getZ() - nearestZBoundary);
            double xSpread = Math.min(50.0, distX+5.0);
            double zSpread = Math.min(50.0, distZ+5.0);
            player.spawnParticle(
                    Particle.CLOUD,
                    new Location(loc.getWorld(), nearestXBoundary, loc.getY(), loc.getZ()),
                    20,
                    0.0, xSpread, xSpread, 0.0
            );
            player.spawnParticle(
                    Particle.CLOUD,
                    new Location(loc.getWorld(), loc.getX(), loc.getY(), nearestZBoundary),
                    20,
                    zSpread, zSpread, 0.0, 0.0
            );
        }
    }
}
