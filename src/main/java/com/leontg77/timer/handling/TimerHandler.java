package com.leontg77.timer.handling;

/**
 * Timer handler super interface.
 *
 * @author LeonTG
 */
public interface TimerHandler {
    byte ACTION_BAR_TYPE = 2;

    /**
     * Start the timer handler.
     */
    void startTimer();

    /**
     * Stop the timer handler.
     */
    void onCancel();

    /**
     * Send a new packet of the text for the timer handler.
     *
     * @param text The new text.
     */
    void sendText(String text);
}