package site.wetsion.framework.dundunjob.store;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface JobStore {

    /**
     * 缓存任务实例
     * @param jobId 任务id
     * @param timestamp 任务触发时间
     */
    void cacheJobInstance(Long jobId, Long timestamp);

    /**
     * 判断任务实例是否已缓存
     * @param jobId 任务id
     * @param timestamp 触发时间
     * @return 是否已缓存
     */
    boolean isJobInstanceCached(Long jobId, Long timestamp);

    /**
     * 清理过期任务实例
     */
    void clearExpiredJobInstances();

    /**
     * 添加任务实例到待调度队列
     * @param jobInstance 任务实例
     */
    void addJobInstanceToScheduleQueue(JobInstance jobInstance);

    /**
     * 调度任务，将调度队列小于时间戳的任务加入待消费队列
     * @param timestamp 时间戳
     */
    void scheduleJob(Long timestamp);
    /**
     * 从待调度队列中获取任务
     * @return 任务id
     */
    Long consumeJobFromQueue();

    Long consumeJobFromQueue(Long time, TimeUnit timeUnit);
}
