# Spring JdbcTemplate with Java 8 Streams

This repository contains example code for how one might use Spring's
[JdbcTemplate] with the Java 8 [Stream] API.

Sample code

  - `JdbcStream.streamableQuery()`: an extension of [JdbcTemplate] to make a
    `Closeable` streamable query.  This gets into the protected guts of
    `JdbcTemplate` to manage a connection's lifetime, and becomes a resource
     which must be closed.
  - `JdbcStream.streamQuery()`: a callback-style query interface allowing
    stream processing inside `JdbcTemplate`'s own resource management system.
    This version will not work for empty result sets.
  - `JdbcStreamApplication.streamer`: a bean implementing `streamQuery()` as a
    consumer of a `JdbcTemplate`.  This one correctly handles empty result sets,
    and is the basis of the [gist] and [blog post] which this code fed.

There's a performance test suite which will go out of memory for query methods
which inadvertently cache the entire result set in memory, and a basic test
suite demonstrating the streaming code performs as expected.

The test cases show what not to do with this interface: they reimplement trivial
SQL queries in Java code.

[JdbcTemplate]: https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html
[Stream]: https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
