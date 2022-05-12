package com.github.hhhzzzsss.buildsync.plots;

import com.fastasyncworldedit.core.extent.clipboard.MemoryOptimizedClipboard;
import com.github.hhhzzzsss.buildsync.display.BossbarHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

public class PlotManager {
    private static JavaPlugin plugin;
    private static PlotCoord activePlot = null;
    private static HashMap<PlotCoord, Plot> plotMap = new HashMap<>();
    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(PlotCoord.class, new PlotCoord.PlotCoordDeserializer())
            .create();
    private static Path savedPlotsDir;
    private static Path indexPath;

    public static void init(JavaPlugin currentPlugin) {
        plugin = currentPlugin;
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

    public static PlotCoord getActivePlot() {
        return activePlot;
    }

    public static void setActivePlot(PlotCoord coord) {
        activePlot = coord;
    }

    public static void registerPlot(PlotCoord coord, String name) throws IOException {
        if (plotMap.containsKey(coord)) {
            plotMap.get(coord).setName(name);
        } else {
            plotMap.put(coord, new Plot(coord, name));
        }
        BossbarHandler.INSTANCE.updateAllBossbars();
        try {
            saveIndex();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save index: " + e.getMessage());
        }
    }

    public static void savePlot(PlotCoord coord) {
        if (!plotMap.containsKey(coord)) {
            throw new CommandException("Selected plot must be registered first");
        }
        Plot plot = plotMap.get(coord);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new SaveThread(plot));
    }

    public static class SaveThread implements Runnable {
        Plot plot;
        World world;

        public SaveThread(Plot plot) {
            this.plot = plot;
            this.world = new BukkitWorld(Bukkit.getWorlds().get(0));
        }

        @Override
        public void run() {
            Clipboard clipboard;
            try (EditSession editSession =
                         WorldEdit.getInstance()
                                 .newEditSessionBuilder()
                                 .world(world)
                                 .fastMode(true)
                                 .build()) {
                int blockX = plot.getX() * PlotUtils.PLOT_DIM;
                int blockZ = plot.getZ() * PlotUtils.PLOT_DIM;
                CuboidRegion region = new CuboidRegion(
                        BlockVector3.at(blockX, 0, blockZ),
                        BlockVector3.at(blockX + PlotUtils.PLOT_DIM - 1, PlotUtils.PLOT_DIM - 1, blockZ + PlotUtils.PLOT_DIM - 1)
                );
                clipboard = new BlockArrayClipboard(region, UUID.fromString("3f797064-b9bb-588f-9337-e2deb81e8973"));
                clipboard.setOrigin(region.getCenter().toBlockPoint().withY(region.getMinimumY()));

                ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                        editSession, region, clipboard, region.getMinimumPoint()
                );
                forwardExtentCopy.setCopyingBiomes(false);
                forwardExtentCopy.setCopyingEntities(false);
                try {
                    Operations.complete(forwardExtentCopy);
                } catch (Throwable e) {
                    throw e;
                } finally {
                    clipboard.flush();
                }
            }

            Path path = savedPlotsDir.resolve(plot.pos.toString() + ".schem");
            try {
                try (ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(Files.newOutputStream(path))) {
                    writer.write(clipboard);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.broadcast(Component.text("Failed to save plot to schematic: " + e.getMessage(), NamedTextColor.RED));
                });
                return;
            }
            plot.setSaved(true);
            try {
                saveIndex();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.broadcast(Component.text("Failed to save index: " + e.getMessage(), NamedTextColor.RED));
                });
                return;
            }
            BossbarHandler.INSTANCE.updateAllBossbars();
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.broadcast(Component.text("Successfully saved to schematic", NamedTextColor.GRAY));
            });
        }
    }

    public static synchronized void loadIndex() throws IOException {
        if (Files.exists(indexPath)) {
            Reader indexReader = Files.newBufferedReader(indexPath);
            Type typeToken = new TypeToken<HashMap<PlotCoord, Plot>>() { }.getType();
            plotMap = gson.fromJson(indexReader, typeToken);
            indexReader.close();
        }
    }

    public static synchronized void saveIndex() throws IOException {
        Writer indexWriter = Files.newBufferedWriter(indexPath);
        indexWriter.write(gson.toJson(plotMap));
        indexWriter.close();
    }
}
