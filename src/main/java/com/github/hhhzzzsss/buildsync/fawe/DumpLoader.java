package com.github.hhhzzzsss.buildsync.fawe;

import com.fastasyncworldedit.core.command.SuggestInputParseException;
import com.github.hhhzzzsss.buildsync.plots.PlotCoord;
import com.github.hhhzzzsss.buildsync.plots.PlotUtils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DumpLoader implements Runnable {
    private boolean finished = false;
    JavaPlugin plugin;
    private Path dumpPath;
    private PlotCoord plotCoord;
    private World world;

    public DumpLoader(JavaPlugin plugin, Path dumpPath, PlotCoord plotCoord, World world) {
        this.plugin = plugin;
        this.dumpPath = dumpPath;
        this.plotCoord = plotCoord;
        this.world = world;
    }

    @Override
    public void run() {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(dumpPath.toFile());
            BufferedInputStream bin = new BufferedInputStream(fin);
            DataInputStream din = new DataInputStream(bin);

            loadFromStream(din);

            din.close();
            Files.delete(dumpPath);
            Bukkit.getScheduler().runTask(plugin, () -> {
                Component component = Component.text("Finished loading region dump").color(NamedTextColor.GRAY);
                Bukkit.broadcast(component);
            });
        } catch (SuggestInputParseException e) {
            e.printStackTrace();
            Bukkit.getScheduler().runTask(plugin, () -> {
                Component component = Component.text("Invalid block used in region dump palette. Deleting.").color(NamedTextColor.RED);
                Bukkit.broadcast(component);
            });
            try {
                if (fin != null) {
                    fin.close();
                }
                Files.delete(dumpPath);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getScheduler().runTask(plugin, () -> {
                Component component = Component.text("Error while loading region dump. See console for mode details").color(NamedTextColor.RED);
                Bukkit.broadcast(component);
            });
        }  finally {
            finished = true;
        }
    }

    synchronized public boolean isFinished() {
        return finished;
    }

    private void loadFromStream(DataInputStream din) throws IOException {
        try (EditSession editSession =
                     WorldEdit.getInstance()
                             .newEditSessionBuilder()
                             .world(world)
                             .fastMode(true)
                             .build()) {
            int paletteSize = din.readInt();
            ArrayList<BlockState> palette = new ArrayList<>();
            for (int i = 0; i < paletteSize; i++) {
                int stringSize = din.readInt();
                String paletteString = new String(din.readNBytes(stringSize));
                palette.add(BlockState.get(paletteString));
            }
            int offsetX = plotCoord.x*PlotUtils.PLOT_DIM;
            int offsetZ = plotCoord.z *PlotUtils.PLOT_DIM;
            for (int y = 0; y < PlotUtils.PLOT_DIM; y++) {
                for (int z = 0; z < PlotUtils.PLOT_DIM; z++) {
                    for (int x = 0; x < PlotUtils.PLOT_DIM; x++) {
                        BlockState blockState = palette.get(din.readInt());
                        editSession.setBlock(x + offsetX, y, z + offsetZ, blockState);
                    }
                }
            }
        }
    }
}
