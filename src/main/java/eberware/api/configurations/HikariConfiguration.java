package eberware.api.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eberware.api.core.systems.libaries.DatabaseLibrary;
import eberware.api.core.systems.managers.ScriptorianManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class HikariConfiguration extends HikariConfig {

    private void setup() {
        setDriverClassName(DatabaseLibrary.get_driver());
        setJdbcUrl(DatabaseLibrary.get_urlPath());
        setUsername(DatabaseLibrary.get_user());
        setPassword(DatabaseLibrary.get_password());
    }

    @Bean
    public DataSource dataSource() {
        try {
            ScriptorianManager.onStartup();
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        setup();
        return new HikariDataSource(this);
    }
}
