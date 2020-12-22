package collab.pikaandlucas.spdvshunt.runnables;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;

public class SidebarScoreboard extends BukkitRunnable {
	Scoreboard board;
	Objective sidebar;
	Objective seconds;
	Team speedrunners;
	Team hunters;
	ArrayList<Score> displayScores = new ArrayList<Score>();
	
	public SidebarScoreboard(Main plugin, WeakReference<Scoreboard> boardRef) {
		board = (Scoreboard) boardRef.get();
		sidebar = board.getObjective("sidebar");
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	@Override
	public void run() {
		// Update board
		
		
		// Set board to all players online.
		
	}
	
}
