package com.github.hhhzzzsss.buildsync.plots;

public class PlotCoord {
    public final int x;
    public final int z;

    public PlotCoord(int x, int y) {
        this.x = x;
        this.z = y;
    }

    public PlotCoord(String str) {
        String[] split = str.substring(1, str.length()-1).split(",");
        this.x = Integer.parseInt(split[0]);
        this.z = Integer.parseInt(split[1]);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlotCoord plotCoord = (PlotCoord) o;
        return x == plotCoord.x && z == plotCoord.z;
    }

    @Override
    public int hashCode() {
        return 31*x + z + 17;
    }

    @Override
    public String toString() {
        return "(" + x + "," + z + ")";
    }
}
