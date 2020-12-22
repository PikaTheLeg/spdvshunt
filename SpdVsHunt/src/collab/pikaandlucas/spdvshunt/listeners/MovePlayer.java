package collab.pikaandlucas.spdvshunt.listeners;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;

public class MovePlayer implements Listener {
	Scoreboard board;
	Team speedrunners;
	Objective overworldCoords;
	Objective netherCoords;
	Objective endCoords;
	Score playerX;
	Score playerY;
	Score playerZ;
	
	public MovePlayer(Main plugin, WeakReference<Scoreboard> boardRef) {
		board = (Scoreboard) boardRef.get();
		speedrunners = board.getTeam("svhSpeedrunners");
		overworldCoords = board.getObjective("overworldCoords");
		netherCoords = board.getObjective("netherCoords");
		endCoords = board.getObjective("endCoords");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		
		if (speedrunners.hasEntry(player.getName())) {
			Location location = e.getTo();
			
			World.Environment world = location.getWorld().getEnvironment();
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			
			switch (world.toString()) {
				case "NORMAL":
					playerX = overworldCoords.getScore(player.getName() + "X");
					playerY = overworldCoords.getScore(player.getName() + "Y");
					playerZ = overworldCoords.getScore(player.getName() + "Z");
					break;
				case "NETHER":
					playerX = netherCoords.getScore(player.getName() + "X");
					playerY = netherCoords.getScore(player.getName() + "Y");
					playerZ = netherCoords.getScore(player.getName() + "Z");
					break;
				case "THE_END":
					playerX = endCoords.getScore(player.getName() + "X");
					playerY = endCoords.getScore(player.getName() + "Y");
					playerZ = endCoords.getScore(player.getName() + "Z");
					break;
				default:
					break;
			}
			
			playerX.setScore(x);
			playerY.setScore(y);
			playerZ.setScore(z);
		}
	}
}
