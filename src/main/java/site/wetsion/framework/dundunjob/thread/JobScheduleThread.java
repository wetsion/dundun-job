package site.wetsion.framework.dundunjob.thread;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.wetsion.framework.dundunjob.store.JobInstance;
import site.wetsion.framework.dundunjob.store.JobStore;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度线程，定时从待调度队列中取出可执行的任务放入待消费队列
 * @author wetsion
 */
@Component
public class JobScheduleThread {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(JobScheduleThread.class);

    @Autowired
    private JobStore jobStore;

    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        executorService.scheduleAtFixedRate(() -> {
            jobStore.scheduleJob(System.currentTimeMillis());
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }
}
