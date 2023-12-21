package org.shroom;

import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseProvider {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setPoolName("shroompool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:shroom.db");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        ds = new HikariDataSource(config);
    }

    private DatabaseProvider() {
    }

    public static Database getDB() throws SQLException {
        return new Database(ds.getConnection(), true);
    }
}
