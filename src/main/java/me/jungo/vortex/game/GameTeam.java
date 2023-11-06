package me.jungo.vortex.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GameTeam {
    private Set<Player> members;
    private HashMap<Player, Integer> lives;
    private boolean isActive;
    private String color; // Champ pour la couleur de l'équipe
    private Location spawnLocation;

    // Constructeur modifié pour accepter la couleur
    public GameTeam(String color) {
        this.members = new HashSet<>();
        this.lives = new HashMap<>();
        this.isActive = true;
        this.color = color; // Assigner la couleur
    }

    // Méthodes pour ajouter, enlever des joueurs, et gérer les vies
    public void addPlayer(Player player, int initialLives) {
        members.add(player);
        lives.put(player, initialLives);
    }

    public void removePlayer(Player player) {
        members.remove(player);
        lives.remove(player);
        if (members.isEmpty()) {
            isActive = false;
        }
    }

    public void loseLife(Player player) {
        if (lives.containsKey(player)) {
            int remainingLives = lives.get(player) - 1;
            if (remainingLives <= 0) {
                removePlayer(player);
            } else {
                lives.put(player, remainingLives);
            }
        }
    }

    // Méthodes pour obtenir le statut et la couleur de l'équipe
    public boolean isActive() {
        return isActive;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }
}
