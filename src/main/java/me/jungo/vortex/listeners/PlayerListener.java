package me.jungo.vortex.listeners;

import me.jungo.vortex.game.GamePhase;
import me.jungo.vortex.game.GameTeam;
import me.jungo.vortex.managers.GameManager;
import me.jungo.vortex.managers.GameScoreboardManager;
import me.jungo.vortex.managers.LobbyConfigManager;
import me.jungo.vortex.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {
    private JavaPlugin plugin;
    private LobbyConfigManager lobbyConfigManager;
    private GameManager gameManager;
    private GameScoreboardManager scoreboardManager;
    private TeamManager teamManager;

    public PlayerListener(JavaPlugin plugin, LobbyConfigManager lobbyConfigManager, GameManager gameManager, GameScoreboardManager gameScoreboardManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.lobbyConfigManager = lobbyConfigManager;
        this.gameManager = gameManager;
        this.scoreboardManager = gameScoreboardManager;
        this.teamManager = teamManager; // Stockez teamManager ici
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        gameManager.resetPlayer(player);

        GamePhase currentPhase = gameManager.getPhase();
        if (currentPhase == GamePhase.WAITING) {
            gameManager.setPhase(GamePhase.WAITING);
            player.teleport(lobbyConfigManager.getLobbySpawn());
        } else if (currentPhase == GamePhase.INGAME) {
            // Logique pour les joueurs rejoignant en cours de partie (par exemple, mode spectateur)
        }

        if (scoreboardManager != null) {
            scoreboardManager.updateScoreboard(player, gameManager.getPhase());

        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.NETHER_STAR) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                openTeamSelectionGUI(event.getPlayer());
            }
        }
    }

    private void openTeamSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Choisissez une équipe");

        gui.setItem(1, createTeamItem(Material.RED_WOOL, ChatColor.RED + "Équipe Rouge"));
        gui.setItem(3, createTeamItem(Material.BLUE_WOOL, ChatColor.BLUE + "Équipe Bleue"));
        gui.setItem(5, createTeamItem(Material.GREEN_WOOL, ChatColor.GREEN + "Équipe Verte"));
        gui.setItem(7, createTeamItem(Material.YELLOW_WOOL, ChatColor.YELLOW + "Équipe Jaune"));

        player.openInventory(gui);
    }

    private ItemStack createTeamItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Bukkit.getLogger().info("[Vortex] InventoryClickEvent déclenché");
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Choisissez une équipe")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String teamColor = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).toLowerCase();
                Bukkit.getLogger().info("[Vortex] Couleur d'équipe sélectionnée : " + teamColor);

                // Supprimer le préfixe "Équipe " pour correspondre aux clés de la map
                teamColor = teamColor.replace("Équipe ", "");

                GameTeam team = teamManager.getTeamByColor(teamColor);
                if (team != null) {
                    Bukkit.getLogger().info("[Vortex] Équipe trouvée : " + teamColor);
                    teamManager.addPlayerToTeam(player, team);
                    Bukkit.getLogger().info("[Vortex] Joueur ajouté à l'équipe : " + teamColor);
                    player.sendMessage(ChatColor.GREEN + "Vous avez rejoint l'équipe " + teamColor);
                } else {
                }

                // Mettre à jour le scoreboard avec la phase actuelle
                scoreboardManager.updateScoreboard(player, gameManager.getPhase());

            }

            player.closeInventory();
        }
    }


}
