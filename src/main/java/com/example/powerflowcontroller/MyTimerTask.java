package com.example.powerflowcontroller;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
    private boolean isRunning = false;

    @Override
    public void run() {
        isRunning = true;
        // Ваш код задачи
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean cancel() {
        isRunning = false;
        return super.cancel();
    }
}
