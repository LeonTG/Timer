package com.leontg77.timer.cmds;

import com.google.common.base.Optional;
import com.leontg77.timer.packets.ActionSender;
import com.leontg77.timer.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TimerRunnable implements Runnable {

    protected long ticksRemaining = 0;
    protected boolean countdown = true;
    protected String message;

    protected Optional<Integer> jobId = Optional.absent();

    protected final ActionSender sender;
    protected final Plugin plugin;

    /**
     * @param sender an action sender to use
     */
    public TimerRunnable(Plugin plugin, ActionSender sender) {
        this.sender = sender;
        this.plugin = plugin;
    }

    /**
     * Starts this timer with the given message for the amount of invocations.
     * Once complete the task will cancel itself.
     *
     * Overwrites any previous settings
     *
     * @param message the message to send
     * @param seconds the amount of seconds to send the message for
     */
    public void startSendingMessage(String message, long seconds) {
        this.ticksRemaining = seconds;
        this.message = message;

        this.ticksRemaining = seconds * 20;
        this.countdown = seconds > -1;
        this.message = message;

        // start once per second
        cancel();
        jobId = Optional.of(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20));
    }

    public void cancel() {
        if (jobId.isPresent()) {
            Bukkit.getScheduler().cancelTask(jobId.get());
            jobId = Optional.absent();
        }
    }

    public boolean isRunning() {
        return jobId.isPresent();
    }

    public void run() {
        // send to all
        try {
            sender.sendToAll(message + " " + TimeUtils.timeToString(ticksRemaining));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (countdown) {
            // cancel the task when we're complete
            if (ticksRemaining == 0) {
                cancel();
                return;
            }

            // remove one from our time left
            ticksRemaining--;
        }
    }
}
