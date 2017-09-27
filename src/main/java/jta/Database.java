package jta;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {
  private final DataSource datasource;

  public Database(DataSource datasource) {
    this.datasource = datasource;
  }

  private Connection connection() throws SQLException {
    return datasource.getConnection();
  }

  public void executeScript(Script script) throws Throwable {
    execute(script::run);
  }

  public void execute(Action action) throws Throwable {
    try(Connection connection = connection()){
      action.run(connection);
    }
  }

  public interface Action {
    void run(Connection connection) throws Throwable;

    default Action then(Action after){
      return connection -> {
        run(connection);
        after.run(connection);
      };
    }
  }
}
