package me.nekocloud.base.sql.api;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.nekocloud.base.sql.api.query.Query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface AbstractDatabase {

    ExecutorService QUERY_EXECUTOR = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder()
                    .setNameFormat("NekoCloud MySQL-HikariPool-Worker #%s")
                    .setDaemon(true)
                    .build());

    int execute(String query, Object... objects);

    int execute(Query query);

    int execute(StatementWrapper wrapper, Object... objects);

    <T> T executeQuery(String query, ResponseHandler<ResultSet, T> handler, Object... objects);

    <T> T executeQuery(Query query, ResponseHandler<ResultSet, T> handler);

    <T> T executeQuery(StatementWrapper wrapper, ResponseHandler<ResultSet, T> handler, Object... objects);

    void close();

    Connection getConnection();
}
