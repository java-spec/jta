package jta;

import jta.Database.Action;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class JDBCTransactionTest {
  private static final Action INSERTION = connection -> {
    PreparedStatement statement = connection.prepareStatement("insert into USERS(NAME,MALE) values(?, ?)");
    statement.setString(1, "Bob");
    statement.setBoolean(2, true);
    assertThat(statement.executeUpdate(), equalTo(1));
  };
  private static final Action DISABLE_AUTO_COMMIT = connection -> connection.setAutoCommit(false);

  @Rule
  public final DatabaseServer server = new DatabaseServer("test");
  private final Database database = new Database(new JDBCDataSource(){{
    setURL("jdbc:hsqldb:hsql:///test");
  }});

  @Before
  public void setUp() throws Throwable {
    database.executeScript(new ClassPathScript("bootstrap.sql"));
  }

  @Test
  public void commitTransactionAutomatically() throws Throwable {
    database.execute(INSERTION);
    database.execute(this::checkingDataWereInserted);
  }

  @Test
  public void commitTransactionManually() throws Throwable {
    database.execute(DISABLE_AUTO_COMMIT.then(INSERTION));
    database.execute(this::checkingNoEffects);

    database.execute(DISABLE_AUTO_COMMIT.then(INSERTION).then(Connection::commit));
    database.execute(this::checkingDataWereInserted);
  }

  private void checkingNoEffects(Connection connection) throws SQLException {
    ResultSet rs = connection.prepareStatement("select NAME,MALE from USERS").executeQuery();
    assertFalse(rs.next());
  }

  private void checkingDataWereInserted(Connection connection) throws SQLException {
    ResultSet rs = connection.prepareStatement("select NAME,MALE from USERS").executeQuery();
    assertTrue(rs.next());
    assertThat(rs.getString(1), equalTo("Bob"));
    assertThat(rs.getBoolean(2), equalTo(true));
  }
}
