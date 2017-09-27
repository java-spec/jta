package jta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;


public class ClassPathScript implements Script {
  private final URL resource;
  private final ScriptParser parser;

  public ClassPathScript(String resource){
    this(resource, new ScriptParser(';'));
  }

  public ClassPathScript(String resource, ScriptParser parser){
    this.resource = Objects.requireNonNull(ClassLoader.getSystemResource(resource));
    this.parser = Objects.requireNonNull(parser);
  }

  public void run(Connection connection) throws IOException {
    try(Stream<String> scripts = scripts()){
      scripts.forEach(statement -> {
        try{
          connection.createStatement().execute(statement);
        }catch(SQLException ex){
          throw new IllegalStateException("Invalid statement: `" + statement + "`", ex);
        }
      });
    }
  }

  private Stream<String> scripts() throws IOException {
    return parser.parse(new InputStreamReader(resource.openStream(), "UTF-8"));
  }
}
