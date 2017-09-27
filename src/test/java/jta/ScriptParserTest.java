package jta;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class ScriptParserTest {
  private final ScriptParser parser = new ScriptParser(';');

  @Test
  public void skipsComments() throws Throwable {
    assertThat(parser.parse("#COMMENT").count(), equalTo(0L));
  }

  @Test
  public void parseSsingleStatement() throws Throwable {
    String statement = "drop table USERS if exists";
    assertThat(parser.parse(statement).collect(toList()), equalTo(asList(statement)));
  }

  @Test
  public void supportsUnicodeChars() throws Throwable {
    String statement = "update USERS set name='\u7ba1\u7406\u5458'";
    assertThat(parser.parse(statement).collect(toList()), equalTo(asList(statement)));
  }

  @Test
  public void parseMultiStatements() throws Throwable {
    String statement = "update USERS set name='bob';update USERS set name='joe';";
    assertThat(parser.parse(statement).collect(toList()), equalTo(asList(statement.split(";"))));
  }

  @Test
  public void parseMultilineStatement() throws Throwable {
    String statement = "create table USERS(\r\n" +
                         "NAME varchar(10) not null primary key\r\n" +
                       ")";
    assertThat(parser.parse(statement).collect(toList()), equalTo(asList(statement.split(";"))));
  }

  @Test
  public void skipsEmptyLines() throws Throwable {
    assertThat(parser.parse("\r\n").count(), equalTo(0L));
  }

  @Test
  public void closeReaderWhileStreamClosed() throws Throwable {
    AtomicBoolean closed = new AtomicBoolean(false);

    Stream<String> scripts = parser.parse(notifiesOnClosed(()-> closed.set(true)));
    assertFalse(closed.get());

    scripts.close();
    assertTrue(closed.get());
  }

  private Reader notifiesOnClosed(Runnable action){
    return new StringReader("unused"){
      public void close(){
        action.run();
      }
    };
  }
}

