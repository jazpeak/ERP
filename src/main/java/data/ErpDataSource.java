package data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import util.config;

import javax.sql.DataSource;

public final class ErpDataSource {
    private ErpDataSource() {}

    public static DataSource build() {
        HikariConfig c = new HikariConfig();
        c.setJdbcUrl(config.get("erp.jdbc.url"));
        c.setUsername(config.get("erp.jdbc.user"));
        c.setPassword(config.get("erp.jdbc.pass"));
        // Optional (only if keys exist in application.properties)
        // c.setMaximumPoolSize(Integer.parseInt(Config.get("hikari.maximumPoolSize")));
        return new HikariDataSource(c);
    }
}

