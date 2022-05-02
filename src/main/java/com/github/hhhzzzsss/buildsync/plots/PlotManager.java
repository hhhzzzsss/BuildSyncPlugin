package com.github.hhhzzzsss.buildsync.plots;

import java.util.HashMap;

public class PlotManager {
    private static PlotCoord activePlot = null;
    private static HashMap<PlotCoord, Plot> plotMap = new HashMap<>();

    static {

    }

    public static Plot getPlot(PlotCoord coord) {
        return plotMap.get(coord);
    }

    public static PlotCoord getActivePlot() {
        return activePlot;
    }

    public static void setActivePlot(PlotCoord coord) {
        activePlot = coord;
    }
}
