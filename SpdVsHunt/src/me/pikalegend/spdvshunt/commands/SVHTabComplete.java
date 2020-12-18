package me.pikalegend.spdvshunt.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class SVHTabComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("spdVsHunt")) {
			List<String> list = new ArrayList<String>();
			
			if (args.length == 1) {
				list.add("join");
				list.add("settings");
				list.add("reset");
				list.add("help");
			} else if (args.length == 2) {
				switch (args[0].toLowerCase()) {
					case "join":
						list.add("speedrunner");
						list.add("hunter");
						list.add("none");
						break;
					case "settings":
						list.add("hunterBarInfo");
						list.add("runnerCompass");
						break;
					case "help":
						list.add("join");
						list.add("settings");
						list.add("reset");
						break;
					default:
						break;
				}
			} else if (args.length == 3) {
				if (args[0].toLowerCase().equals("join")) {
					Player[] onlineArray = new Player[Bukkit.getOnlinePlayers().size()];
					Bukkit.getOnlinePlayers().toArray(onlineArray);
					
					for (Player player : onlineArray) {
						list.add(player.getName());
					}
				}
			}
			return list;
		}
		
		return null;
	}
	
}
