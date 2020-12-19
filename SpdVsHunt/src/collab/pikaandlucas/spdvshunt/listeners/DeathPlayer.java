package collab.pikaandlucas.spdvshunt.listeners;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;
import collab.pikaandlucas.spdvshunt.runnables.HunterCompass;
import collab.pikaandlucas.spdvshunt.utils.Utils;

public class DeathPlayer implements Listener {
	WeakReference<Scoreboard> boardRef;
	Scoreboard board;
	Team speedrunners;
	Team hunters;
	Objective deaths;
	
	private Main plugin;
	
	public DeathPlayer(Main plugin, WeakReference<Scoreboard> boardRef) {
		this.plugin = plugin;
		this.boardRef = boardRef;

		board = (Scoreboard) boardRef.get();
		speedrunners = board.getTeam("svhSpeedrunners");
		hunters = board.getTeam("svhHunters");
		deaths = board.getObjective("deaths");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		
		// Check if it's speedrunner.
		if (speedrunners.hasEntry(e.getEntity().getName())) {
			// Ensures their scoreboard is set. Prevents bug where a speedrunner died but objective is not updated.
			deaths.getScore(e.getEntity().getName()).setScore(1); 

			ArrayList<String> aliveRunners = Utils.aliveRunners(board);
			if (aliveRunners.size() > 0) {
				Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.runnerDied").replace("<player>", e.getEntity().getName()).replace("<number>", aliveRunners.size()+"")));
			} else {
				// Hunters Win!
				Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.runnersLost").replace("<player>", e.getEntity().getName())));
			}
			
			// Check for compass drop, and deletes it.
			List<ItemStack> drops = e.getDrops();
			for (ItemStack drop : drops) {
				if (drop.getItemMeta().getPersistentDataContainer().has(Utils.key(plugin, "tracker"), PersistentDataType.INTEGER)) {
					drop.setAmount(0);
				}
			}
			
		} else if (hunters.hasEntry(e.getEntity().getName())) {
			List<ItemStack> drops = e.getDrops();
			for (ItemStack drop : drops) {
				if (drop.getItemMeta().getPersistentDataContainer().has(Utils.key(plugin, "tracker"), PersistentDataType.INTEGER)) {
					drop.setAmount(0);
				}
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		// Check if it's hunter.
		if (hunters.hasEntry(e.getPlayer().getName())) {
			Player player = e.getPlayer();
			// Reboot compass.
			new HunterCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
		}
	}
	
	@EventHandler
	public void onEnderDragonDeath(EntityDeathEvent e){
	     if(e.getEntity() instanceof EnderDragon){
	         // Runners Win!
	    	 Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.runnersWin")));
	    }
	}
}
