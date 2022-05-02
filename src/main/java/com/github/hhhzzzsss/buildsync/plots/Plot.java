package com.github.hhhzzzsss.buildsync.plots;

public class Plot {
    public final PlotCoord pos;
    private String name;
    private boolean saved;

    public Plot(PlotCoord plotCoord, String name) {
        this.pos = plotCoord;
        this.name = name;
        this.saved = false;
    }

    public int getX() {
        return pos.x;
    }
    public int getY() {
        return pos.z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSaved() {
        return saved;
    }
}
