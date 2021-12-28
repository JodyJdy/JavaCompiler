

package compiler.bytecode;

import compiler.lexer.Token;

/**
    放常量到栈里面
 **/

public class Constant extends ByteCode {
    private final Token token;

    public Constant(Token token) {
        this.token = token;

    }

    @Override
    public String toString() {
        return "constant " + token.toString();
    }
}
