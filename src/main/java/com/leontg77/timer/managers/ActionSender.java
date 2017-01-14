package com.leontg77.timer.managers;

import com.leontg77.timer.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Action sender class.
 *
 * @author ghowden
 */
public class ActionSender {
    private static final byte ACTION_BAR_TYPE = 2;

    private final PacketSender packetSender;
    private final Constructor<?> chatComponentTextConstructor;
    private final Constructor<?> packetPlayerOutChatConstructor;

    public ActionSender(Main plugin, PacketSender packetSender) throws ClassNotFoundException, NoSuchMethodException {
        this.packetSender = packetSender;

        chatComponentTextConstructor = plugin.getNMSClass("ChatComponentText").getConstructor(String.class);
        packetPlayerOutChatConstructor = plugin.getNMSClass("PacketPlayOutChat").getConstructor(plugin.getNMSClass("IChatBaseComponent"), byte.class);
    }

    /**
     * Sends a message in the action bar to the given player.
     *
     * @param player The player sending it to.
     * @param message The message to send.
     */
    private void sendAction(Player player, String message) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object chat = chatComponentTextConstructor.newInstance(message); // make a new chat message
        Object packet = packetPlayerOutChatConstructor.newInstance(chat, ACTION_BAR_TYPE); // create a packet with the message in the action bar slot

        packetSender.sendPacket(player, packet);
    }

    /**
     * Sends a message in the action bar to all online players.
     *
     * @param message The message to send.
     */
    void sendToAll(String message) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendAction(player, message);
        }
    }
}