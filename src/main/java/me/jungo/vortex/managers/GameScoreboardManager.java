package me.jungo.vortex.managers;

import me.jungo.vortex.game.GamePhase;
import me.jungo.vortex.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScoreboardManager {
    private ConfigManager configManager;
    private ScoreboardManager bukkitScoreboardManager;
    private Objective objective;
    private TeamManager teamManager;
    private Map<Player, String> lastTeamInfo = new HashMap<>();


    public GameScoreboardManager(ConfigManager configManager, TeamManager teamManager) {
        this.configManager = configManager;
        this.teamManager = teamManager;
        bukkitScoreboardManager = Bukkit.getScoreboardManager();
        createScoreboard();
    }

    private void createScoreboard() {
        Scoreboard board = bukkitScoreboardManager.getNewScoreboard();
        objective = board.registerNewObjective("vortex", "dummy", "Vortex");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setScoreboard(Player player, GamePhase currentPhase) {
        // Nettoyer l'ancien scoreboard
        Scoreboard board = player.getScoreboard();
        if (board.getObjective(DisplaySlot.SIDEBAR) != null) {
            board.getObjective(DisplaySlot.SIDEBAR).unregister();
        }

        // Créer un nouveau scoreboard pour la phase actuelle
        board = bukkitScoreboardManager.getNewScoreboard();
        player.setScoreboard(board);
        Objective obj = board.registerNewObjective("vortex", "dummy", "Vortex");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Mettre à jour le scoreboard avec les informations de la phase actuelle
        updateScoreboard(player, currentPhase);
    }

    public void updateScoreboard(Player player, GamePhase currentPhase) {
        clearPreviousScores(player); // Nettoyer les scores précédents

        Objective obj = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (obj == null) {
            obj = player.getScoreboard().registerNewObjective("vortex", "dummy", "Vortex");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        String path = "scoreboard." + (currentPhase == GamePhase.WAITING ? "waiting" : "ingame");
        String title = configManager.getConfig().getString(path + ".title", "&bVortex Scoreboard");
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));

        List<String> lines = configManager.getConfig().getStringList(path + ".lines");
        if (lines == null || lines.isEmpty()) {
            lines = Arrays.asList("&cAucune configuration de scoreboard trouvée!");
        }

        String currentTeam = "";
        GameTeam team = teamManager.getTeam(player);
        if (team != null) {
            currentTeam = team.getColor();
        }

        if (!lastTeamInfo.containsKey(player) || !lastTeamInfo.get(player).equals(currentTeam)) {
            int scoreValue = lines.size();
            for (String line : lines) {
                String formattedLine = line.equals("") ? ChatColor.WHITE + " " + ChatColor.RESET + repeat(" ", scoreValue)
                        : ChatColor.translateAlternateColorCodes('&', line)
                        .replace("%player_count%", String.valueOf(Bukkit.getOnlinePlayers().size()));

                if (line.contains("%team%")) {
                    formattedLine = formattedLine.replace("%team%", currentTeam);
                }

                Score score = obj.getScore(formattedLine);
                score.setScore(scoreValue--);
            }
            lastTeamInfo.put(player, currentTeam); // Mise à jour de la dernière équipe du joueur
        }
    }

    private void clearPreviousScores(Player player) {
        if (player.getScoreboard() != null) {
            for (String entry : player.getScoreboard().getEntries()) {
                player.getScoreboard().resetScores(entry);
            }
        }
    }


    private String repeat(String str, int count) {
        return new String(new char[count]).replace("\0", str);
    }

    private String getTeamColor(String teamName) {
        switch (teamName) {
            case "Rouge":
                return ChatColor.RED + teamName;
            case "Bleue":
                return ChatColor.BLUE + teamName;
            case "Verte":
                return ChatColor.GREEN + teamName;
            case "Jaune":
                return ChatColor.YELLOW + teamName;
            default:
                return ChatColor.WHITE + teamName;
        }
    }

}