package org.shroom;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SelectMapperFunction<T> {
    T run(ResultSet rs) throws SQLException;
}
