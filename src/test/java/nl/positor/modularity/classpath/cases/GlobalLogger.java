package nl.positor.modularity.classpath.cases;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arien on 16-Dec-16.
 */
public class GlobalLogger {
    private List<String> lines = new ArrayList<>();

    public void log(String line) {
        lines.add(line);
    }

    @Override
    public String toString() {
        return lines.toString();
    }
}
