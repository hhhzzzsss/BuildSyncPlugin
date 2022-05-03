package com.github.hhhzzzsss.buildsync.plots;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class PlotManager {
    private static PlotCoord activePlot = null;
    private static HashMap<PlotCoord, Plot> plotMap = new HashMap<>();
    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(PlotCoord.class, new PlotCoord.PlotCoordDeserializer())
            .create();
    private static Path savedPlotsDir;
    private static Path indexPath;

    public static void init(JavaPlugin plugin) {
        savedPlotsDir = plugin.getDataFolder().toPath().resolve("SavedPlots");
        savedPlotsDir.toFile().mkdirs();
        indexPath = savedPlotsDir.resolve("index.json");
        try {
            loadIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Plot getPlot(PlotCoord coord) {
        return plotMap.get(coord);
    }

    public static void registerPlot(PlotCoord coord, String name) {
        if (plotMap.containsKey(coord)) {
            plotMap.get(coord).setName(name);
        } else {
            plotMap.put(coord, new Plot(coord, name));
        }
        try {
            saveIndex();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.broadcast(Component.text("Failed to save index", NamedTextColor.RED));
        }
    }

    public static PlotCoord getActivePlot() {
        return activePlot;
    }

    public static void setActivePlot(PlotCoord coord) {
        activePlot = coord;
    }

    public static void loadIndex() throws IOException {
        if (Files.exists(indexPath)) {
            Reader indexReader = Files.newBufferedReader(indexPath);
            Type typeToken = new TypeToken<HashMap<PlotCoord, Plot>>() { }.getType();
            plotMap = gson.fromJson(indexReader, typeToken);
            indexReader.close();
        }
    }

    public static void saveIndex() throws IOException {
        Writer indexWriter = Files.newBufferedWriter(indexPath);
        indexWriter.write(gson.toJson(plotMap));
        indexWriter.close();
    }
}
