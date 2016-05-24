package nl.positor.module.testcases;

/**
 * Created by Arien on 24-May-16.
 */
public class JobSupervisor {
    private JobCreator jobCreator;
    private JobRunner jobRunner;

    public JobSupervisor(JobCreator jobCreator, JobRunner jobRunner) {
        this.jobCreator = jobCreator;
        this.jobRunner = jobRunner;
    }

    void runSomeJobs(int count) {
        for (int i=0; i < count; i++) {
            jobRunner.run(jobCreator.create());
        }
    }
}
