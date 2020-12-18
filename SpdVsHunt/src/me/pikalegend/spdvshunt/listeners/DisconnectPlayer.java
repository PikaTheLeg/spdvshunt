package me.pikalegend.spdvshunt.listeners;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.pikalegend.spdvshunt.Main;

public class DisconnectPlayer implements Listener {
	WeakReference<Scoreboard> boardRef;
	Scoreboard board;
	Team speedrunners;
	Team hunters;
	Objective deaths;
	
	private Main plugin;
	
	public DisconnectPlayer(Main plugin, WeakReference<Scoreboard> boardRef) {
		this.plugin = plugin;
		this.boardRef = boardRef;

		board = (Scoreboard) boardRef.get();
		speedrunners = board.getTeam("svhSpeedrunners");
		hunters = board.getTeam("svhHunters");
		deaths = board.getObjective("deaths");

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		// On Disconnect, player is removed from teams.
		Player player = e.getPlayer();
		
		speedrunners.removeEntry(player.getName());
		hunters.removeEntry(player.getName());
		
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
	
}
