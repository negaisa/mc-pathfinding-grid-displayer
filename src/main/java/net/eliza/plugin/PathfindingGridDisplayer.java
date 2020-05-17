package net.eliza.plugin;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class PathfindingGridDisplayer extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginCommand command = getCommand("grid");
        BukkitScheduler scheduler = getServer().getScheduler();

        if (command != null) {
            command.setExecutor(new GridCommand(this));
        }
    }

}
