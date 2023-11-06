package me.jungo.vortex;

import me.jungo.vortex.commands.VortexCommandExecutor;
import me.jungo.vortex.game.GamePhase;
import me.jungo.vortex.listeners.PlayerListener;
import me.jungo.vortex.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private LobbyConfigManager lobbyConfigManager;
    private GameManager gameManager;
    private ConfigManager configManager;
    private GameScoreboardManager gameScoreboardManager;
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        lobbyConfigManager = new LobbyConfigManager(this);
        teamManager = new TeamManager(this, 5);
        gameScoreboardManager = new GameScoreboardManager(configManager, teamManager);
        gameManager = new GameManager(this, lobbyConfigManager, configManager, teamManager, gameScoreboardManager);

        // Enregistrement des commandes et des listeners
        getCommand("vortex").setExecutor(new VortexCommandExecutor(this, gameManager, teamManager)); // Ajout de teamManager ici
        getServer().getPluginManager().registerEvents(new PlayerListener(this, lobbyConfigManager, gameManager, gameScoreboardManager, teamManager), this);

    }

    @Override
    public void onDisable() {
        // Logique de désactivation si nécessaire
    }
}
