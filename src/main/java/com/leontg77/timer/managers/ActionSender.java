/*
 * Project: ActionTimer
 * Class: com.leontg77.timer.managers.ActionSender
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Leon Vaktskjold <leontg77@gmail.com>.
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