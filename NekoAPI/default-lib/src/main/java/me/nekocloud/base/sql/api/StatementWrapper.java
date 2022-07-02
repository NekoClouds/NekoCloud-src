package me.nekocloud.base.sql.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StatementWrapper {

    private final AbstractDatabase database;

    private String query;

    private boolean sync; //по умолчанию все асинхронно

    public StatementWrapper setQuery(String query) {
        this.query = query;
        return this;
    }

    public StatementWrapper sync() { //тут можно сделать синхронно
        sync = true;
        return this;
    }

    private PreparedStatement createStatement(int generatedKeys, Object... objects) throws SQLException {
        PreparedStatement ps = database.getConnection().prepareStatement(query, generatedKeys);

        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                ps.setObject(i + 1, objects[i]);
            }
        }

        if (objects == null || objects.length == 0) {
            ps.clearParameters();
        }

        return ps;
    }

    private void validateQuery() {
        if (query == null || query.isEmpty()) {
            throw new IllegalStateException("пошел нахуй, тут query null");
        }
    }

    public int execute(int generatedKeys, Object... objects) {
        validateQuery();

        Callable<Integer> callable = () -> {
            try (PreparedStatement ps = createStatement(generatedKeys, objects)) {
                ps.execute();

                ResultSet rs = ps.getGeneratedKeys();
                return (rs.next() ? rs.getInt(1) : -1);
            }
        };

        return handle(callable);
    }

    public <T> T executeQuery(ResponseHandler<ResultSet, T> handler, Object... objects) {
        validateQuery();

        Callable<T> callable = () -> {
            try (PreparedStatement ps = createStatement(PreparedStatement.NO_GENERATED_KEYS, objects)) {

                ResultSet rs = ps.executeQuery();

                return handler.handleResponse(rs);
            }
        };

        return handle(callable);
    }

    private <T> T handle(Callable<T> callable) {
        if (!sync) {
            Future<T> future = AbstractDatabase.QUERY_EXECUTOR.submit(callable);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to execute async query ", e);
            }

        } else try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute sync query ", e);
        }
    }

    public static StatementWrapper create(AbstractDatabase database, String query) {
        return new StatementWrapper(database).setQuery(query);
    }
}
