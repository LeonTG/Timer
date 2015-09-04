package com.leontg77.timer;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.leontg77.timer.cmds.TimerCommand;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for enabling, disabling and getting the prefix.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	private Logger logger = getLogger();
	public static Main plugin;
	
	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " is now disabled.");
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		logger.info(file.getName() + " was made by LeonTG77.");
		plugin = this;

		getCommand("timer").setExecutor(new TimerCommand());
	}
	
	/**
	 * Get the timer plugin prefix.
	 * @return The timer prefix.
	 */
	public static String prefix() {
		return "§c§lTimer §8» §7";
	}
}