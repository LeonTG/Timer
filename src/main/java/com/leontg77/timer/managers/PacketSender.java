package com.leontg77.timer.managers;

import com.google.common.base.Preconditions;
import com.leontg77.timer.Main;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Packet Sender class.
 *
 * @author ghowden
 */
public class PacketSender {
    private final Field playerConnectionField;
    private final Method sendPacketMethod;
    private final Method getHandleMethod;
    private final Class<?> packetClass;

    /**
     * Handles sending arbitary packets to players
     *
     * @throws ClassNotFoundException if init fails.
     * @throws NoSuchMethodException if init fails.
     * @throws NoSuchFieldException if init fails.
     */
    public PacketSender(Main plugin) throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        getHandleMethod = plugin.getCraftBukkitClass("entity.CraftPlayer").getDeclaredMethod("getHandle"); // method CraftPlayer#getHandle() -> EntityPlayer
        playerConnectionField = plugin.getNMSClass("EntityPlayer").getField("playerConnection"); // field EntityPlayer.playerConnection
        packetClass = plugin.getNMSClass("Packet"); // packet class
        sendPacketMethod = plugin.getNMSClass("PlayerConnection").getMethod("sendPacket", packetClass); // method PlayerConnection#sendPacket(Packet)
    }

    /**
     * Send the given packet to the given player.
     *
     * @param player The player sending it to.
     * @param packet The packet being sent.
     *
     * @throws InvocationTargetException if reflection fails.
     * @throws IllegalAccessException if reflection fails.
     * @throws IllegalArgumentException if packet is not a Packet.
     */
    void sendPacket(Player player, Object packet) throws InvocationTargetException, IllegalAccessException {
        Preconditions.checkArgument(packetClass.isAssignableFrom(packet.getClass()), "Tried to send a packet to a player that was an incorrect class");

        Object entityPlayer = getHandleMethod.invoke(player); // grab the nms EntityPlayer
        Object playerConnection = playerConnectionField.get(entityPlayer); // grab their PlayerConnection
        sendPacketMethod.invoke(playerConnection, packet); // send the packet
    }
}