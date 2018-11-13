package com.leontg77.timer.handling.handlers;

import com.leontg77.timer.handling.TimerHandler;
import com.leontg77.timer.handling.PacketSender;
import com.leontg77.timer.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * New action bar handler, for versions with the new constructor for sending action bars.
 *
 * @author LeonTG & ghowden
 */
public class NewActionBarHandler implements TimerHandler {
    private final PacketSender packetSender;

    private final Constructor<?> componentConstructor;
    private final Constructor<?> packetConstructor;

    private final Method getType;

    public NewActionBarHandler(PacketSender packetSender) throws ClassNotFoundException, NoSuchMethodException {
        this.packetSender = packetSender;

        final Class<?> messageType = ReflectionUtils.getNMSClass("ChatMessageType");
        this.getType = messageType.getDeclaredMethod("a", byte.class);

        this.componentConstructor = ReflectionUtils.getNMSClass("ChatComponentText").getConstructor(String.class);
        this.packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), messageType);
    }

    @Override
    public void sendText(String text) {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Object chat = componentConstructor.newInstance(text); // make a new chat message
                Object packet = packetConstructor.newInstance(chat, getType.invoke(null, ACTION_BAR_TYPE)); // create a packet with the message in the action bar slot

                packetSender.sendPacket(player, packet);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while sending action bar packet, are you using Minecraft 1.8 or higher?", ex);
        }
    }

    @Override
    public void startTimer() {}

    @Override
    public void onCancel() {}
}