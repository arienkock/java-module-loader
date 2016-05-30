package nl.positor.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Arien on 26-May-16.
 */
public class DistinctParams {
    public static <A, B> DistinctParamsFunction<A, B> requireDistinct(Function<A, B> function) {
        Set<A> paramSet = new HashSet<>();
        return param -> {
            boolean isNew = paramSet.add(param);
            if (!isNew) {
                throw new DistinctParamsViolationException();
            }
            return function.apply(param);
        };
    }

    @FunctionalInterface
    public interface DistinctParamsFunction<A, B> {
        B apply(A param) throws DistinctParamsViolationException;
    }

    public static class DistinctParamsViolationException extends Exception {

    }
}
