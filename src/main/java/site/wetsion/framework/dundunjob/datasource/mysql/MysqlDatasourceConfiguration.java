package site.wetsion.framework.dundunjob.datasource.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "site.wetsion.framework.dundunjob.datasource.mysql.dao")
public class MysqlDatasourceConfiguration {
}
