package site.wetsion.framework.dundunjob.datasource.mysql;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.datasource.JobInfo;
import site.wetsion.framework.dundunjob.datasource.JobInfoDatasource;
import site.wetsion.framework.dundunjob.datasource.mysql.dao.JobInfoDao;

import javax.annotation.Resource;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "job.info", name = "datasource", havingValue = "mysql")
public class MysqlJobInfoDatasource implements JobInfoDatasource {
    @Resource
    private JobInfoDao jobInfoDao;
    @Override
    public JobInfo loadJobInfo(Long jobId) {
        return jobInfoDao.getById(jobId);
    }

    @Override
    public List<JobInfo> loadAllJobInfo() {
        return jobInfoDao.getAll();
    }
}
