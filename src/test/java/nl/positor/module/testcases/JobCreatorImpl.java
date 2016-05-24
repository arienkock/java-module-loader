package nl.positor.module.testcases;

/**
 * Created by Arien on 24-May-16.
 */
public class JobCreatorImpl implements JobCreator {
    private Integer value;

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public Job create() {
        Integer valueToReturn = value;
        return () -> valueToReturn;
    }
}
