package nl.positor.module.testcases;

/**
 * Created by Arien on 24-May-16.
 */
public class JobRunnerImpl implements JobRunner {
    @Override
    public Integer run(Job job) {
        return job.run();
    }
}
