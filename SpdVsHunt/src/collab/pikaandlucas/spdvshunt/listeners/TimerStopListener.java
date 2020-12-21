package collab.pikaandlucas.spdvshunt.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import collab.pikaandlucas.spdvshunt.events.TimerStop;

public class TimerStopListener implements Listener {
	
	@EventHandler
	public void onTimerStop(TimerStop e) {
		Bukkit.broadcastMessage("timer has stoped");
		
		BukkitRunnable runable = e.getRunnable();
		
		runable.cancel();
	}
}