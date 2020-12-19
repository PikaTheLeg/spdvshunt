package collab.pikaandlucas.spdvshunt.utils;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
	
	 // Give Tracker Compass
 	public static void giveCompass(Main plugin, Player player, int x, int y, int z, boolean confused) {
 		ItemStack compass = new ItemStack(Material.COMPASS);
 		ItemMeta meta = compass.getItemMeta();
 		
 		meta.setDisplayName(ChatColor.GOLD + "Tracker Compass");
 		meta.getPersistentDataContainer().set(Utils.key(plugin, "tracker"), PersistentDataType.INTEGER, 1);
 		
 		CompassMeta trackerCompassMeta = (CompassMeta) meta;
 		
 		if (!confused) {
 			// If speedrunner was in this dimension. Else it would just give a normal unenchanted compass.
	 		Location location = new Location(player.getWorld(), x, y, z);
	 		trackerCompassMeta.setLodestone(location);
	 		trackerCompassMeta.setLodestoneTracked(false);
 		}
 		compass.setItemMeta(trackerCompassMeta);

		ItemStack item = player.getInventory().getItem(8);
 		
		// If player has an item, not air, not the tracker, at the 9th slot, drop item on the ground.
 		if (item != null && !item.getType().equals(Material.AIR)) {
 			if (!player.getInventory().getItem(8).getItemMeta().getPersistentDataContainer().has(key(plugin, "tracker"), PersistentDataType.INTEGER)) {
	 			// Drop the item on the ground.
	 			Location location = player.getLocation();
	 			
	 			player.getWorld().dropItemNaturally(location, item);
	 		}
 		}
 		
 		player.getInventory().setItem(8, compass);
 	}
	
	public static void removeCompass(Main plugin, Player player) {		
		ItemStack[] inventory = player.getInventory().getContents();
		for (ItemStack item : inventory) {
			if (item != null) {
				if (item.getItemMeta().getPersistentDataContainer().has(key(plugin, "tracker"), PersistentDataType.INTEGER)) {
					player.getInventory().remove(item);
					return;
				}
			}
		}
	}
	
	public static NamespacedKey key(Main plugin, String key) {
        return new NamespacedKey(plugin, key);
    }
	
	public static ArrayList<String> aliveRunners(Scoreboard board) {
		// Generates a list of live speedrunners from the speedrunners team.
		ArrayList<String> list = new ArrayList<String>();

		Team speedrunners = board.getTeam("svhSpeedrunners");
		Objective deaths = board.getObjective("deaths");
		
		String[] speedrunnersArray = new String[speedrunners.getSize()];
		speedrunners.getEntries().toArray(speedrunnersArray);
		
		for (String speedrunnerName : speedrunnersArray) {
			if (deaths.getScore(speedrunnerName).getScore() == 0) {
				list.add(speedrunnerName);
			}
		}
		
		return list;
	}
	
}