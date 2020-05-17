package net.eliza.plugin;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.IBlockData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridCommand implements CommandExecutor {

    private final Plugin plugin;
    private final Logger logger;

    public GridCommand(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only player can use this command");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Please provide file name");
            return true;
        }

        Path folder = plugin.getDataFolder().toPath();

        String fileName = args[0];
        Path path = folder.resolve(fileName);

        if (!Files.exists(path)) {
            sender.sendMessage(ChatColor.RED + "File not found");
            return true;
        }

        Grid grid;

        try (DataInputStream in = new DataInputStream(Files.newInputStream(path))) {
            grid = Grid.importGrid(in);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load data from file", e);
            sender.sendMessage(ChatColor.RED + "Failed to load data from file");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Starting heavy grid render task");
        logger.log(Level.INFO, "Starting heavy grid render task");

        Player player = (Player) sender;
        Location startAt = player.getLocation();

        for (int x = 0; x < grid.getWidth(); x += 1) {
            for (int y = 0; y < grid.getWidth(); y += 1) {
                for (int z = 0; z < grid.getHeight(); z += 1) {
                    // Minecraft height is Y-axis.
                    Location location = startAt.clone().add(x, z, y);

                    if (grid.isObstacle(x, y, z)) {
                        setBlock(location, Material.BEDROCK);
                    } else {
                        setBlock(location, Material.AIR);
                    }
                }
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Grid are rendered");
        logger.log(Level.INFO, "Grid are rendered");
        return true;
    }

    // This is a special implementation of the change block method,
    // which is faster than the default bukkit approach.
    private void setBlock(Location location, Material material) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        net.minecraft.server.v1_15_R1.World nmsWorld = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_15_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        BlockPosition blockPosition = new BlockPosition(x, y, z);

        IBlockData blockData = net.minecraft.server.v1_15_R1.Block.getByCombinedId(material.getId());
        nmsChunk.setType(blockPosition, blockData, false);
    }

}
