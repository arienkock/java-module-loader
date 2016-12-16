package nl.positor.module.loading;

/**
 * Created by Arien on 10-Jun-16.
 */
public interface Instantiator {

    Instantiator withConstructorArgs(Object... arguments);

    Instantiator withConstructorArguments(Iterable<Object> arguments);

    Instantiator thenCallingWithArgs(CharSequence methodName, Object... arguments);

    Instantiator thenCallingWithArguments(CharSequence methodName, Iterable<Object> arguments);
}
