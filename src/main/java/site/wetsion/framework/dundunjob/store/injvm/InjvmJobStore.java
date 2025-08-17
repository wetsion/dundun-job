package site.wetsion.framework.dundunjob.store.injvm;

import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.store.JobInstance;
import site.wetsion.framework.dundunjob.store.JobStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于jvm内存的JobStore
 * @author wetsion
 */
@Component
public class InjvmJobStore implements JobStore {

    /**
     * 任务实例缓存
     */
    private final static Map<Long, Set<Long>> JOB_INSTANCE_CACHE = new ConcurrentHashMap<>();
    /**
     * 待消费队列
     */
    private final static LinkedBlockingQueue<Long> CONSUME_QUEUE = new LinkedBlockingQueue<>();
    /**
     * 等待调度队列
     */
    private final static TreeSet<JobInstance> WAIT_SCHEDULE_QUEUE = new TreeSet<>();

    private final Lock lock = new ReentrantLock();

    @Override
    public void cacheJobInstance(Long jobId, Long timestamp) {
        Set<Long> timestamps = JOB_INSTANCE_CACHE.computeIfAbsent(jobId, k -> new CopyOnWriteArraySet<>());
        timestamps.add(timestamp);
    }

    @Override
    public boolean isJobInstanceCached(Long jobId, Long timestamp) {
        Set<Long> timestamps = JOB_INSTANCE_CACHE.get(jobId);
        if (timestamps == null) {
            return false;
        }
        return timestamps.contains(timestamp);
    }

    @Override
    public void clearExpiredJobInstances() {
        JOB_INSTANCE_CACHE.forEach((key, value) -> value.removeIf(timestamp -> {
            if (timestamp < System.currentTimeMillis()) {
                System.out.println("清理时间戳：" + key + ":" + timestamp);
                return true;
            }
            return false;
        }));
    }

    @Override
    public void addJobToQueue(Long jobId) {
        CONSUME_QUEUE.offer(jobId);
    }

    @Override
    public void addJobInstanceToScheduleQueue(JobInstance jobInstance) {
        lock.lock();
        try {
            WAIT_SCHEDULE_QUEUE.add(jobInstance);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<JobInstance> popJobInstanceFromScheduleQueue(Long timestamp) {
        SortedSet<JobInstance> headSet = WAIT_SCHEDULE_QUEUE.headSet(new JobInstance(null, timestamp));
        if (!headSet.isEmpty()) {
            return new ArrayList<>(headSet);
        }
        return Collections.emptyList();
    }

    @Override
    public Long consumeJobFromQueue() {
        try {
            return CONSUME_QUEUE.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long consumeJobFromQueue(Long time, TimeUnit timeUnit) {
        try {
            return CONSUME_QUEUE.poll(time, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
