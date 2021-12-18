

package compiler;

import compiler.lexer.Lexer;
import compiler.parser.Parser;

import java.io.IOException;

/**
 * @author
 * @date 2021/12/18
 **/

public class Main {
    public static int a;
    public final  static   int b = 1;
    int i=3,j,y=4;
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        Parser parser = new Parser(lexer);
        //
        parser.clazz(false);

    }
}
