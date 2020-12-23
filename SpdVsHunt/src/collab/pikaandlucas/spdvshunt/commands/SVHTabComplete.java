package collab.pikaandlucas.spdvshunt.commands;

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
		Player[] onlineArray = new Player[Bukkit.getOnlinePlayers().size()];
		Bukkit.getOnlinePlayers().toArray(onlineArray);
		
		if (cmd.getName().equalsIgnoreCase("spdVsHunt")) {
			List<String> list = new ArrayList<String>();
			
			if (args.length == 1) {
				list.add("join");
				list.add("settings");
				list.add("reset");
				list.add("revive");
				list.add("timer");
				list.add("stopwatch");
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
						list.add("autoTracking");
						list.add("timerBorder");
						break;
					case "help":
						list.add("join");
						list.add("settings");
						list.add("revive");
						list.add("clock");
						list.add("reset");
						break;
					case "revive":
						for (Player player : onlineArray) {
							list.add(player.getName());
						}
						break;
					case "timer":
						list.add("start");
						list.add("pause");
						list.add("resume");
						list.add("stop");
						break;
					case "stopwatch":
						list.add("start");
						list.add("pause");
						list.add("resume");
						list.add("stop");
						break;
					default:
						break;
				}
			} else if (args.length == 3) {
				switch (args[0].toLowerCase()) {
					case "join":
						for (Player player : onlineArray) {
							list.add(player.getName());
						}
						break;
					case "settings":
						switch (args[1].toLowerCase()) {
							case "border":
								list.add("start");
								list.add("end");
								list.add("on");
								list.add("off");
								break;
							default:
								break;
						}
					default:
						break;
				}
			}
			return list;
		}
		
		return null;
	}
	
}
