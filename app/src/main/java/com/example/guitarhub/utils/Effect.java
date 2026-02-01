package com.example.guitarhub.utils;

public class Effect {
    private String name;
    private int intensity;
    private int timing;

    public Effect() {}

    public Effect(String name, int intensity, int timing) {
        this.name = name;
        this.intensity = intensity;
        this.timing = timing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getTiming() {
        return timing;
    }

    public void setTiming(int timing) {
        this.timing = timing;
    }
}
