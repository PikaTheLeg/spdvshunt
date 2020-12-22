package collab.pikaandlucas.spdvshunt.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import collab.pikaandlucas.spdvshunt.events.TimerStop;

public class TimerStopListener implements Listener {
	
	@EventHandler
	public void onTimerStop(TimerStop e) {
		Bukkit.broadcastMessage("timer has stoped");
		
		int taskID = e.getTaskID();
		
		Bukkit.getServer().getScheduler().cancelTask(taskID);
	}
}