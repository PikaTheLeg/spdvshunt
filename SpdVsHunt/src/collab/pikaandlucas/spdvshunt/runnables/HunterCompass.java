package collab.pikaandlucas.spdvshunt.runnables;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;
import collab.pikaandlucas.spdvshunt.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class HunterCompass extends BukkitRunnable {

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

    public HunterCompass(Main plugin, Player player, WeakReference<Scoreboard> boardRef) {
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
 	
    @Override
    public void run() {
        // While running, update Compass in Slot 8 to display coordinates of Speedrunners (if any).
		String spdrunWorld;
		String worldName;
		ArrayList<String> aliveRunners = Utils.aliveRunners(board);
	
		if (aliveRunners.size() > 0) {
			// If current selection is out of bounds. Set to 0.
			if (aliveRunners.size() <= playerSelect.getScore()) {
				playerSelect.setScore(0);
			}
			
			Player speedrunner = Bukkit.getPlayer(aliveRunners.get(playerSelect.getScore()));
			
			World.Environment playerWorld = player.getWorld().getEnvironment();
			World.Environment speedrunnerWorld = speedrunner.getWorld().getEnvironment();
			
			switch (playerWorld.toString()) {
			case "NORMAL":
				playerX = overworldCoords.getScore(speedrunner.getName() + "X");
				playerY = overworldCoords.getScore(speedrunner.getName() + "Y");
				playerZ = overworldCoords.getScore(speedrunner.getName() + "Z");
				worldName = "Overworld";
				break;
			case "NETHER":
				playerX = netherCoords.getScore(speedrunner.getName() + "X");
				playerY = netherCoords.getScore(speedrunner.getName() + "Y");
				playerZ = netherCoords.getScore(speedrunner.getName() + "Z");
				worldName = "Nether";
				break;
			case "THE_END":
				playerX = endCoords.getScore(speedrunner.getName() + "X");
				playerY = endCoords.getScore(speedrunner.getName() + "Y");
				playerZ = endCoords.getScore(speedrunner.getName() + "Z");
				worldName = "The End";
				break;
			default:
				worldName = "Error";
				break;
			}
			
			switch (speedrunnerWorld.toString()) {
				case "NORMAL":
					spdrunWorld = "Overworld";
					break;
				case "NETHER":
					spdrunWorld = "Nether";
					break;
				case "THE_END":
					spdrunWorld = "The End";
					break;
				default:
					spdrunWorld = "Error";
					break;
			}
	
			if (plugin.getConfig().getString("options.hunterBar").equals("true")) {
				if (playerWorld == speedrunnerWorld) {
					// Speedrunner in same world as hunter.
					Location location = new Location(player.getWorld(), playerX.getScore(), playerY.getScore(), playerZ.getScore());
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.sameWorld").replace("<player>", speedrunner.getName()).replace("<world>", spdrunWorld).replace("<distance>", Math.round(player.getLocation().distance(location)) + ""))));
					Utils.giveCompass(plugin, player, playerX.getScore(), playerY.getScore(), playerZ.getScore(), false);
				} else if (!playerX.isScoreSet()) {
					// Score not set. Speedrunner has not visited the place.
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.notVisited").replace("<player>", speedrunner.getName()).replace("<world>", spdrunWorld).replace("<dimension>", worldName))));
					Utils.giveCompass(plugin, player, 0, 0, 0, true);
				} else {
					// Score is set. Speedrunner is in different world.	
					Location location = new Location(player.getWorld(), playerX.getScore(), playerY.getScore(), playerZ.getScore());
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.differentWorld").replace("<player>", speedrunner.getName()).replace("<world>", spdrunWorld).replace("<distance>", Math.round(player.getLocation().distance(location)) + ""))));
					Utils.giveCompass(plugin, player, playerX.getScore(), playerY.getScore(), playerZ.getScore(), false);
				}
			} else {
				if (!playerX.isScoreSet()) {
					// If player has not visited this dimension, thus the score is not yet set.
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.noInfo").replace("<player>", speedrunner.getName()))));
					Utils.giveCompass(plugin, player, 0, 0, 0, true);
				} else {
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.noInfo").replace("<player>", speedrunner.getName()))));
					Utils.giveCompass(plugin, player, playerX.getScore(), playerY.getScore(), playerZ.getScore(), false);
				}
			}
		} else {
			// No Alive Speedrunners. Send Appropriate Action Bar.
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.noSpeedrun"))));
			Utils.giveCompass(plugin, player, 0, 0, 0, true);
		}
    	
    	
        if (!hunters.hasEntry(player.getName()) || playerDeath.getScore() > 0) {
        	this.cancel();
        	Utils.removeCompass(plugin, player);
        }
    }
}
