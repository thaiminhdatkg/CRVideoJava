package vn.crln.video.crvideo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.sqlite.SQLiteDataSource;

@SpringBootConfiguration
public class AppConfig {
    @Value("${db.url}")
    String dbUrl;

    @Bean(name="dataSourceMain")
    public SQLiteDataSource dataSource() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(dbUrl);
        return dataSource;
    }
}
