package collab.pikaandlucas.spdvshunt.runnables;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;
import collab.pikaandlucas.spdvshunt.utils.Utils;

public class SidebarScoreboard extends BukkitRunnable {
	Scoreboard board;
	Score seconds;
	Team hunters;
	ArrayList<String> displayEntries;
			
	public SidebarScoreboard(Main plugin, WeakReference<Scoreboard> boardRef) {
		board = (Scoreboard) boardRef.get();
		seconds = board.getObjective("timer").getScore("global");
		hunters = board.getTeam("svhHunters");
	}
	
	@Override
	public void run() {
		Scoreboard display = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective sidebar = display.registerNewObjective("sidebar", "dummy", Utils.chat("&6Speedrunner Vs Hunter"));
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		// Update board
		ArrayList<String> displayScores = new ArrayList<String>();
		
		if (seconds.isScoreSet() && seconds.getScore() > 0) {
			// Timer is running
			int[] time = Utils.timeFormat(seconds.getScore());
			String[] timeString = new String[3];
			
			for (int i = 0; i < 3; i++) {
				if (time[i] < 10) {
					timeString[i] = "0" + time[i];
				} else {
					timeString[i] = "" + time[i];
				}
			}
			
			displayScores.add(Utils.chat("&bTimer: &a"+timeString[0]+":"+timeString[1]+":"+timeString[2]));
		} else {
			// Timer is not running
			displayScores.add(Utils.chat("&bTimer not running."));
		}
		
		displayScores.add(Utils.chat("Testing!"));
		
		for (String string : displayScores) {
			sidebar.getScore(string).setScore(displayScores.size() - displayScores.indexOf(string));
		}
		
		// Set board to all players online.
		Player[] onlineArray = new Player[Bukkit.getOnlinePlayers().size()];
		Bukkit.getOnlinePlayers().toArray(onlineArray);
		
		for (Player player : onlineArray) {
			player.setScoreboard(display);
		}
	}
	
}
