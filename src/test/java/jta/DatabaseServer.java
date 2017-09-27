package jta;
import org.hsqldb.server.Server;

public class DatabaseServer {
  private final Server server = new Server();
  public DatabaseServer(String... databases){
    for(int i = 0; i < databases.length; i++){
      String name = databases[i];
      server.setDatabasePath(i, String.format("mem:%s", name));
      server.setDatabaseName(i, name);
    }
  }

  public void start(){
    server.start();
  }

  public void stop(){
    server.stop();
  }
}
