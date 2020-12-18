package me.pikalegend.spdvshunt.commands;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.pikalegend.spdvshunt.Main;
import me.pikalegend.spdvshunt.runnables.HunterCompass;
import me.pikalegend.spdvshunt.runnables.RunnerCompass;
import me.pikalegend.spdvshunt.utils.Utils;
import net.md_5.bungee.api.ChatColor;


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

		plugin.getConfig().set("options.hunterBar", "false");
		plugin.getConfig().set("options.runnerCompass", "true");
		plugin.saveConfig();
		
		plugin.getCommand("spdVsHunt").setExecutor(this);
		plugin.getCommand("spdVsHunt").setTabCompleter(new SVHTabComplete());
	}
	
	// Give Tracker Compass
	public void giveCompass(Player hunter) {
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta meta = compass.getItemMeta();
		
		meta.setDisplayName(ChatColor.GOLD + "Tracker Compass");
		meta.getPersistentDataContainer().set(key("tracker"), PersistentDataType.INTEGER, 1);
		
		CompassMeta trackerCompassMeta = (CompassMeta) meta;
		
		compass.setItemMeta(trackerCompassMeta);
		
		hunter.getInventory().setItem(8, compass);
	}
	
	// Removes Tracker Compass from Inventory.
	public void removeCompass(Player player) {		
		ItemStack[] inventory = player.getInventory().getContents();
		for (ItemStack item : inventory) {
			if (item != null) {
				if (item.getItemMeta().getPersistentDataContainer().has(key("tracker"), PersistentDataType.INTEGER)) {
					player.getInventory().remove(item);
					return;
				}
			}
		}
	}
	
	public void resetSVH() {
		// Resets the game entirely
		for (String entry : board.getEntries()) {
			board.resetScores(entry);
		}
		for (Team team : board.getTeams()) {
			for (String entry : team.getEntries()) {
				team.removeEntry(entry);
			}
		}
		Player[] onlineArray = new Player[Bukkit.getOnlinePlayers().size()];
		Bukkit.getOnlinePlayers().toArray(onlineArray);
		
		for (Player player : onlineArray) {
			removeCompass(player);
		}
	}
	
	public boolean joinTeam(CommandSender sender, Player player, String teamString) {
		switch (teamString.toLowerCase()) {
			case "speedrunner":
				if (!speedrunners.hasEntry(player.getName())) {
					deaths.getScore(player.getName()).setScore(0);
					speedrunners.addEntry(player.getName());
					hunters.removeEntry(player.getName());
					Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.joinSpd").replace("<player>", player.getName())));
					
					if (plugin.getConfig().getString("options.runnerCompass").equals("true")) {
						new RunnerCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
						compassSelector.getScore(player.getName()).setScore(0);
					} else {
						removeCompass(player);
					}
				} else {
					sender.sendMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.alreadySpd").replace("<player>", player.getName())));
				}
				return true;
			case "hunter":
				if (!hunters.hasEntry(player.getName())) {
					deaths.getScore(player.getName()).setScore(0);
					hunters.addEntry(player.getName());
					speedrunners.removeEntry(player.getName());
					giveCompass(player);
					new HunterCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
					compassSelector.getScore(player.getName()).setScore(0);
					
					Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.joinHunt").replace("<player>", player.getName())));					
				} else {
					sender.sendMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.alreadyHunt").replace("<player>", player.getName())));
				}
				return true;
			case "none":
				if (hunters.hasEntry(player.getName()) || speedrunners.hasEntry(player.getName())) {
					speedrunners.removeEntry(player.getName());
					hunters.removeEntry(player.getName());
					Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.joinNone").replace("<player>", player.getName())));					
					removeCompass(player);
				} else {
					sender.sendMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.alreadyNone").replace("<player>", player.getName())));
				}
				return true;
			default:
				return false;
		}
	}
	
	private NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/* Usage:
		 * args[0]: "join", "options", "reset"
		 * 
		 * Joining Team:
		 * args[1]: "speedrunner", "hunter", "none"
		 * args[2]: <playername> - optional if player is a sender.
		 * 
		 * Options:
		 * args[1]: "hunterBarInfo", "runnerCompass"
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
								sender.sendMessage(Utils.chat(plugin.getConfig().getString("teamNotFound").replace("<team>", args[1])));
								return true;
							}
						} else {
							// Player not found. Return error message.
							sender.sendMessage(Utils.chat(plugin.getConfig().getString("playerNotFound").replace("<player>", args[2])));
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
								sender.sendMessage(Utils.chat(plugin.getConfig().getString("teamNotFound").replace("<team>", args[1])));
								return true;
							}
						} else {
							// Sender is console, print error message.
							sender.sendMessage(Utils.chat(plugin.getConfig().getString("consoleErrorMessage")));
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
							Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.hunterBarTrue")));
						} else {
							// Set to false
							plugin.getConfig().set("options.hunterBar", "false");
							Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.hunterBarFalse")));
						}
						plugin.saveConfig();
						return true;
					} else if (args[1].toLowerCase().equals("runnercompass")) {
						String runnerCompass = plugin.getConfig().getString("options.runnerCompass");
						
						if (runnerCompass.equals("false")) {
							// Set to true
							plugin.getConfig().set("options.runnerCompass", "true");
							Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.runnerCompassTrue")));
							
							// All existing speedrunners now get a compass if needed.
							for (String playername: speedrunners.getEntries()) {
								Player player = Bukkit.getPlayer(playername);
								new RunnerCompass(this.plugin, player, boardRef).runTaskTimer(this.plugin, 10, 10);
								compassSelector.getScore(player.getName()).setScore(0);
							}
						} else {
							// Set to false
							plugin.getConfig().set("options.runnerCompass", "false");
							Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.runnerCompassFalse")));
						
							// Remove Compass from everyone
							for (String playername: speedrunners.getEntries()) {
								Player player = Bukkit.getPlayer(playername);
								removeCompass(player);
							}
							
						}
						plugin.saveConfig();
						return true;

					}
				} else {
					// Did not state any options. Return false.
					return false;
				}
			} else if (args[0].equals("reset")) {
				// For each entry in objectives, and each player in teams.
				// Reset Everything!
				resetSVH();
				Bukkit.broadcastMessage(Utils.chat(plugin.getConfig().getString("spdVsHunt.reset")));
				return true;
				
			} else if (args[0].equals("help")) { 
				// Check if there are any specific command the user is looking for help on.
				if (args.length > 1) {
					switch (args[1]) {
						case "join":
							sender.sendMessage(Utils.chat(plugin.getConfig().getString("help.join").replace("\\n", "\n")));
							return true;
						case "settings":
							sender.sendMessage(Utils.chat(plugin.getConfig().getString("help.settings").replace("\\n", "\n")));
							return true;
						case "reset":
							sender.sendMessage(Utils.chat(plugin.getConfig().getString("help.reset").replace("\\n", "\n")));
							return true;
						default:
							// Use a break, since the command is not found in the "dictionary", show default command.
							break;
					}
				}
				// If args[1] is not found, show default help message
				sender.sendMessage(Utils.chat(plugin.getConfig().getString("help.default").replace("\\n", "\n")));
				return true;
			}
		} else {
			// Return error as no command is specified.
			return false;
		}
		
		return false;
	}
	
}
