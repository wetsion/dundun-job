package site.wetsion.framework.dundunjob.executor;

import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.datasource.JobInfo;
import site.wetsion.framework.dundunjob.thread.JobInstanceRunningThread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class JobConsumeExecutor {

    private ThreadPoolExecutor EXECUTOR;

    public void start() {
        EXECUTOR = new ThreadPoolExecutor(
                8,
                8,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                (r) -> new Thread(r, "JobConsumeExecutor"),
                new ThreadPoolExecutor.AbortPolicy());
        EXECUTOR.prestartAllCoreThreads();
    }

    public void stop() {
        EXECUTOR.shutdown();
    }
    public void execute(JobInfo jobInfo) {
        EXECUTOR.execute(new JobInstanceRunningThread(jobInfo));
    }
}
