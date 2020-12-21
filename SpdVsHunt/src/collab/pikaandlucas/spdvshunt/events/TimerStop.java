package collab.pikaandlucas.spdvshunt.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerStop extends Event {
	private int ticksRemain;
	private boolean isComplete;
	private BukkitRunnable runnable;
	
	public TimerStop(int ticks, BukkitRunnable runnable) {
		this.ticksRemain = ticks;
		this.runnable = runnable;
		if (ticks <= 0) {
			this.isComplete = true;
		}
		else {
			this.isComplete = false;
		}
	}
	
	public int getTicksRemain() {
		return ticksRemain;
	}
	
	public boolean getIsComplete() {
		return isComplete;
	}
	
	public BukkitRunnable getRunnable() {
		return runnable;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
