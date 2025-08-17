package site.wetsion.framework.dundunjob.executor;

import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.datasource.JobInfo;
import site.wetsion.framework.dundunjob.thread.JobInstanceRunningThread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class JobConsumeExecutor {

    ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            8,
            8,
            10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            (r) -> new Thread(r, "JobConsumeExecutor"),
            new ThreadPoolExecutor.AbortPolicy());

    public void stop() {
        EXECUTOR.shutdown();
    }
    public void execute(JobInfo jobInfo) {
        EXECUTOR.execute(new JobInstanceRunningThread(jobInfo));
    }
}
