package com.floweytf.mobopacity;

public class AlphaStatus {
    private static boolean enabled = false;
    private static float value = 0f;

    public static void alpha(float f) {
        value = f;
        enabled = true;
    }

    public static void end() {
        enabled = false;
    }

    public static boolean enabled() {
        return enabled;
    }

    public static float alpha() {
        return value;
    }
}
