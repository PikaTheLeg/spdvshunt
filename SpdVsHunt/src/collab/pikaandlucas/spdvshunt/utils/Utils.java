package collab.pikaandlucas.spdvshunt.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;

public class Utils {
	
	public static String chat (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static void brodcastTitle(@Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
		Player[] players = (Player[]) Bukkit.getOnlinePlayers().toArray();
		
		for (Player player : players) {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void brodcastTitle(@Nullable String title, @Nullable String subtitle) {
		Player[] players = (Player[]) Bukkit.getOnlinePlayers().toArray();
		
		for (Player player : players) {
			player.sendTitle(title, subtitle);
		}
	}
	
	// Give Tracker Compass
 	public static void giveCompass(Main plugin, Player player, int x, int y, int z, boolean confused) {
 		// create a compass item
 		ItemStack compass = new ItemStack(Material.COMPASS);
 		
 		// get item meta data
 		ItemMeta meta = compass.getItemMeta();
 		// set gold display name
 		meta.setDisplayName(ChatColor.GOLD + "Tracker Compass");
 		
 		// set custom metadata in plugin namespace identifying this compass as a tracker compass
 		meta.getPersistentDataContainer().set(Utils.key(plugin, "tracker"), PersistentDataType.INTEGER, 1);
 		
 		// cast item metadata into compass meta data
 		CompassMeta trackerCompassMeta = (CompassMeta) meta;
 		
 		// if the compass is not confused
 		if (!confused) {
 			// If speedrunner was in this dimension. Else it would just give a normal unenchanted compass.
	 		Location location = new Location(player.getWorld(), x, y, z);
	 		trackerCompassMeta.setLodestone(location);
	 		trackerCompassMeta.setLodestoneTracked(false);
 		}
 		// set the compass' metadata
 		compass.setItemMeta(trackerCompassMeta);

 		// get item in the appropriate inventory slot
		ItemStack item = player.getInventory().getItem(8);
 		
		// If player has an item, not air, not the tracker, at the 9th slot, drop item on the ground.
 		if (item != null && !item.getType().equals(Material.AIR)) {
 			// if the player does not have a tracker compass in the appropriate spot
 			if (!player.getInventory().getItem(8).getItemMeta().getPersistentDataContainer().has(key(plugin, "tracker"), PersistentDataType.INTEGER)) {
	 			// Drop the item on the ground.
	 			Location location = player.getLocation();
	 			player.getWorld().dropItemNaturally(location, item);
	 		}
 		}
 		
 		// give the player the tracker compass
 		player.getInventory().setItem(8, compass);
 	}
	
 	// removes tracker compass from the player's inventory
	public static void removeCompass(Main plugin, Player player) {
		// get the player's inventory
		ItemStack[] inventory = player.getInventory().getContents();
		
		// for each item in the player's inventory
		for (ItemStack item : inventory) {
			// if the item is not null/air
			if (item != null) {
				// check if the item is the tracker compass
				if (item.getItemMeta().getPersistentDataContainer().has(key(plugin, "tracker"), PersistentDataType.INTEGER)) {
					// remove the tracker compass from the inventory
					player.getInventory().remove(item);
					return;
				}
			}
		}
	}
	
	public static NamespacedKey key(Main plugin, String key) {
        return new NamespacedKey(plugin, key);
    }
	
	// Generates a list of live speedrunners from the speedrunners team.
	public static ArrayList<String> aliveRunners(Scoreboard board) {
		// initialise list of alive speedrunners
		ArrayList<String> list = new ArrayList<String>();

		// get the speedrunner team
		Team speedrunners = board.getTeam("svhSpeedrunners");
		// get the deaths objective
		Objective deaths = board.getObjective("deaths");
		
		// initialise a string array of speedrunners
		String[] speedrunnersArray = new String[speedrunners.getSize()];
		// save entries to array
		speedrunners.getEntries().toArray(speedrunnersArray);
		
		// for each speedrunner
		for (String speedrunnerName : speedrunnersArray) {
			// if death count is still zero
			if (deaths.getScore(speedrunnerName).getScore() == 0) {
				// add them to the alive speedrunner list
				list.add(speedrunnerName);
			}
		}
		
		// return resulting list of alive speedrunners
		return list;
	}
	
	public static int[] timeFormat(int inputSeconds) {
		// Returns time from seconds into hh, mm and seconds, as an Array.
		// int[0] = hours, int[1] = minutes, int[2] = seconds.
		int[] time = new int[3];
		
		time[0] = inputSeconds / 3600; // Hours
		time[1] = (inputSeconds % 3600) / 60; // Minutes
		time[2] = inputSeconds % 60; // Seconds
		
		return time;
	}
	
}