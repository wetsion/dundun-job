package site.wetsion.framework.dundunjob.store.redis;

import site.wetsion.framework.dundunjob.store.JobInstance;
import site.wetsion.framework.dundunjob.store.JobStore;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisJobStore implements JobStore {
    @Override
    public void cacheJobInstance(Long jobId, Long timestamp) {

    }

    @Override
    public boolean isJobInstanceCached(Long jobId, Long timestamp) {
        return false;
    }

    @Override
    public void clearExpiredJobInstances() {

    }

    @Override
    public void addJobToQueue(Long jobId) {

    }

    @Override
    public void addJobInstanceToScheduleQueue(JobInstance jobInstance) {

    }

    @Override
    public List<JobInstance> popJobInstanceFromScheduleQueue(Long timestamp) {
        return Collections.emptyList();
    }

    @Override
    public Long consumeJobFromQueue() {
        return 0L;
    }

    @Override
    public Long consumeJobFromQueue(Long time, TimeUnit timeUnit) {
        return 0L;
    }
}
