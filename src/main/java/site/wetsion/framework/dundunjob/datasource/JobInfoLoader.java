package site.wetsion.framework.dundunjob.datasource;

import java.util.List;

public interface JobInfoLoader {

    JobInfo loadJobInfo(Long jobId);

    List<JobInfo> loadAllJobInfo();
}
