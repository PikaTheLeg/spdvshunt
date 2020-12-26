package collab.pikaandlucas.spdvshunt.runnables;

import java.lang.ref.WeakReference;

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

public class MovePlayer extends BukkitRunnable {
	Scoreboard board;
	Team speedrunners;
	Objective overworldCoords;
	Objective netherCoords;
	Objective endCoords;
	Score playerX;
	Score playerY;
	Score playerZ;
	
	Player player;
	
	public MovePlayer(Main plugin, Player player, WeakReference<Scoreboard> boardRef) {
		this.player = player;
		
		board = (Scoreboard) boardRef.get();
		speedrunners = board.getTeam("svhSpeedrunners");
		overworldCoords = board.getObjective("overworldCoords");
		netherCoords = board.getObjective("netherCoords");
		endCoords = board.getObjective("endCoords");
	}
	
	@Override
	public void run() {
		if (Utils.aliveRunners(board).indexOf(player.getName()) != -1) {
			Location location = player.getLocation();
			
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
		} else {
			this.cancel();
		}
	}
}
