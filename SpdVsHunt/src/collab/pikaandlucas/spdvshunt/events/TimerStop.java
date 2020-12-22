package collab.pikaandlucas.spdvshunt.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerStop extends Event {
	private int secsRemain;
	private boolean isComplete;
	private int taskID;
	
	public TimerStop(int secs, int taskID) {
		this.secsRemain = secs;
		this.taskID = taskID;
		if (secs <= 0) {
			this.isComplete = true;
		}
		else {
			this.isComplete = false;
		}
	}
	
	public int getSecsRemain() {
		return secsRemain;
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
