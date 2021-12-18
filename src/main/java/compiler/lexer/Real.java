

package compiler.lexer;

import compiler.enums.Tag;

/**
 * @author
 * @date 2021/12/18
 **/

public class Real extends Token {
    private final float value;
    public Real(float value) {
        super(Tag.REAL);
        this.value = value;
    }
}
