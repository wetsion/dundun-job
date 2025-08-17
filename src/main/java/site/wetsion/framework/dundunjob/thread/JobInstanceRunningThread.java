package site.wetsion.framework.dundunjob.thread;

import org.slf4j.Logger;
import site.wetsion.framework.dundunjob.datasource.JobInfo;

/**
 * @author wetsion
 */
public class JobInstanceRunningThread implements Runnable {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(JobInstanceRunningThread.class);

    private final JobInfo jobInfo;

    public JobInstanceRunningThread(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }

    @Override
    public void run() {
        log.info("job running, jobId: {}", jobInfo.getId());
    }
}
