package net.apnic.example.jdbcstream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class JdbcStream {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcStream(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Stream<SqlRow> queryForStream(String sql, Object... args) {
        Supplier<Spliterator<SqlRow>> supplier = () -> {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, args);
            SqlRow row = new SqlRowAdapter(rowSet);
            return Spliterators.<SqlRow>spliteratorUnknownSize(new Iterator<SqlRow>() {
                @Override
                public boolean hasNext() {
                    return !rowSet.isLast();
                }

                @Override
                public SqlRow next() {
                    if (!rowSet.next()) {
                        throw new NoSuchElementException();
                    }
                    return row;
                }
            }, Spliterator.IMMUTABLE);
        };
        boolean parallel = false;
        return StreamSupport.stream(supplier, Spliterator.IMMUTABLE, parallel);
    }

    /**
     * Facade to hide the cursor movement methods of an SqlRowSet
     */
    public interface SqlRow {
        //TODO - implement remainting getters
        Long getLong(String columnLabel);
        String getString(String columnLabel);
        Timestamp getTimestamp(String columnLabel);
    }

    public class SqlRowAdapter implements SqlRow{
        private final SqlRowSet sqlRowSet;

        public SqlRowAdapter(SqlRowSet sqlRowSet) {
            this.sqlRowSet = sqlRowSet;
        }

        @Override
        public Long getLong(String columnLabel) {
            return sqlRowSet.getLong(columnLabel);
        }

        @Override
        public String getString(String columnLabel) {
            return sqlRowSet.getString(columnLabel);
        }

        @Override
        public Timestamp getTimestamp(String columnLabel) {
            return sqlRowSet.getTimestamp(columnLabel);
        }
    }
}
