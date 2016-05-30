package nl.positor.module.testcases;

/**
 * Created by Arien on 24-May-16.
 */
public class JobSupervisor {
    private JobCreator jobCreator;
    private JobRunner jobRunner;

    public JobSupervisor() {
    }

    public JobSupervisor(JobCreator jobCreator, JobRunner jobRunner) {
        this.jobCreator = jobCreator;
        this.jobRunner = jobRunner;
    }

    public void runSomeJobs(int count) {
        for (int i=0; i < count; i++) {
            jobRunner.run(jobCreator.create());
        }
    }

    public JobCreator getJobCreator() {
        return jobCreator;
    }

    public void setJobCreator(JobCreator jobCreator) {
        this.jobCreator = jobCreator;
    }

    public JobRunner getJobRunner() {
        return jobRunner;
    }

    public void setJobRunner(JobRunner jobRunner) {
        this.jobRunner = jobRunner;
    }
}
