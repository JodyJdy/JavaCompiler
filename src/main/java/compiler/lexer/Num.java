

package compiler.lexer;

import compiler.enums.Tag;

/**
 * @author
 * @date 2021/12/18
 **/

public class Num extends Token {
    private final int num;
    public Num(int num) {
        super(Tag.NUM);
        this.num = num;
    }
}
