package collab.pikaandlucas.spdvshunt;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import collab.pikaandlucas.spdvshunt.commands.SpdVsHunt;
import collab.pikaandlucas.spdvshunt.listeners.ClickCompass;
import collab.pikaandlucas.spdvshunt.listeners.DeathPlayer;
import collab.pikaandlucas.spdvshunt.listeners.DisconnectPlayer;
import collab.pikaandlucas.spdvshunt.listeners.MoveCompass;
import collab.pikaandlucas.spdvshunt.listeners.MovePlayer;

public class Main extends JavaPlugin {
	WeakReference<Scoreboard> boardRef;
	
	public void setScoreboard() {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		board.registerNewTeam("svhSpeedrunners");
		board.registerNewTeam("svhHunters");
		board.registerNewObjective("overworldCoords", "dummy", "Overworld Coords");
		board.registerNewObjective("netherCoords", "dummy", "Nether Coords");
		board.registerNewObjective("endCoords", "dummy", "End Coords");
		board.registerNewObjective("deaths", "deathCount", "Deaths");
		board.registerNewObjective("compassSelector", "dummy", "Selector");
		
		boardRef = new WeakReference<>(board); 
	}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		setScoreboard();
		
		// List Commands and Listeners.
		new SpdVsHunt(this, boardRef);
		new MoveCompass(this, boardRef);
		new MovePlayer(this, boardRef);
		new ClickCompass(this, boardRef);
		new DeathPlayer(this, boardRef);
		new DisconnectPlayer(this, boardRef);
	}
}