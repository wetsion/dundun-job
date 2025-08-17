package site.wetsion.framework.dundunjob.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.thread.ExpireJobInstanceCacheClearThread;
import site.wetsion.framework.dundunjob.thread.JobConsumeThread;
import site.wetsion.framework.dundunjob.thread.JobInstanceGeneratorThread;
import site.wetsion.framework.dundunjob.thread.JobScheduleThread;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DunDunJobExecutor implements SmartLifecycle {

    private final AtomicBoolean isWorking = new AtomicBoolean(false);

    @Autowired
    private JobInstanceGeneratorThread jobInstanceGeneratorThread;
    @Autowired
    private ExpireJobInstanceCacheClearThread expireJobInstanceCacheClearThread;
    @Autowired
    private JobScheduleThread jobScheduleThread;
    @Autowired
    private JobConsumeThread jobConsumeThread;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void start() {
        isWorking.set(true);
        jobInstanceGeneratorThread.start();
        expireJobInstanceCacheClearThread.start();
        jobScheduleThread.start();
        jobConsumeThread.start();
    }

    @Override
    public void stop() {
        jobInstanceGeneratorThread.stop();
        expireJobInstanceCacheClearThread.stop();
        jobScheduleThread.stop();
        jobConsumeThread.stop();
        isWorking.set(false);
    }

    @Override
    public boolean isRunning() {
        return isWorking.get();
    }
}
