
package auth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import util.config;

import javax.sql.DataSource;

public final class AuthDataSourceFactory {
    private AuthDataSourceFactory() {}

    public static DataSource build() {
        HikariConfig c = new HikariConfig();
        c.setJdbcUrl(config.get("auth.jdbc.url"));
        c.setUsername(config.get("auth.jdbc.user"));
        c.setPassword(config.get("auth.jdbc.pass"));
        return new HikariDataSource(c);
    }
}
