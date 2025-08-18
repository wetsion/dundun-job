package site.wetsion.framework.dundunjob.store.redis;

import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.store.JobInstance;
import site.wetsion.framework.dundunjob.store.JobStore;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "job.store", name = "impl", havingValue = "redis")
public class RedisJobStore implements JobStore {

    private final static Logger log = LoggerFactory.getLogger(RedisJobStore.class);

    @Autowired
    private RedissonClient redissonClient;

    private RSet<String> jobInstanceCache;
    private RBlockingQueue<Long> jobQueue;

    private RScoredSortedSet<JobInstance> waitScheduleQueue;

    @PostConstruct
    private void init() {
        this.jobInstanceCache = redissonClient.getSet(JobCacheConstant.JOB_INSTANCE_CACHE);
        this.jobQueue = redissonClient.getBlockingQueue(JobCacheConstant.JOB_QUEUE);
        this.waitScheduleQueue = redissonClient.getScoredSortedSet(JobCacheConstant.WAIT_SCHEDULE_QUEUE);
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
    public void addJobToQueue(Long jobId) {
        boolean r = jobQueue.offer(jobId);
        if (!r) {
            log.error("添加任务到队列失败, jobId: {}", jobId);
        }
    }

    @Override
    public void addJobInstanceToScheduleQueue(JobInstance jobInstance) {
        waitScheduleQueue.add(jobInstance.getTimestamp(), jobInstance);
    }

    @Override
    public List<JobInstance> popJobInstanceFromScheduleQueue(Long timestamp) {
        Collection<JobInstance> jobInstances = waitScheduleQueue.valueRange(0, true, timestamp, true);
        waitScheduleQueue.removeAll(jobInstances);
        return new ArrayList<>(jobInstances);
    }

    @Override
    public Long consumeJobFromQueue() {
        try {
            return jobQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long consumeJobFromQueue(Long time, TimeUnit timeUnit) {
        try {
            return jobQueue.poll(time, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
