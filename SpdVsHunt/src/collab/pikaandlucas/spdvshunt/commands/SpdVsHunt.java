package collab.pikaandlucas.spdvshunt.commands;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import collab.pikaandlucas.spdvshunt.Main;
import collab.pikaandlucas.spdvshunt.events.TimerStop;
import collab.pikaandlucas.spdvshunt.runnables.HunterCompass;
import collab.pikaandlucas.spdvshunt.runnables.RunnerCompass;
import collab.pikaandlucas.spdvshunt.runnables.TimerRunable;
import collab.pikaandlucas.spdvshunt.utils.Utils;


public class SpdVsHunt implements CommandExecutor {
	WeakReference<Scoreboard> boardRef;
	Scoreboard board;
	Team speedrunners;
	Team hunters;
	Objective overworldCoords;
	Objective netherCoords;
	Objective endCoords;
	Objective deaths;
	Objective compassSelector;
	Objective timer;
	BukkitTask timerTask;
	WorldBorder Border;
	
	private Main plugin;
	
	public SpdVsHunt(Main plugin, WeakReference<Scoreboard> boardRef) {
		this.plugin = plugin;
		this.boardRef = boardRef;
		
		// Get scoreboard object.
		board = (Scoreboard) boardRef.get();
		speedrunners = board.getTeam("svhSpeedrunners");
		hunters = board.getTeam("svhHunters");
		overworldCoords = board.getObjective("overworldCoords");
		netherCoords = board.getObjective("netherCoords");
		endCoords = board.getObjective("endCoords");
		deaths = board.getObjective("deaths");
		compassSelector = board.getObjective("compassSelector");
		timer = board.getObjective("timer");
		
		plugin.getCommand("spdVsHunt").setExecutor(this);
		plugin.getCommand("spdVsHunt").setTabCompleter(new SVHTabComplete());
	}
	
	// Resets the game entirely
	public void resetSVH() {
		// for each entry in scoreboard
		for (String entry : board.getEntries()) {
			// reset to default score
			board.resetScores(entry);
		}
		
		// for each team present in the scoreboard
		for (Team team : board.getTeams()) {
			// for each player in the team
			for (String entry : team.getEntries()) {
				// remove player from the team
				team.removeEntry(entry);
			}
		}
		
		// initalise player array
		Player[] onlineArray = new Player[Bukkit.getOnlinePlayers().size()];
		// recreate online player array
		Bukkit.getOnlinePlayers().toArray(onlineArray);
		
		// for each player in the online array
		for (Player player : onlineArray) {
			// remove tracker compass
			Utils.removeCompass(plugin, player);
		}
	}
	
