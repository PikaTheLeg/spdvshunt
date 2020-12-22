package collab.pikaandlucas.spdvshunt.runnables;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import collab.pikaandlucas.spdvshunt.Main;
import collab.pikaandlucas.spdvshunt.events.TimerStop;

public class TimerRunable extends BukkitRunnable {
	Scoreboard board;
	Objective timer;
	
	
	public TimerRunable(Main plugin, WeakReference<Scoreboard> boardRef) {
        
        board = (Scoreboard) boardRef.get();
		timer = board.getObjective("timer");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		/* 
		 * https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTaskTimer(org.bukkit.plugin.Plugin,java.lang.Runnable,long,long)
		 * 
		 * 
		 */
		// while running update the timer on scoreboard
		
		int secs = timer.getScore("global").getScore();
		if (secs <= 0) {
			Bukkit.getServer().getPluginManager().callEvent(new TimerStop(secs, this.getTaskId()));
			return;
		}
		
		secs -= 1;
		timer.getScore("global").setScore(secs);
	}
}
