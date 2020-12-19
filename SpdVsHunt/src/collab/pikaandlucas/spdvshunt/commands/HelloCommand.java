package collab.pikaandlucas.spdvshunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import collab.pikaandlucas.spdvshunt.Main;


public class HelloCommand implements CommandExecutor {
	
	public HelloCommand(Main plugin) {
		plugin.getCommand("hello").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		sender.sendMessage("Hello epic user!");
		return true;
	}
}


