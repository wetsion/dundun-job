package site.wetsion.framework.dundunjob.thread;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import site.wetsion.framework.dundunjob.cron.CronParser;
import site.wetsion.framework.dundunjob.datasource.JobInfo;
import site.wetsion.framework.dundunjob.datasource.JobInfoLoader;
import site.wetsion.framework.dundunjob.store.JobInstance;
import site.wetsion.framework.dundunjob.store.JobStore;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务实例生成线程，每隔10秒从存储中获取job并生成近十次可触发执行的任务实例
 * @author wetsion
 */
@Component
public class JobInstanceGeneratorThread {

    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(JobInstanceGeneratorThread.class);

    @Autowired
    private JobInfoLoader jobInfoLoader;
    @Autowired
    private JobStore jobStore;

    private ScheduledExecutorService executorService;

//    private static final JobInstanceGeneratorThread instance = new JobInstanceGeneratorThread();
//
//    public static JobInstanceGeneratorThread getInstance() {
//        return instance;
//    }

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void stop() {
        executorService.shutdown();
    }

    public void start() {
        executorService.scheduleAtFixedRate(() -> {
            List<JobInfo> jobInfos = jobInfoLoader.loadAllJobInfo();
            if (CollectionUtils.isEmpty(jobInfos)) {
                return;
            }
            for (JobInfo jobInfo : jobInfos) {
                generateJobInstances(jobInfo);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void generateJobInstances(JobInfo jobInfo) {
        // 获取下十次执行时间
        List<Long> timestamps;
        try {
            timestamps = CronParser.getNextNTimestamps(jobInfo.getCron(), ZonedDateTime.now(), 10);
        } catch (Exception e) {
            logger.error("cron表达式解析错误", e);
            return;
        }
        System.out.println(Thread.currentThread().getId() + "" + timestamps);
        for (Long timestamp : timestamps) {
            System.out.println(timestamp);
            if (jobStore.isJobInstanceCached(jobInfo.getId(), timestamp)) {
                System.out.println("已存在:" +  timestamp);
                continue;
            }
            try {
                System.out.println(Thread.currentThread().getId() +"不存在就加入:" + timestamp);
                // 缓存任务实例，用于去重
                jobStore.cacheJobInstance(jobInfo.getId(), timestamp);
                // 加入到待调度队列
                jobStore.addJobInstanceToScheduleQueue(new JobInstance(jobInfo.getId(), timestamp));
            } catch (Exception e) {
                logger.error("加入任务实例失败, jobId: {}, timestamp: {}", jobInfo.getId(), timestamp, e);
            }
        }
    }
}
