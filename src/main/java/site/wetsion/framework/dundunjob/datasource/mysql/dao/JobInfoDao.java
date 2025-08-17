package site.wetsion.framework.dundunjob.datasource.mysql.dao;

import org.apache.ibatis.annotations.Param;
import site.wetsion.framework.dundunjob.datasource.JobInfo;

import java.util.List;

public interface JobInfoDao {

    JobInfo getById(@Param("id") Long id);

    List<JobInfo> getAll();
}
