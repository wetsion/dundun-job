package site.wetsion.framework.dundunjob.store.redis;

import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.store.JobInstance;
import site.wetsion.framework.dundunjob.store.JobStore;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "job.store", name = "impl", havingValue = "redis")
public class RedisJobStore implements JobStore {

    private final static Logger log = LoggerFactory.getLogger(RedisJobStore.class);

    @Autowired
    private RedissonClient redissonClient;

    private RSet<String> jobInstanceCache;
    private RBlockingQueue<JobInstance> jobQueue;

    private RScoredSortedSet<JobInstance> waitScheduleQueue;

    private RScript script;

    private List<Object> MOVE_JOB_SCRIPT_KEYS = Arrays.asList(JobCacheConstant.WAIT_SCHEDULE_QUEUE, JobCacheConstant.JOB_QUEUE);

    @PostConstruct
    private void init() {
        this.jobInstanceCache = redissonClient.getSet(JobCacheConstant.JOB_INSTANCE_CACHE);
        this.jobQueue = redissonClient.getBlockingQueue(JobCacheConstant.JOB_QUEUE);
        this.waitScheduleQueue = redissonClient.getScoredSortedSet(JobCacheConstant.WAIT_SCHEDULE_QUEUE);
        this.script = redissonClient.getScript(StringCodec.INSTANCE);
    }

    @Override
    public void cacheJobInstance(Long jobId, Long timestamp) {
        jobInstanceCache.add(jobId + JobCacheConstant.JOB_INSTANCE_CACHE_SPLITTER + timestamp);
    }

    @Override
    public boolean isJobInstanceCached(Long jobId, Long timestamp) {
        return jobInstanceCache.contains(jobId + JobCacheConstant.JOB_INSTANCE_CACHE_SPLITTER + timestamp);
    }

    @Override
    public void clearExpiredJobInstances() {
        jobInstanceCache.removeIf(jobInstance -> {
            String[] split = jobInstance.split(JobCacheConstant.JOB_INSTANCE_CACHE_SPLITTER);
            return Long.parseLong(split[1]) < System.currentTimeMillis();
        });
    }

    @Override
    public void addJobInstanceToScheduleQueue(JobInstance jobInstance) {
        waitScheduleQueue.add(jobInstance.getTimestamp(), jobInstance);
    }

    @Override
    public void scheduleJob(Long timestamp) {
        try {
            String now = String.valueOf(System.currentTimeMillis());
            log.info("开始移动过期任务, now: {}", now);
            Long movedCount = script.eval(
                    RScript.Mode.READ_WRITE,
                    JobCacheConstant.MOVE_EXPIRED_JOB_SCRIPT,
                    RScript.ReturnType.INTEGER,
                    MOVE_JOB_SCRIPT_KEYS,
                    now
            );
            log.info("移动过期任务数量：{}", movedCount);
        } catch (Exception e) {
            log.error("移动过期任务异常", e);
        }
    }

    @Override
    public Long consumeJobFromQueue() {
        try {
            JobInstance jobInstance = jobQueue.take();
            if (Objects.nonNull(jobInstance)) {
                return jobInstance.getJobId();
            }
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long consumeJobFromQueue(Long time, TimeUnit timeUnit) {
        try {
            JobInstance jobInstance = jobQueue.poll(time, timeUnit);
            if (Objects.nonNull(jobInstance)) {
                return jobInstance.getJobId();
            }
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
