package jta;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static java.util.stream.StreamSupport.stream;

public class ScriptParser{
  private final int delimeter;

  public ScriptParser(int delimeter){
    this.delimeter = delimeter;
  }

  public Stream<String> parse(String script){
    return parse(new StringReader(script));
  }

  public Stream<String> parse(Reader script){
    return stream(tokenizer(script), false).onClose(()->{
      try{
        script.close();
      }catch(IOException ex){
        throw new UncheckedIOException(ex);
      }
    });
  }

  private Spliterator<String> tokenizer(Reader reader){
    return new AbstractSpliterator<String>(Long.MAX_VALUE, Spliterator.NONNULL | Spliterator.IMMUTABLE){
      private final StreamTokenizer tokenizer = splitBy(delimeter, reader);

      private StreamTokenizer splitBy(int delimeter, Reader reader){
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer.resetSyntax();
        tokenizer.wordChars(0, 0xFF);
        tokenizer.whitespaceChars(delimeter, delimeter);
        tokenizer.commentChar('#');
        return tokenizer;
      }

      public boolean tryAdvance(Consumer<? super String> action){
        int token = next();
        if(token == StreamTokenizer.TT_WORD){
          String script = tokenizer.sval.trim();
          if(!script.isEmpty()) action.accept(tokenizer.sval);
          return true;
        }
        return false;
      }

      private int next(){
        try{
          return tokenizer.nextToken();
        }catch(IOException ex){
          throw new UncheckedIOException(ex);
        }
      }
    };
  }
}
