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
     * 添加任务到待消费队列
     * @param jobId 任务id
     */
    void addJobToQueue(Long jobId);
    /**
     * 添加任务实例到待调度队列
     * @param jobInstance 任务实例
     */
    void addJobInstanceToScheduleQueue(JobInstance jobInstance);

    /**
     * 获取小于等于当前时间的任务实例
     * @param timestamp 时间戳
     * @return 任务实例
     */
    List<JobInstance> popJobInstanceFromScheduleQueue(Long timestamp);

    /**
     * 从待调度队列中获取任务
     * @return 任务id
     */
    Long consumeJobFromQueue();

    Long consumeJobFromQueue(Long time, TimeUnit timeUnit);
}
