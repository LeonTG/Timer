/*
 * Project: Timer
 * Class: com.leontg77.timer.handling.handlers.OldActionBarHandler
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

    public OldActionBarHandler() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        this.packetSender = new PacketSender();

        this.componentConstructor = ReflectionUtils.getNMSClass("ChatComponentText").getConstructor(String.class);
        this.packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), byte.class);
    }

    @Override
    public void sendText(String text) {
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
