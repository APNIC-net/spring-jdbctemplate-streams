package net.apnic.example.jdbcstream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JdbcStreamApplication.class)
public class JdbcStreamApplicationTests {
    @Autowired
    JdbcStream jdbcStream;

	@Test
	public void contextLoads() {
	}

	@Test
	public void streamsData() throws SQLException, IOException {
        try (JdbcStream.StreamableQuery query = jdbcStream.streamableQuery("SELECT * FROM test_data")) {
            Set<String> results = query.stream()
                    .map(row -> row.getString("entry"))
                    .filter(s -> Character.isAlphabetic(s.charAt(0)))
                    .collect(Collectors.toSet());

            assertThat("3 results start with an alphabetic character", results.size(), is(equalTo(3)));
        }
    }

    @Test
    public void callbackStreaming() {
        Set<String> results = jdbcStream.streamQuery("SELECT * FROM test_data", stream -> stream
            .map(row -> row.getString("entry"))
            .filter(s -> Character.isAlphabetic(s.charAt(0)))
            .collect(Collectors.toSet()));

        assertThat("3 results start with an alphabetic character", results.size(), is(equalTo(3)));
    }

}
