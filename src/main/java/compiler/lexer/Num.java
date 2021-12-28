

package compiler.lexer;

import compiler.enums.Tag;

/**
    整型数字
 **/

public class Num extends Token {
    private final int num;
    public Num(int num) {
        super(Tag.NUM);
        this.num = num;
    }

    @Override
    public String toString() {
        return String.valueOf(num);
    }
}
