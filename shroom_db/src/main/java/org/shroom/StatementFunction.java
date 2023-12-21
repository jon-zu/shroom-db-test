package org.shroom;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementFunction {
    void run(PreparedStatement stmt) throws SQLException;
}
