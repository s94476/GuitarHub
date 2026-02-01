package com.example.guitarhub.utils;

public class TimedEffect extends Effect {
    private int timingMs;

    public TimedEffect() {}

    public TimedEffect(String name, int intensity, int timing, int timingMs) {
        super(name, intensity, timing);
        this.timingMs = timingMs;
    }

    public int getTimingMs() {
        return timingMs;
    }

    public void setTimingMs(int timingMs) {
        this.timingMs = timingMs;
    }
}
