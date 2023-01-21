package ol.utils;

import org.jetbrains.annotations.Contract;

public class Angles {
    @Contract(pure = true)
    public static boolean alignX(int rotation) {
        return rotation == 0 || rotation == 2;
    }

    @Contract(pure = true)
    public static boolean alignY(int rotation) {
        return rotation == 1 || rotation == 3;
    }
}