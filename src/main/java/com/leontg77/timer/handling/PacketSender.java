/*
 * Project: Timer
 * Class: com.leontg77.timer.handling.PacketSender
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018 Leon Vaktskjold <leontg77@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.leontg77.timer.handling;

import com.google.common.base.Preconditions;
import com.leontg77.timer.ReflectionUtils;
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
    private final Class<?> packetClass;

    private final Method sendPacketMethod;
    private final Method getHandleMethod;

    public PacketSender() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        this.getHandleMethod = ReflectionUtils.getCraftBukkitClass("entity.CraftPlayer").getDeclaredMethod("getHandle"); // method CraftPlayer#getHandle() -> EntityPlayer
        this.playerConnectionField = ReflectionUtils.getNMSClass("EntityPlayer").getField("playerConnection"); // field EntityPlayer.playerConnection

        this.packetClass = ReflectionUtils.getNMSClass("Packet"); // packet class
        this.sendPacketMethod = ReflectionUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", packetClass); // method PlayerConnection#sendPacket(Packet)
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
    public void sendPacket(Player player, Object packet) throws InvocationTargetException, IllegalAccessException {
        Preconditions.checkArgument(packetClass.isAssignableFrom(packet.getClass()), "Tried to send a packet to a player that was an incorrect class");

        Object entityPlayer = getHandleMethod.invoke(player); // grab the nms EntityPlayer
        Object playerConnection = playerConnectionField.get(entityPlayer); // grab their PlayerConnection
        sendPacketMethod.invoke(playerConnection, packet); // send the packet
    }
}
