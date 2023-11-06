package me.jungo.vortex.managers;

import me.jungo.vortex.Main;
import me.jungo.vortex.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TeamManager {
    private JavaPlugin plugin;
    private Map<Player, GameTeam> playerTeams;
    private Map<String, GameTeam> teamsByColor;
    private int initialLives;

    public TeamManager(Main plugin, int initialLives) {
        this.playerTeams = new HashMap<>();
        this.initialLives = initialLives;
        this.teamsByColor = new HashMap<>();
        this.plugin = plugin;

        // Initialiser et ajouter des équipes
        teamsByColor.put("rouge", new GameTeam("Rouge"));
        teamsByColor.put("bleue", new GameTeam("Bleue"));
        teamsByColor.put("verte", new GameTeam("Verte"));
        teamsByColor.put("jaune", new GameTeam("Jaune"));
    }

    public void addPlayerToTeam(Player player, GameTeam team) {
        playerTeams.put(player, team);
        team.addPlayer(player, initialLives);
    }

    public GameTeam getTeam(Player player) {
        return playerTeams.get(player);
    }

    public GameTeam getTeamByColor(String color) {
        return teamsByColor.get(color.toLowerCase());
    }

    public void setTeamSpawn(String teamName, Location location) {
        GameTeam team = getTeamByColor(teamName);
        if (team != null) {
            team.setSpawnLocation(location);

            // Sauvegarde de la position dans le fichier de configuration
            FileConfiguration config = plugin.getConfig();
            String path = "teamSpawns." + teamName;
            config.set(path + ".world", location.getWorld().getName());
            config.set(path + ".x", location.getX());
            config.set(path + ".y", location.getY());
            config.set(path + ".z", location.getZ());
            config.set(path + ".yaw", location.getYaw());
            config.set(path + ".pitch", location.getPitch());
            plugin.saveConfig();
        } else {
            // Gérer le cas où l'équipe n'existe pas
        }
    }

    public boolean areAllSpawnsSet() {
        for (GameTeam team : teamsByColor.values()) {
            if (team.getSpawnLocation() == null) {
                return false;
            }
        }
        return true;
    }

    public boolean doesTeamExist(String teamName) {
        return teamsByColor.containsKey(teamName);
    }

}
