package com.leontg77.timer.handling.handlers;

import com.leontg77.timer.handling.TimerHandler;
import com.leontg77.timer.handling.PacketSender;
import com.leontg77.timer.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * Old action bar handler, for versions with a older constructor for sending action bars.
 *
 * @author LeonTG & ghowden
 */
public class OldActionBarHandler implements TimerHandler {
    private final PacketSender packetSender;

    private final Constructor<?> componentConstructor;
    private final Constructor<?> packetConstructor;

    public OldActionBarHandler(PacketSender packetSender) throws ClassNotFoundException, NoSuchMethodException {
        this.packetSender = packetSender;

        this.componentConstructor = ReflectionUtils.getNMSClass("ChatComponentText").getConstructor(String.class);
        this.packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), byte.class);
    }

    @Override
    public void sendText(String text, int remaining, int total) {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Object chat = componentConstructor.newInstance(text); // make a new chat message
                Object packet = packetConstructor.newInstance(chat, ACTION_BAR_TYPE); // create a packet with the message in the action bar slot

                packetSender.sendPacket(player, packet);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while sending action bar packet, are you using Minecraft 1.8 or higher?", ex);
        }
    }

    @Override
    public void startTimer(String text) {}

    @Override
    public void onCancel() {}
}