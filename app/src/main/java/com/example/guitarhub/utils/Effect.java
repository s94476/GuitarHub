package com.example.guitarhub.utils;

/**
 * Model class representing a guitar effect or setting.
 * Each effect has a name and an intensity level (0-10).
 */
public class Effect {
    private String name;
    private int intensity;

    public Effect() {}

    public Effect(String name, int intensity) {
        this.name = name;
        this.intensity = intensity;
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
}
