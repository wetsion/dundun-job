package site.wetsion.framework.dundunjob.thread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.store.JobStore;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 清理过期的job instance缓存
 * @author wetsion
 */
@Component
public class ExpireJobInstanceCacheClearThread {

    private ScheduledExecutorService executorService;

    @Autowired
    private JobStore jobStore;

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        executorService.scheduleAtFixedRate(() -> jobStore.clearExpiredJobInstances(), 0, 20, TimeUnit.SECONDS);
    }
    public void stop() {
        executorService.shutdown();
    }
}
