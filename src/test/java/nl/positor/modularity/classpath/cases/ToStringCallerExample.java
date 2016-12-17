package nl.positor.modularity.classpath.cases;

/**
 * Created by Arien on 16-Dec-16.
 */
public class ToStringCallerExample implements ToStringCaller {
    private GlobalLogger globalLogger = new GlobalLogger();

    @Override
    public String callToString(ToStringer toStringer) {
        String result = toStringer.convertToString();
        globalLogger.log("To stringer called and returned " + result);
        return result;
    }

    @Override
    public String toString() {
        return globalLogger.toString();
    }
}
