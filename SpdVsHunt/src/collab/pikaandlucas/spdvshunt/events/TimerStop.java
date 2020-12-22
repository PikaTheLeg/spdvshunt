package collab.pikaandlucas.spdvshunt.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerStop extends Event {
	private int ticksRemain;
	private boolean isComplete;
	private int taskID;
	
	public TimerStop(int ticks, int taskID) {
		this.ticksRemain = ticks;
		this.taskID = taskID;
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
	
	public int getTaskID() {
		return taskID;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}