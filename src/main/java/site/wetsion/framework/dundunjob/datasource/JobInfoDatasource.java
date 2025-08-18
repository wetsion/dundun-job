package site.wetsion.framework.dundunjob.datasource;

import java.util.List;

public interface JobInfoDatasource {

    JobInfo loadJobInfo(Long jobId);

    List<JobInfo> loadAllJobInfo();
}
