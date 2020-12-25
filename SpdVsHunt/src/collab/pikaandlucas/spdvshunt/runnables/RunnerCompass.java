package collab.pikaandlucas.spdvshunt.runnables;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.bukkit.Bukkit;
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
 	
    @Override
    public void run() {
		ArrayList<String> aliveRunners = Utils.aliveRunners(board);
		// Check that speedrunners size is greater than 1
		if (aliveRunners.size() > 1) {			
			
			// If current selection is out of bounds. Set to 0.
			if (aliveRunners.size() >= playerSelect.getScore()) {
				playerSelect.setScore(0);
			}
			
			// If the current selection is the player itself, set it to some other player.
			if (aliveRunners.get(playerSelect.getScore()) == player.getName()) {
				// Player itself is currently selected. Iterate playerSelect score once more.
				if (aliveRunners.size() > playerSelect.getScore() + 1) {
					// Select the next Speedrunner
					playerSelect.setScore(playerSelect.getScore() + 1);
				}
				else {
					// Return to the first Speedrunner
					playerSelect.setScore(0);
				}
			}
			
			Player speedrunner = Bukkit.getPlayer(aliveRunners.get(playerSelect.getScore()));
			
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
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.noInfo").replace("<player>", speedrunner.getName()))));
				Utils.giveCompass(plugin, player, 0 , 0, 0, true);
			} else {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(plugin.getMessages().getString("trackingBar.noInfo").replace("<player>", speedrunner.getName()))));
				Utils.giveCompass(plugin, player, playerX.getScore(), playerY.getScore(), playerZ.getScore(), false);
			}
			
		} else {
			// If not, then remove compass, as player is the only runner.
			Utils.removeCompass(plugin, player);
		}
    	
        if (aliveRunners.indexOf(player.getName()) == -1 || plugin.getConfig().getString("options.runnerCompass").equals("false")) {
        	this.cancel();
			Utils.removeCompass(plugin, player);
        }
    }
}
