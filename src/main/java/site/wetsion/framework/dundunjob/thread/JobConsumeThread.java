package site.wetsion.framework.dundunjob.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.datasource.JobInfo;
import site.wetsion.framework.dundunjob.datasource.JobInfoLoader;
import site.wetsion.framework.dundunjob.executor.JobConsumeExecutor;
import site.wetsion.framework.dundunjob.store.JobStore;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * job消费线程, 从队列中取出job丢到本地线程池执行
 * @author wetsion
 */
@Component
public class JobConsumeThread {

    private final static Logger log = LoggerFactory.getLogger(JobConsumeThread.class);

    private ScheduledExecutorService executorService;
    @Autowired
    private JobStore jobStore;
    @Autowired
    private JobInfoLoader jobInfoLoader;
    @Autowired
    private JobConsumeExecutor jobConsumeExecutor;

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        executorService.scheduleAtFixedRate(this::consumeJob, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdown();
        jobConsumeExecutor.stop();
    }

    private void consumeJob() {
        Long jobId = jobStore.consumeJobFromQueue();
        if (Objects.isNull(jobId)) {
            return;
        }
        JobInfo jobInfo = jobInfoLoader.loadJobInfo(jobId);
        if (Objects.isNull(jobInfo)) {
            log.error("任务不存在, jobId: {}", jobId);
            return;
        }
        jobConsumeExecutor.execute(jobInfo);
    }
}
