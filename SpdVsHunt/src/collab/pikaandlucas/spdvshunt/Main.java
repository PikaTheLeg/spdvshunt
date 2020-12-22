package collab.pikaandlucas.spdvshunt;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import collab.pikaandlucas.spdvshunt.commands.SpdVsHunt;
import collab.pikaandlucas.spdvshunt.listeners.ClickCompass;
import collab.pikaandlucas.spdvshunt.listeners.DeathPlayer;
import collab.pikaandlucas.spdvshunt.listeners.DisconnectPlayer;
import collab.pikaandlucas.spdvshunt.listeners.MoveCompass;
import collab.pikaandlucas.spdvshunt.listeners.MovePlayer;
import collab.pikaandlucas.spdvshunt.listeners.TimerStopListener;

public class Main extends JavaPlugin {
	
	WeakReference<Scoreboard> boardRef;
	
	private File messagesFile;
	private FileConfiguration messages;
	
	public FileConfiguration getMessages() {
		// Returns messages file to get messages.
		return this.messages;
	}
	
	private void createMessages() {
		// Create file for messages.yml
		messagesFile = new File(getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			// If message file does not exist. Create message file.
			messagesFile.getParentFile().mkdirs();
			saveResource("messages.yml", false);
		}
		
		messages = new YamlConfiguration();
		try {
			messages.load(messagesFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void setScoreboard() {
		// create new scoreboard
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		
		// create hunters and speedrunner teams
		board.registerNewTeam("svhSpeedrunners");
		board.registerNewTeam("svhHunters");
		
		// create objectives to track position in the dimensions
		board.registerNewObjective("overworldCoords", "dummy", "Overworld Coords");
		board.registerNewObjective("netherCoords", "dummy", "Nether Coords");
		board.registerNewObjective("endCoords", "dummy", "End Coords");
		
		// create objective to track death counts
		board.registerNewObjective("deaths", "deathCount", "Deaths");
		
		// create objective to to hold player index to be tracked
		board.registerNewObjective("compassSelector", "dummy", "Selector");
		
		board.registerNewObjective("timer", "dummy", "Timer");
		boardRef = new WeakReference<>(board);
	}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		createMessages();
		setScoreboard();
		
		// List Commands and Listeners.
		new SpdVsHunt(this, boardRef);
		new MoveCompass(this, boardRef);
		new MovePlayer(this, boardRef);
		new ClickCompass(this, boardRef);
		new DeathPlayer(this, boardRef);
		new DisconnectPlayer(this, boardRef);
		Bukkit.getServer().getPluginManager().registerEvents(new TimerStopListener(), this);
	}
}
