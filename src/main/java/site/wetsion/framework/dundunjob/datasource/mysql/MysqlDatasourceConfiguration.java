package site.wetsion.framework.dundunjob.datasource.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "job.info", name = "datasource", havingValue = "mysql")
@Configuration
@MapperScan(basePackages = "site.wetsion.framework.dundunjob.datasource.mysql.dao")
public class MysqlDatasourceConfiguration {
}
