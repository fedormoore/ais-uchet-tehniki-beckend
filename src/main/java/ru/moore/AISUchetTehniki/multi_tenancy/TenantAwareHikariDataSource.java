package ru.moore.AISUchetTehniki.multi_tenancy;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class TenantAwareHikariDataSource extends HikariDataSource {

    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        connectionThreadLocal.set(connection);
        setTenantId();
        return connection;
    }

    public void setTenantId() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (TenantContext.getTenantId() != null) {
            try (Statement sql = connection.createStatement()) {
                UUID tenantName = TenantContext.getTenantId();
                sql.execute("SET app.current_tenantId = '" + tenantName + "'");
            }
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);

        try (Statement sql = connection.createStatement()) {
            sql.execute("SET app.current_tenantId = '" + TenantContext.getTenantId() + "'");
        }

        return connection;
    }

}
