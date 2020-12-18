package me.pikalegend.spdvshunt.runnables;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.pikalegend.spdvshunt.Main;
import me.pikalegend.spdvshunt.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class RunnerCompass extends BukkitRunnable {

	Scoreboard board;
	Team speedrunners;
	Team hunters;
	Objective overworldCoords;
	Objective netherCoords;
	Objective endCoords;
	Objective deaths;
	Objective compassSelector;
	Score playerSelect;
	Score playerDeath;
	Score playerX;
	Score playerY;
	Score playerZ;
	
	Player player;
	
	private Main plugin;

    public RunnerCompass(Main plugin, Player player, WeakReference<Scoreboard> boardRef) {
        this.plugin = plugin;
        this.player = player;
        
        board = (Scoreboard) boardRef.get();
		speedrunners = board.getTeam("svhSpeedrunners");
		hunters = board.getTeam("svhHunters");
		overworldCoords = board.getObjective("overworldCoords");
		netherCoords = board.getObjective("netherCoords");
		endCoords = board.getObjective("endCoords");
		deaths = board.getObjective("deaths");
		compassSelector = board.getObjective("compassSelector");
		playerSelect = compassSelector.getScore(player.getName());
		playerDeath = deaths.getScore(player.getName());
		
		playerDeath.setScore(0);
    }
    
 // Give Tracker Compass
 	public void giveCompass(Player player, int x, int y, int z, boolean confused) {
 		ItemStack compass = new ItemStack(Material.COMPASS);
 		ItemMeta meta = compass.getItemMeta();
 		
 		meta.setDisplayName(ChatColor.GOLD + "Tracker Compass");
 		meta.getPersistentDataContainer().set(key("tracker"), PersistentDataType.INTEGER, 1);
 		
 		CompassMeta trackerCompassMeta = (CompassMeta) meta;
 		
 		if (!confused) {
 			// If speedrunner was in this dimension. Else it would just give a normal unenchanted compass.
	 		Location location = new Location(player.getWorld(), x, y, z);
	 		trackerCompassMeta.setLodestone(location);
	 		trackerCompassMeta.setLodestoneTracked(false);
 		}
 		compass.setItemMeta(trackerCompassMeta);
 		
 		player.getInventory().setItem(8, compass);
 	}
 	
 	public void removeCompass(Player player) {		
		ItemStack[] inventory = player.getInventory().getContents();
		for (ItemStack item : inventory) {
			if (item != null) {
				if (item.getItemMeta().getPersistentDataContainer().has(key("tracker"), PersistentDataType.INTEGER)) {
					player.getInventory().remove(item);
					return;
				}
			}
		}
	}

	private NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }
 	
    @Override
    public void run() {
		// Check that speedrunners size is greater than 1
		if (speedrunners.getSize() > 1) {
			String[] speedrunnersArray = new String[speedrunners.getSize()];
			speedrunners.getEntries().toArray(speedrunnersArray);
			
			// If the current selection is the player itself, set it to some other player.
			if (speedrunnersArray[playerSelect.getScore()] == player.getName()) {
				// Player itself is currently selected. Iterate playerSelect score once more.
				if (speedrunners.getEntries().size() > playerSelect.getScore() + 1) {
					// Select the next Speedrunner
					playerSelect.setScore(playerSelect.getScore() + 1);
				} else {
					// Return to the first Speedrunner
					playerSelect.setScore(0);
				}
			}
			
			Player speedrunner = Bukkit.getPlayer(speedrunnersArray[playerSelect.getScore()]);
			
			World.Environment playerWorld = player.getWorld().getEnvironment();
			
			switch (playerWorld.toString()) {
			case "NORMAL":
				playerX = overworldCoords.getScore(speedrunner.getName() + "X");
				playerY = overworldCoords.getScore(speedrunner.getName() + "Y");
				playerZ = overworldCoords.getScore(speedrunner.getName() + "Z");
				break;
			case "NETHER":
				playerX = netherCoords.getScore(speedrunner.getName() + "X");
				playerY = netherCoords.getScore(speedrunner.getName() + "Y");
				playerZ = netherCoords.getScore(speedrunner.getName() + "Z");
				break;
			case "THE_END":
				playerX = endCoords.getScore(speedrunner.getName() + "X");
				playerY = endCoords.getScore(speedrunner.getName() + "Y");
				playerZ = endCoords.getScore(speedrunner.getName() + "Z");
				break;
			default:
				break;
			}
	
			// Update Compass and Action bar.
			if (!playerX.isScoreSet()) {
				// If player has not visited this dimension, thus the score is not yet set.
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getConfig().getString("trackingBar.noInfo").replace("<player>", speedrunner.getName()))));
				giveCompass(player, 0 , 0, 0, true);
			} else {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getConfig().getString("trackingBar.noInfo").replace("<player>", speedrunner.getName()))));
				giveCompass(player, playerX.getScore(), playerY.getScore(), playerZ.getScore(), false);
			}
			
		} else {
			// If not, then remove compass, as player is the only runner.
			removeCompass(player);
		}
    	
        if (!speedrunners.hasEntry(player.getName()) || playerDeath.getScore() > 0 || plugin.getConfig().getString("options.runnerCompass").equals("false")) {
        	this.cancel();
        	removeCompass(player);
        }
    }
}
