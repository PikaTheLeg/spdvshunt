package collab.pikaandlucas.spdvshunt.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import collab.pikaandlucas.spdvshunt.enums.ClockType;

public class TimerStop extends Event {
	private int secsRemain;
	private boolean isComplete;
	private int taskID;
	private ClockType type;
	
	public TimerStop(int secs, int taskID, ClockType type) {
		this.secsRemain = secs;
		this.taskID = taskID;
		this.type = type;
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
	
	public ClockType getClockType() {
		return type;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}