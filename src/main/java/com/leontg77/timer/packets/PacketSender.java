package com.leontg77.timer.packets;

import com.google.common.base.Preconditions;
import com.leontg77.timer.utils.ReflectionUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketSender {

    protected final Method getHandleMethod;
    protected final Field playerConnectionField;
    protected final Method sendPacketMethod;
    protected final Class<?> packetClass;

    /**
     * Handles sending arbitary packets to players
     *
     * @throws ClassNotFoundException if init fails
     * @throws NoSuchMethodException if init fails
     * @throws NoSuchFieldException if init fails
     */
    public PacketSender() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        // cache all reflection methods/fields use to avoid expensive lookups

        // method CraftPlayer#getHandle() -> EntityPlayer
        getHandleMethod = ReflectionUtils.getCraftBukkitClass("entity.CraftPlayer").getDeclaredMethod("getHandle");

        // field EntityPlayer.playerConnection
        playerConnectionField = ReflectionUtils.getNMSClass("EntityPlayer").getField("playerConnection");

        // packet class
        packetClass = ReflectionUtils.getNMSClass("Packet");

        // method PlayerConnection#sendPacket(Packet)
        sendPacketMethod = ReflectionUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", packetClass);
    }

    /**
     * Send the given packet to the given player.
     *
     * @param player the player sending it to.
     * @param packet the packet being sent.
     *
     * @throws InvocationTargetException if reflection fails
     * @throws IllegalAccessException if reflection fails
     * @throws IllegalArgumentException if packet is not a Packet
     */
    public void sendPacket(Player player, Object packet) throws InvocationTargetException, IllegalAccessException {
        Preconditions.checkArgument(packetClass.isAssignableFrom(packet.getClass()), "Tried to send a packet to a player that was an incorrect class");

        // grab the nms EntityPlayer
        Object entityPlayer = getHandleMethod.invoke(player);

        // grab their PlayerConnection
        Object playerConnection = playerConnectionField.get(entityPlayer);

        // send the packet
        sendPacketMethod.invoke(playerConnection, packet);
    }
}
