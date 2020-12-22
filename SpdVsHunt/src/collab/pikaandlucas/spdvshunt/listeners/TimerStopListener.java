package collab.pikaandlucas.spdvshunt.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import collab.pikaandlucas.spdvshunt.events.TimerStop;
import collab.pikaandlucas.spdvshunt.utils.Utils;

public class TimerStopListener implements Listener {
	
	@EventHandler
	public void onTimerStop(TimerStop e) {
		Utils.brodcastTitle("Timmer has stopped", null);
		
		int taskID = e.getTaskID();
		
		Bukkit.getServer().getScheduler().cancelTask(taskID);
	}
}