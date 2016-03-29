package nl.positor.util;

import java.util.function.Supplier;

/**
 * Created by Arien on 28-Mar-16.
 */
public class Always {
    public static <T> Supplier<T> supply(T object) {
        return () -> object;
    }
}
