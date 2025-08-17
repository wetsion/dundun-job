package site.wetsion.framework.dundunjob.store;

import java.io.Serializable;
import java.util.Objects;

public class JobInstance implements Serializable, Comparable<JobInstance> {
    private static final long serialVersionUID = -2577563775677491969L;
    /**
     * 任务id
     */
    private final Long jobId;
    /**
     * 触发执行时间
     */
    private final Long timestamp;

    public JobInstance(Long jobId, Long timestamp) {
        this.jobId = jobId;
        this.timestamp = timestamp;
    }

    public Long getJobId() {
        return jobId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        JobInstance jobInstance = (JobInstance) obj;
        return Objects.equals(timestamp, jobInstance.timestamp) && Objects.equals(jobId, jobInstance.jobId);
    }

    @Override
    public int compareTo(JobInstance o) {
        return Long.compare(timestamp, o.timestamp);
    }
}
