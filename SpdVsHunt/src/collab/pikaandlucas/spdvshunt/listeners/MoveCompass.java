package collab.pikaandlucas.spdvshunt.listeners;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;
import collab.pikaandlucas.spdvshunt.utils.Utils;

// Click Compass prevents hunters from moving the compass / putting items on the eigth slot.
public class MoveCompass implements Listener {
	Scoreboard board;
	Team hunters;
	Team speedrunners;
	
	private Main plugin;

	public MoveCompass(Main plugin, WeakReference<Scoreboard> boardRef) {
		this.plugin = plugin;
		board = (Scoreboard) boardRef.get();
		hunters = board.getTeam("svhHunters");
		speedrunners = board.getTeam("svhSpeedrunners");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		// If player clicked is a hunter
		if (hunters.hasEntry(e.getWhoClicked().getName()) || (speedrunners.hasEntry(e.getWhoClicked().getName()) && speedrunners.getSize() > 1 && plugin.getConfig().getString("options.runnerCompass").equals("true"))) {
			if (e.getHotbarButton() == 8) {
				e.setCancelled(true);
			} else if (e.getSlot() == 8 && e.getClickedInventory().getItem(8) != null) {
				if (e.getClickedInventory().getItem(8).getItemMeta().getPersistentDataContainer().has(Utils.key(plugin, "tracker"), PersistentDataType.INTEGER)) {
					e.setCancelled(true);
				}
			} 
		}
	}
	
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent e) {
		// If player clicked is a hunter
		if (hunters.hasEntry(e.getPlayer().getName()) || (speedrunners.hasEntry(e.getPlayer().getName()) && speedrunners.getSize() > 1 && plugin.getConfig().getString("options.runnerCompass").equals("true"))) {
			if (e.getOffHandItem() != null) {
				if (e.getOffHandItem().getItemMeta().getPersistentDataContainer().has(Utils.key(plugin, "tracker"), PersistentDataType.INTEGER)) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		// If player clicked is a hunter
		if (hunters.hasEntry(e.getPlayer().getName()) || (speedrunners.hasEntry(e.getPlayer().getName()) && speedrunners.getSize() > 1 && plugin.getConfig().getString("options.runnerCompass").equals("true"))) {
			if (e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(Utils.key(plugin,"tracker"), PersistentDataType.INTEGER)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void itemFrameClick(PlayerInteractEntityEvent e){
		if (hunters.hasEntry(e.getPlayer().getName()) || (speedrunners.hasEntry(e.getPlayer().getName()) && speedrunners.getSize() > 1 && plugin.getConfig().getString("options.runnerCompass").equals("true"))) {
			Player player = e.getPlayer();
			Entity itemFrame = e.getRightClicked();
			if(itemFrame instanceof ItemFrame){
				if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
					if (player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Utils.key(plugin, "tracker"), PersistentDataType.INTEGER)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
}
