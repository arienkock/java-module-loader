package nl.positor.modularity.glue.impl;

/**
 * Created by Arien on 13-Jan-17.
 */
public class Preconditions {
    public static void checkArgument(boolean check, String message, Object... args) {
        if (!check) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }
}
