package com.leontg77.timer;

import java.util.logging.Logger;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the UHC plugin.
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	private final Logger logger = Bukkit.getServer().getLogger();
	public static Main plugin;
	
	private static final long SECONDS_PER_HOUR = 3600;
	private static final long SECONDS_PER_MINUTE = 60;
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " is now enabled.");
		
		plugin = this;
		getCommand("timer").setExecutor(new TimerCommand());
	}
	
	/**
	 * Get the UHC prefix with an ending color.
	 * @param endcolor the ending color.
	 * @return The UHC prefix.
	 */
	public static String prefix() {
		String prefix = "§c§lTimer §8§l>> §7";
		return prefix;
	}
	
	/**
     * Converts the seconds to human readable
     * @param ticks the  number of ticks
     * @return the human readable version
     */
    public static String ticksToString(long ticks) {
        int hours = (int) Math.floor(ticks / (double) SECONDS_PER_HOUR);
        ticks -= hours * SECONDS_PER_HOUR;
        int minutes = (int) Math.floor(ticks / (double)SECONDS_PER_MINUTE);
        ticks -= minutes * SECONDS_PER_MINUTE;
        int seconds = (int) ticks;

        StringBuilder output = new StringBuilder();
        if (hours > 0) {
            output.append(hours).append('h');
            if (minutes == 0) {
            	output.append("0m");
            }
        }
        if (minutes > 0) {
            output.append(minutes).append('m');
        }
        output.append(seconds).append('s');

        return output.toString();
    }
    
    /**
	 * Sends a message in action bar to a player.
	 * @param player the player.
	 * @param msg the message.
	 */
	public static void sendAction(Player player, String msg) {
		CraftPlayer p = (CraftPlayer) player;
        
        IChatBaseComponent cbc = ChatSerializer.a("{text:'" + msg + "'}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        p.getHandle().playerConnection.sendPacket(ppoc);
	}
}