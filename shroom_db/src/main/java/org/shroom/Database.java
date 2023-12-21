package org.shroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import com.zaxxer.hikari.pool.HikariProxyConnection;

public class Database implements AutoCloseable {
    private Connection conn;
    private boolean sqlite;

    public Database(Connection conn, boolean sqlite) {
        this.conn = conn;
        this.sqlite = sqlite;
    }

    void testConnection() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            throw new SQLException("Connection is closed");
        }
    }

    void safeClose() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            this.conn.close();
        }
    }

    @Override
    public void close() throws SQLException {
        this.safeClose();
    }

    public void createTable(
            String query) throws SQLException {
        testConnection();
        try (PreparedStatement stmt = this.conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            conn.close();
            throw ex;
        }
    }

    public void update(
            String query,
            StatementFunction prepare) throws SQLException {
        testConnection();
        try (PreparedStatement stmt = this.conn.prepareStatement(query)) {
            prepare.run(stmt);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            conn.close();
            throw ex;
        }
    }

    public void delete(
            String query,
            StatementFunction prepare) throws SQLException {
        testConnection();
        try (PreparedStatement stmt = this.conn.prepareStatement(query)) {
            prepare.run(stmt);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            conn.close();
            throw ex;
        }
    }

    public Integer insertOne(
            String query,
            StatementFunction prepare) throws SQLException {
        testConnection();
        try (PreparedStatement stmt = this.conn.prepareStatement(query)) {
            prepare.run(stmt);
            stmt.executeUpdate();

            if (this.sqlite) {
                return 1; //TODO
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            this.safeClose();
            throw ex;
        }
    }

    public List<Integer> insertMulti(
            String query,
            StatementFunction prepare) throws SQLException {
        testConnection();

        try (PreparedStatement stmt = this.conn.prepareStatement(query)) {
            prepare.run(stmt);
            stmt.executeUpdate();

            var ids = new ArrayList<Integer>();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                ids.add(rs.getInt(1));
            }
            return ids;
        } catch (SQLException ex) {
            this.safeClose();
            throw ex;
        }
    }

    public <T> T selectOne(
            String query,
            StatementFunction prepare,
            SelectMapperFunction<T> mapper) throws SQLException {
        testConnection();

        try (PreparedStatement stmt = this.conn.prepareStatement(query)) {
            prepare.run(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return mapper.run(rs);
            }
        } catch (SQLException ex) {
            this.safeClose();
            throw ex;
        }
    }

    public <T> List<T> selectMulti(
            String query,
            StatementFunction prepare,
            SelectMapperFunction<T> mapper) throws SQLException {
        testConnection();

        try (PreparedStatement stmt = this.conn.prepareStatement(query)) {
            prepare.run(stmt);

            var results = new ArrayList<T>();
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                results.add(mapper.run(rs));
            }
            return results;
        } catch (SQLException ex) {
            this.safeClose();
            throw ex;
        }
    }
}
