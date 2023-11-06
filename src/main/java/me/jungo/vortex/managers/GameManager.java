package me.jungo.vortex.managers;

import me.jungo.vortex.game.GamePhase;
import me.jungo.vortex.game.GameTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class GameManager {
    private final ConfigManager configManager;
    private GamePhase phase = GamePhase.WAITING;
    private final JavaPlugin plugin;
    private final LobbyConfigManager lobbyConfigManager;
    private final GameScoreboardManager scoreboardManager;
    private TeamManager teamManager;

    public GameManager(JavaPlugin plugin, LobbyConfigManager lobbyConfigManager, ConfigManager configManager, TeamManager teamManager, GameScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.lobbyConfigManager = lobbyConfigManager;
        this.configManager = configManager;
        this.teamManager = teamManager;
        this.scoreboardManager = scoreboardManager;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase newPhase) {
        this.phase = newPhase;
        if (newPhase == GamePhase.WAITING) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                resetPlayer(player);
                setupWaitingItems(player);
            }
        }
    }


    public void startGame() {
        if (phase == GamePhase.WAITING) {
            // Vérifiez si tous les points de spawn des équipes sont définis
            boolean allSpawnsSet = teamManager.areAllSpawnsSet();
            if (!allSpawnsSet) {
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Vous n'avez pas ajouté les spawn de vos équipes");
                return;
            }

            phase = GamePhase.INGAME;
            // Téléportez chaque joueur au point de spawn de son équipe
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                scoreboardManager.updateScoreboard(player, GamePhase.INGAME);
                GameTeam team = teamManager.getTeam(player);
                if (team != null && team.getSpawnLocation() != null) {
                    player.teleport(team.getSpawnLocation());
                } else {
                    // Gérer le cas où le joueur n'a pas d'équipe ou si le spawn n'est pas défini
                }
                resetPlayerIngame(player); // Réinitialiser le joueur pour la phase Ingame
            }
            // Autres logiques de démarrage de jeu...
        } else {
            // Informer que le jeu ne peut pas être démarré
        }
    }



    public void stopGame() {
        if (phase == GamePhase.INGAME) {
            phase = GamePhase.FINISH;
            endGame();
        } else {
            // Inform that game cannot be stopped
        }
    }

    public void resetGame() {
        phase = GamePhase.WAITING;
        // Réinitialiser chaque joueur et mettre en place les items d'attente
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            scoreboardManager.updateScoreboard(player, GamePhase.WAITING);
            resetPlayer(player);
            setupWaitingItems(player);
        }
        // Autres logiques de réinitialisation...
    }


    public void endGame() {
        // Show title to all players
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendTitle(ChatColor.RED + "FIN DE LA PARTIE", ChatColor.YELLOW + "Vous allez être téléporté", 10, 70, 20);
        }

        // Schedule to reset the game after 5 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                resetGame();
                Location lobbySpawn = lobbyConfigManager.getLobbySpawn();
                if (lobbySpawn != null) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        player.teleport(lobbySpawn);
                    }
                }
                cancel(); // Cancel this task
            }
        }.runTaskLater(plugin, 100L); // 100 ticks for 5 seconds
    }

    public void setupWaitingItems(Player player) {
        Bukkit.getLogger().info("Configuration des items pour " + player.getName()); // Log pour confirmer l'appel

        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        // Item de sélection d'équipe
        ItemStack teamSelector = new ItemStack(Material.NETHER_STAR);
        ItemMeta teamSelectorMeta = teamSelector.getItemMeta();
        teamSelectorMeta.setDisplayName(ChatColor.GREEN + "Sélectionner une équipe");
        teamSelector.setItemMeta(teamSelectorMeta);
        inventory.setItem(0, teamSelector);

        // Item pour quitter
        ItemStack leaveItem = new ItemStack(Material.IRON_DOOR);
        ItemMeta leaveItemMeta = leaveItem.getItemMeta();
        leaveItemMeta.setDisplayName(ChatColor.RED + "Quitter la partie");
        leaveItem.setItemMeta(leaveItemMeta);
        inventory.setItem(8, leaveItem);
    }

    public void resetPlayer(Player player) {
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.setExp(0);
        player.setLevel(0);
        // Autres réinitialisations nécessaires
    }

    public void resetPlayerIngame(Player player) {
        player.getInventory().clear(); // Vider l'inventaire
        player.setHealth(20.0); // Réinitialiser la santé
        player.setFoodLevel(20); // Réinitialiser la faim
        player.setGameMode(GameMode.SURVIVAL); // Mettre en mode survie
        player.setExp(0); // Réinitialiser l'expérience
        player.setLevel(0); // Réinitialiser le niveau
        // Autres réinitialisations nécessaires
    }





}
