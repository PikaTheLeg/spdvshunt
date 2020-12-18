package me.pikalegend.spdvshunt.listeners;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.pikalegend.spdvshunt.Main;
import me.pikalegend.spdvshunt.utils.Utils;

public class ClickCompass implements Listener {
	
	Scoreboard board;
	Team speedrunners;
	Team hunters;
	Objective overworldCoords;
	Objective netherCoords;
	Objective endCoords;
	Objective deaths;
	Objective compassSelector;
	Score playerSelect;
	
	private Main plugin;
	
	public ClickCompass(Main plugin, WeakReference<Scoreboard> boardRef) {
		this.plugin = plugin;

		board = (Scoreboard) boardRef.get();
		speedrunners = board.getTeam("svhSpeedrunners");
		hunters = board.getTeam("svhHunters");
		overworldCoords = board.getObjective("overworldCoords");
		netherCoords = board.getObjective("netherCoords");
		endCoords = board.getObjective("endCoords");
		deaths = board.getObjective("deaths");
		compassSelector = board.getObjective("compassSelector");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (hunters.hasEntry(e.getPlayer().getName())) {
			// Checks that the player is participating in SpdVsHunt
			Player player = e.getPlayer();
			
			// Check the player has an item.
			if (e.getItem() != null) {
				// Check item to be the compass.
				if (e.getItem().getItemMeta().getPersistentDataContainer().has(key("tracker"), PersistentDataType.INTEGER) && (e.getAction() != null && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
					// Then player is clicking with the compass.
					playerSelect = compassSelector.getScore(player.getName());

					if (speedrunners.getEntries().size() > playerSelect.getScore() + 1) {
						// Select the next Speedrunner
						playerSelect.setScore(playerSelect.getScore() + 1);
					} else {
						// Return to the first Speedrunner
						playerSelect.setScore(0);
					}
					
					if (speedrunners.getSize() > 0) {
						String[] speedrunnersArray = new String[speedrunners.getSize()];
						speedrunners.getEntries().toArray(speedrunnersArray);
						player.sendMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.nowTracking").replace("<player>", speedrunnersArray[playerSelect.getScore()])));
					} else {
						player.sendMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.notTracking")));
					}
				}
			}
		} else if (speedrunners.hasEntry(e.getPlayer().getName()) && speedrunners.getSize() > 1 && plugin.getConfig().getString("options.runnerCompass").equals("true")) {
			// If: There are multiple speedrunners, player is a speedrunner and speedrunners can have compasses.
			Player player = e.getPlayer();
			
			if (e.getItem() != null) {
				// Check item to be the compass.
				if (e.getItem().getItemMeta().getPersistentDataContainer().has(key("tracker"), PersistentDataType.INTEGER) && (e.getAction() != null && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
					// Player is right clicking the tracking compass.
					playerSelect = compassSelector.getScore(player.getName());

					// Cycle playerSelect score.
					if (speedrunners.getEntries().size() > playerSelect.getScore() + 1) {
						// Select the next Speedrunner
						playerSelect.setScore(playerSelect.getScore() + 1);
					} else {
						// Return to the first Speedrunner
						playerSelect.setScore(0);
					}
					
					// Create Speedrunners Array.
					String[] speedrunnersArray = new String[speedrunners.getSize()];
					speedrunners.getEntries().toArray(speedrunnersArray);
					
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
					
					// Send message on who they are currently tracking. 
					player.sendMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.nowTracking").replace("<player>", speedrunnersArray[playerSelect.getScore()])));
				}
			}
		}
	}
}
