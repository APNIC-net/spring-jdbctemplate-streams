package net.apnic.example.jdbcstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SpringBootApplication
public class JdbcStreamApplication implements EmbeddedDatabaseConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(JdbcStreamApplication.class, args);
    }

    public interface QueryStream {
        public <T> T streamQuery(String sql, Function<Stream<SqlRowSet>, ? extends T> streamer, Object... args);
    }

    private enum IsParallel {
        SEQUENTIAL(false),
        PARALLEL(true);

        private final boolean flag;

        IsParallel(boolean flag) {
            this.flag = flag;
        }

        boolean getFlag() { return flag; }
    }

    /**
     * The gist code at
     *   https://gist.github.com/codebje/58d1b12e7a2d0ed31b3a#file-jdbcstreams-java
     *
     * @param jdbcTemplate the JdbcTemplate to use
     * @return a QueryStream instance
     */
    @Bean
    public QueryStream streamer(JdbcTemplate jdbcTemplate) {
        return new QueryStream() {
            @Override
            public <T> T streamQuery(String sql, Function<Stream<SqlRowSet>, ? extends T> streamer, Object... args) {
                return jdbcTemplate.query(sql, resultSet -> {
                    final SqlRowSet rowSet = new ResultSetWrappingSqlRowSet(resultSet);

                    if (!rowSet.next()) {
                        return streamer.apply(StreamSupport.stream(Spliterators.emptySpliterator(),
                                IsParallel.PARALLEL.getFlag()));
                    }

                    Spliterator<SqlRowSet> spliterator = Spliterators.spliteratorUnknownSize(new Iterator<SqlRowSet>() {
                        private boolean first = true;
                        @Override
                        public boolean hasNext() {
                            return !rowSet.isLast();
                        }

                        @Override
                        public SqlRowSet next() {
                            if (!first || !rowSet.next()) {
                                throw new NoSuchElementException();
                            }
                            first = false;
                            return rowSet;
                        }
                    }, Spliterator.IMMUTABLE);
                    return streamer.apply(StreamSupport.stream(spliterator,
                            IsParallel.SEQUENTIAL.getFlag()));
                }, args);
            }
        };
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties connectionProperties, String s) {

    }

    @Override
    public void shutdown(DataSource dataSource, String s) {

    }
}
