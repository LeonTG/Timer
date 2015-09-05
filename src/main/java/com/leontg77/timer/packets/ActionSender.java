package com.leontg77.timer.packets;

import com.leontg77.timer.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ActionSender {

    protected final PacketSender packetSender;
    protected final Constructor<?> chatComponentTextConstructor;
    protected final Constructor<?> packetPlayerOutChatConstructor;

    protected static final byte ACTION_BAR_TYPE = 2;

    public ActionSender(PacketSender packetSender) throws ClassNotFoundException, NoSuchMethodException {
        this.packetSender = packetSender;

        chatComponentTextConstructor = ReflectionUtils.getNMSClass("ChatComponentText").getConstructor(String.class);
        packetPlayerOutChatConstructor = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), byte.class);
    }

    /**
     * Sends a message in the action bar to the given player.
     *
     * @param player the player sending it to.
     * @param message the message to send.
     */
    public void sendAction(Player player, String message) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        // make a new chat message
        Object chat = chatComponentTextConstructor.newInstance(message);

        // create a packet with the message in the action bar slot
        Object packet = packetPlayerOutChatConstructor.newInstance(chat, ACTION_BAR_TYPE);

        packetSender.sendPacket(player, packet);
    }

    public void sendToAll(String message) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendAction(player, message);
        }
    }
}
