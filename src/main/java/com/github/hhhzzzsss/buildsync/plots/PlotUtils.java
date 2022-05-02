package com.github.hhhzzzsss.buildsync.plots;

import org.bukkit.Location;

public class PlotUtils {
    public static final int PLOT_DIM = 256;

    public static PlotCoord getPlotCoord(double x, double z) {
        int fx = (int) Math.floor(x);
        int fz = (int) Math.floor(z);
        return new PlotCoord(Math.floorDiv(fx, PLOT_DIM), Math.floorDiv(fz, PLOT_DIM));
    }

    public static PlotCoord getPlotCoord(Location location) {
        return new PlotCoord(Math.floorDiv(location.getBlockX(), PLOT_DIM), Math.floorDiv(location.getBlockZ(), PLOT_DIM));
    }
}
