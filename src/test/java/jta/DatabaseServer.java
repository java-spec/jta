package jta;

import java.io.OutputStream;
import java.io.PrintWriter;
import org.hsqldb.server.Server;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import static java.util.Objects.requireNonNull;

public class DatabaseServer implements TestRule {
  private static final PrintWriter NULL = new PrintWriter(new OutputStream(){
    public void write(int b){}
  });

  private final Server server = new Server();
  private final String[] databases;
  
  public DatabaseServer(String... databases){
    this.databases = requireNonNull(databases);
    log(NULL);
  }

  public DatabaseServer debug(){
    return log(new PrintWriter(System.out));
  }

  public DatabaseServer log(PrintWriter dest){
    server.setLogWriter(dest);
    return this;
  }

  public Statement apply(Statement base, Description description) {
    return new Statement(){
      public void evaluate() throws Throwable {
        start();
        try{
          base.evaluate();
        }finally{
          stop();
        }
      }
    };
  }

  private void start(){
    for(int i = 0; i < databases.length; i++){
      String name = databases[i];
      server.setDatabasePath(i, String.format("mem:%s", name));
      server.setDatabaseName(i, name);
    }
    server.start();
  }

  private void stop(){
    server.stop();
  }
}
