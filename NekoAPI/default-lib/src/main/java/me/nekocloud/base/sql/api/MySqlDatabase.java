package me.nekocloud.base.sql.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nekocloud.base.sql.api.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

@Getter
public class MySqlDatabase implements AbstractDatabase {

    //Основные параметры подключения
    private final String host, password, user, data;

    // HikariCP pool
    private final HikariDataSource dataSource;

    //Подключение
    private Connection connection = null;

    //@Builder(builderMethodName = "newBuilder", builderClassName = "MySqlBuilder", buildMethodName = "create")
    private MySqlDatabase(MySqlBuilder builder) {

        this.host = builder.host;
        this.password = builder.password;
        this.user = builder.user;
        this.data = builder.data;

        this.dataSource = configureHikari(builder.properties);
    }

    public static MySqlBuilder newBuilder() {
        return new MySqlBuilder();
    }

    private HikariDataSource configureHikari(Properties properties) {
        properties.put("jdbcUrl", MessageFormat.format("jdbc:mysql://{0}:17483/{1}?useSSL=false", host, data));
        properties.put("username", user);
        properties.put("password", password);

        properties.put("poolName", "NekoCloud MySQL-HikariPool");
        properties.put("dataSource.useUnicode", true);
        properties.put("dataSource.characterEncoding", "utf8");
        properties.put("dataSource.cachePrepStmts", true);
        properties.put("dataSource.prepStmtCacheSize", 250);
        properties.put("dataSource.prepStmtCacheSqlLimit", 2048);
        properties.put("dataSource.leakDetectionThreshold", 6000);
        properties.put("dataSource.connectionTimeout", 120 * 1000);

        return new HikariDataSource(new HikariConfig(properties));
    }

    @Override
    public int execute(Query query) {
        return execute(query.toString());
    }

    @Override
    public <T> T executeQuery(Query query, ResponseHandler<ResultSet, T> handler) {
        return executeQuery(query.toString(), handler);
    }

    @Override
    public int execute(String query, Object... objects) {
        return execute(StatementWrapper.create(this, query), objects);
    }

    @Override
    public <T> T executeQuery(String query, ResponseHandler<ResultSet, T> handler, Object... objects) {
        return executeQuery(StatementWrapper.create(this, query), handler, objects);
    }

    @Override
    public int execute(StatementWrapper wrapper, Object... objects) {
        return wrapper.execute(PreparedStatement.RETURN_GENERATED_KEYS, objects);
    }

    @Override
    public <T> T executeQuery(StatementWrapper wrapper, ResponseHandler<ResultSet, T> handler, Object... objects) {
        return wrapper.executeQuery(handler, objects);
    }

    @Override
    public Connection getConnection() {
        refreshConnection();
        return connection;
    }

    protected void refreshConnection() {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(1000)) {
                return;
            }

            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Все нахуй обосралось с MySql - " + this.host + "/" + this.data, e);
        }
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            //nothing
        }
    }

    @NoArgsConstructor
    public static class MySqlBuilder {
        private String host;
        private String password;
        private String user;
        private String data;
        private final Properties properties = new Properties();

        public MySqlBuilder host(String host) {
            this.host = host;
            return this;
        }

        public MySqlBuilder property(String key, String value) {
            this.properties.put(key, value);
            return this;
        }

        public MySqlBuilder password(String password) {
            this.password = password;
            return this;
        }

        public MySqlBuilder user(String user) {
            this.user = user;
            return this;
        }

        public MySqlBuilder data(String data) {
            this.data = data;
            return this;
        }

        public MySqlDatabase create() {
            return new MySqlDatabase(this);
        }
    }
}