	// allows player to join hunter or speedrunner team
	public boolean joinTeam(CommandSender sender, Player player, String teamString) {
		
		// switch on team string supplied
		switch (teamString.toLowerCase()) {
		// if player is to join the speedrunner team
			case "speedrunner":
				// if the player is not allready in the speedrunner team
				if (!speedrunners.hasEntry(player.getName())) {
					// reset death count
					deaths.getScore(player.getName()).setScore(0);
					// add player to team
					speedrunners.addEntry(player.getName());
					
					// remove player from hunter team ???
					// TODO is this necessary?
					hunters.removeEntry(player.getName());
					
					// tell all players on server that player has joined the speedrunner team
					Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.joinSpd").replace("<player>", player.getName())));
					
					// if config is set so that speedrunners are allowed to have a tracker compass for other speedrunners
					if (plugin.getConfig().getString("options.runnerCompass").equals("true")) {
						// create a new runner compass
						new RunnerCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
						
						// set the default tracker to the speedrunner in 0th index
						compassSelector.getScore(player.getName()).setScore(0);
					}
					// if the config is set to false
					else {
						// ensure the player does not have a tracker compass
						Utils.removeCompass(plugin, player);
					}
				} 
				// if the player is already apart of the speedrunner team
				else {
					// send a message to sender explaining that specified player is already a speedrunner
					sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.alreadySpd").replace("<player>", player.getName())));
				}
				
				// successful command completion
				return true;
			
			// if player is to join the hunter team
			case "hunter":
				// if the player is not already a hunter
				if (!hunters.hasEntry(player.getName())) {
					// reset death count
					deaths.getScore(player.getName()).setScore(0);
					// add player to hunter team
					hunters.addEntry(player.getName());
					// ensure player is not in the speedrunner team
					speedrunners.removeEntry(player.getName());
					
					// create a new tracker compass
					new HunterCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
					
					// set the default tracker to the speedrunner in 0th index
					compassSelector.getScore(player.getName()).setScore(0);
					
					// broadcast to all players that player has joined the hunter team
					Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.joinHunt").replace("<player>", player.getName())));					
				}
				// if player is already apart of the hunter team
				else {
					// inform sender that player is already a hunter
					sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.alreadyHunt").replace("<player>", player.getName())));
				}
				// successful command completion
				return true;
			// if player is to be removed from the game
			case "none":
				// if the player is a part of the hunters or speedrunners
				if (hunters.hasEntry(player.getName()) || speedrunners.hasEntry(player.getName())) {
					// ensure the player is not apart of either team
					speedrunners.removeEntry(player.getName());
					hunters.removeEntry(player.getName());
					
					// broadcast that the player has been removed from the game
					Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.joinNone").replace("<player>", player.getName())));
					// ensure the player doesn't have a tracker compass
					Utils.removeCompass(plugin, player);
				}
				// if the player is not a part of either the hunters or speedrunners
				else {
					// inform sender that this player is not a part of either team
					sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.alreadyNone").replace("<player>", player.getName())));
				}
				// successful command completion
				return true;
			// if default case something has gone wrong
			default:
				return false;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/* Usage:
		 * args[0]: "join", "options", "clock", "reset", "revive", "help"
		 * 
		 * Joining Team:
		 * args[1]: "speedrunner", "hunter", "none"
		 * args[2]: <playername> - optional if player is a sender.
		 * 
		 * Clock:
		 * args[1]: "start", "pause", "resume", "stop"
		 * args[2]: "stopwatch", "timer"
		 * 
		 * Options:
		 * args[1]: "hunterBarInfo", "runnerCompass", "border", "autoTracking"
		 * 
		 * Revive:
		 * args[1]: <playername>
		 * 
		 * Reset:
		 * Resets the state by clearing all teams and objectives.
		 * 
		 * */
			
		// Check that argument length is greater than 0.
		if (args.length > 0) {
			// Check what the first argument is.
			if (args[0].equals("join")) {
				// Then join a team. 
				
				// Check if argument length is greater than 1 (thus, specifying the team name).
				if (args.length > 1) {
					
					// Check if there exist a player name. (Check if there is a third argument)
					if (args.length > 2) {
						// Get player, and check if player is actually found.
						Player player = Bukkit.getPlayerExact(args[2]);
						
						if (player != null) {
							// Player is not null, meaning it is found. Player joins team.
							if (joinTeam(sender, player, args[1])) {
								// Run the function, and function joinTeam returns true (valid team).
								return true;
							} else {
								// Invalid Team, team is not found.
								sender.sendMessage(Utils.chat(plugin.getMessages().getString("teamNotFound").replace("<team>", args[1])));
								return true;
							}
						} else {
							// Player not found. Return error message.
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("playerNotFound").replace("<player>", args[2])));
							return true;
						}
						
					} else {
						// Otherwise, the selected player would be themselves.
						
						if (sender instanceof Player) {
							// Sender is a Player.
							Player player = (Player) sender;
							
							if (joinTeam(sender, player, args[1])) {
								// Run the function, and function joinTeam returns true (valid team).
								return true;
							} else {
								// Invalid Team, team is not found.
								sender.sendMessage(Utils.chat(plugin.getMessages().getString("teamNotFound").replace("<team>", args[1])));
								return true;
							}
						} else {
							// Sender is console, print error message.
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("consoleErrorMessage")));
							return true;
						}
					}
					
				} else {
					// Return error that no team is specified.
					return false;
				}
			} else if (args[0].equals("settings")) {
				// Then toggle settings.
				
				// Check what options they are selecting.
				if (args.length > 1) {
					if (args[1].toLowerCase().equals("hunterbarinfo")) {
						String extraInfo = plugin.getConfig().getString("options.hunterBar");
						
						if (extraInfo.equals("false")) {
							// Set to true
							plugin.getConfig().set("options.hunterBar", "true");
							Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.hunterBarTrue")));
						} else {
							// Set to false
							plugin.getConfig().set("options.hunterBar", "false");
							Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.hunterBarFalse")));
						}
						plugin.saveConfig();
						return true;
					} else if (args[1].toLowerCase().equals("runnercompass")) {
						String runnerCompass = plugin.getConfig().getString("options.runnerCompass");
						
						if (runnerCompass.equals("false")) {
							// Set to true
							plugin.getConfig().set("options.runnerCompass", "true");
							Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.runnerCompassTrue")));
							
							// All existing speedrunners now get a compass if needed.
							for (String playername: speedrunners.getEntries()) {
								Player player = Bukkit.getPlayer(playername);
								new RunnerCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
								compassSelector.getScore(player.getName()).setScore(0);
							}
						} else {
							// Set to false
							plugin.getConfig().set("options.runnerCompass", "false");
							Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.runnerCompassFalse")));
						
							// Remove Compass from everyone
							for (String playername: speedrunners.getEntries()) {
								Player player = Bukkit.getPlayer(playername);
								Utils.removeCompass(plugin, player);
							}
							
						}
						plugin.saveConfig();
						return true;

					}
					else if (args[1].equals("border")) {
						if (args.length > 2) {
							if (args[2].equals("on")) {
								sender.sendMessage("border would be turned on now");
							}
							else if (args[2].equals("off")) {
								sender.sendMessage("border would turn off");
							}
							else if (args[2].equals("set")) {
								sender.sendMessage("this would set stuff");
							}
						}
						else {
							return false;
						}
					}
				} else if (args[1].toLowerCase().equals("autoTracking")) {
					sender.sendMessage("Settings autoTracking!");
				} else if (args[1].toLowerCase().equals("timerBorder")) {
					sender.sendMessage("Settings timerBorder!");
				} else {
					// Did not state any options. Return false.
					return false;
				}
			} else if (args[0].equals("reset")) {
				// For each entry in objectives, and each player in teams.
				// Reset Everything!
				resetSVH();
				Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.reset")));
				return true;
				
			} else if (args[0].equals("clock")) {
				if (args[1].equals("start")) {
					if (args[2].equals("timer")) {
						if (timer.getScore("global").getScore() > 0) {
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.timerRunning")));
						}
						else {
							if (args.length < 4) {
								sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.needToSpecifyTime")));
								return false;
							}
							else {
								int secs = 0;
								try {
									secs = Integer.parseInt(args[3]) * 60;
								}
								catch (NumberFormatException e) {
									sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.needToSpecifyTime")));
									return false;
								}
								
								timer.getScore("global").setScore(secs);
								timerTask = new TimerRunable(this.plugin, this.boardRef).runTaskTimer(this.plugin, 10, 20);
								
								World[] worldsArray = new World[3];
								Bukkit.getWorlds().toArray(worldsArray);
								for (World world : worldsArray) {
									world.getWorldBorder().setSize(1000);
									world.getWorldBorder().setSize(100, secs);
								}
							}
						}
					}
					return true;
				}
				else if (args[1].equals("stop")) {
					if (args[2].equals("timer")) {
						if (timer.getScore("global").getScore() > 0 || timer != null) {
							Bukkit.getServer().getPluginManager().callEvent(new TimerStop(0, timerTask.getTaskId()));
							timer.getScore("global").setScore(0);
						}
						else {
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("svhVsHunt.noTimerActive")));
						}
					}
					return true;
				}
				else if (args[1].equals("pause")) {
					if (args[2].equals("timer")) {
						if (timer.getScore("global").getScore() > 0 && timer != null) {
							Bukkit.getServer().getPluginManager().callEvent(new TimerStop(timer.getScore("global").getScore(), timerTask.getTaskId()));
						}
						else {
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("svhVsHunt.noTimerActive")));
						}
					}
					return true;
				}
				else if (args[1].equals("resume")) {
					if (timer.getScore("global").getScore() > 0 && timer != null) {
						Bukkit.broadcastMessage("Timer resumed");
						timerTask = new TimerRunable(this.plugin, this.boardRef).runTaskTimer(this.plugin, 10, 20);
						return true;
					}
					else {
						sender.sendMessage(Utils.chat(plugin.getMessages().getString("svhVsHunt.noTimerActive")));
						return false;
					}
				}
				
			} else if (args[0].equals("revive")) {
				// Check if the player specified a playername. Otherwise they are likely specifying themselves.
				Player player;
				if (args.length > 1) {
					player = Bukkit.getPlayerExact(args[1]);
				} else if (sender instanceof Player) {
					player = (Player) sender;
				} else {
					sender.sendMessage(Utils.chat(plugin.getMessages().getString("consoleErrorMessage")));
					return false;
				}
				
				// Check if player is alive and in team speedrunners.
				if (player != null) {
					if (speedrunners.hasEntry(player.getName())) {
						if (deaths.getScore(player.getName()).getScore() >= 1) {
							// Revive player. Player rejoins.
							deaths.getScore(player.getName()).setScore(0);
							new RunnerCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
							Bukkit.broadcastMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.revive").replace("<player>", player.getName())));
						} else {
							// Error as player is still alive.
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.reviveFalseDeath").replace("<player>", player.getName())));
						}
					} else {
						// Error that player is not in speedrunners.
						sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.reviveFalseTeam").replace("<player>", player.getName())));
					}
				} else {
					sender.sendMessage(Utils.chat(plugin.getMessages().getString("spdVsHunt.reviveFalsePlayer").replace("<player>", args[1])));
				}
				return true;
			} else if (args[0].equals("help")) {
				// Check if there are any specific command the user is looking for help on.
				if (args.length > 1) {
					switch (args[1]) {
						case "join":
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("help.join").replace("\\n", "\n")));
							return true;
						case "settings":
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("help.settings").replace("\\n", "\n")));
							return true;
						case "reset":
							sender.sendMessage(Utils.chat(plugin.getMessages().getString("help.reset").replace("\\n", "\n")));
							return true;
						case "revive":
							sender.sendMessage("Help Revive!");
							return true;
						case "clock":
							sender.sendMessage("Help Clock!");
							return true;
						default:
							// Use a break, since the command is not found in the "dictionary", show default command.
							break;
					}
				}
				// If args[1] is not found, show default help message
				sender.sendMessage(Utils.chat(plugin.getMessages().getString("help.default").replace("\\n", "\n")));
				return true;
			}
		} else {
			// Return error as no command is specified.
			return false;
		}
		
		return false;
	}
	
}
