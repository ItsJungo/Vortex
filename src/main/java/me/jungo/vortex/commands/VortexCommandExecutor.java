package me.jungo.vortex.commands;

import me.jungo.vortex.Main;
import me.jungo.vortex.game.GamePhase;
import me.jungo.vortex.managers.GameManager;
import me.jungo.vortex.managers.LobbyConfigManager;
import me.jungo.vortex.managers.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class VortexCommandExecutor implements CommandExecutor {
    private Main plugin;
    private GameManager gameManager;
    private LobbyConfigManager lobbyConfigManager;
    private TeamManager teamManager;

    public VortexCommandExecutor(Main plugin, GameManager gameManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.lobbyConfigManager = new LobbyConfigManager(plugin);
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seul un joueur peut exécuter cette commande.");
            return true;
        }

        Player player = (Player) sender;

        if (!(label.equalsIgnoreCase("vortex") || label.equalsIgnoreCase("vx"))) {
            return false;
        }

        if (args.length == 0) {
            displayHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (gameManager.getPhase() == GamePhase.WAITING) {
                    gameManager.startGame();
                } else {
                    sender.sendMessage(ChatColor.RED + "Une partie est déjà en cours ou terminée.");
                }
                break;
            case "stop":
                if (gameManager.getPhase() == GamePhase.INGAME) {
                    gameManager.stopGame();
                    sender.sendMessage(ChatColor.YELLOW + "La partie s'arrête...");
                } else {
                    sender.sendMessage(ChatColor.RED + "Aucune partie n'est en cours ou elle est déjà terminée.");
                }
                break;
            case "statut":
                sender.sendMessage(ChatColor.BLUE + "Statut actuel du jeu: " + gameManager.getPhase().toString());
                break;
            case "help":
                displayHelp(sender);
                break;
            case "setlobby":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Seul un joueur peut exécuter cette commande.");
                    break;
                }
                if (!sender.hasPermission("vortex.setlobby")) {
                    sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'exécuter cette commande.");
                    break;
                }

                lobbyConfigManager.setLobbySpawn(player.getLocation());
                sender.sendMessage(ChatColor.GREEN + "Le point de spawn du lobby a été défini!");
                break;
            case "teamsetspawn":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Seul un joueur peut exécuter cette commande.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Veuillez spécifier le nom de l'équipe.");
                    return true;
                }
                String teamName = args[1].toLowerCase(); // Convertir en minuscules

                // Vérifier si l'équipe existe
                if (!teamManager.doesTeamExist(teamName)) {
                    sender.sendMessage(ChatColor.RED + "Cette équipe n'existe pas.");
                    return true;
                }

                teamManager.setTeamSpawn(teamName, player.getLocation());
                sender.sendMessage(ChatColor.GREEN + "Point d'apparition défini pour l'équipe " + teamName);
                break;
        }
        return true;
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "=== Vortex Help ===");
        sender.sendMessage(ChatColor.GREEN + "/vortex start" + ChatColor.WHITE + " - Lancer une partie de Vortex.");
        sender.sendMessage(ChatColor.GREEN + "/vortex stop" + ChatColor.WHITE + " - Arrêter la partie en cours.");
        sender.sendMessage(ChatColor.GREEN + "/vortex statut" + ChatColor.WHITE + " - Afficher le statut de la partie en cours.");
        sender.sendMessage(ChatColor.GREEN + "/vortex setlobby" + ChatColor.WHITE + " - Définit le lobby d'attente.");
        sender.sendMessage(ChatColor.GREEN + "/vortex teamsetspawn [equipe]" + ChatColor.WHITE + " - Définit le point de spawn des équipes.");
        // Ajoutez d'autres commandes d'aide ici.
    }
}
