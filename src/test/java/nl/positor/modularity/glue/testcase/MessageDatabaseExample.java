package nl.positor.modularity.glue.testcase;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by Arien on 21-Dec-16.
 */
public class MessageDatabaseExample implements MessageDatabase, Iterable<String> {
    private List<String> database = new Vector<>();
    @Override
    public void save(String message) {
        database.add(message);
    }

    @Override
    public Iterator<String> iterator() {
        return database.iterator();
    }
}
