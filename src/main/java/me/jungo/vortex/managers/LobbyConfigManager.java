package me.jungo.vortex.managers;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LobbyConfigManager {
    private JavaPlugin plugin;
    private File lobbyConfigFile;
    private FileConfiguration lobbyConfig;

    public LobbyConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        lobbyConfigFile = new File(plugin.getDataFolder(), "lobby.yml");
        if (!lobbyConfigFile.exists()) {
            lobbyConfigFile.getParentFile().mkdirs();
            plugin.saveResource("lobby.yml", false);
        }
        lobbyConfig = YamlConfiguration.loadConfiguration(lobbyConfigFile);
    }

    public Location getLobbySpawn() {
        return (Location) lobbyConfig.get("lobby-spawn", plugin.getServer().getWorlds().get(0).getSpawnLocation());
    }

    public void setLobbySpawn(Location location) {
        lobbyConfig.set("lobby-spawn", location);
        saveConfig();
    }

    private void saveConfig() {
        try {
            lobbyConfig.save(lobbyConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
