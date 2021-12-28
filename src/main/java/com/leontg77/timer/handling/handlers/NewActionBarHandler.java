/*
 * Project: Timer
 * Class: com.leontg77.timer.handling.handlers.NewActionBarHandler
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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * New action bar handler, for versions with the new constructor for sending action bars.
 *
 * @author LeonTG & ghowden
 */
public class NewActionBarHandler implements TimerHandler {
    private final Method sendMethod;

    public NewActionBarHandler() throws ClassNotFoundException, NoSuchMethodException {
        this.sendMethod = Class.forName("org.bukkit.entity.Player$Spigot").getMethod("sendMessage", ChatMessageType.class, BaseComponent[].class);
        this.sendMethod.setAccessible(true);
    }

    @Override
    public void sendText(String text) {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendMethod.invoke(player, ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while sending action bar packet, are you using Minecraft 1.8 or higher?", ex);
        }
    }

    @Override
    public void startTimer(String message) {}

    @Override
    public void onCancel() {}
}
