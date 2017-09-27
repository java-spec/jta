package jta;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

interface Script{
  void run(Connection connection) throws SQLException, IOException;
}
