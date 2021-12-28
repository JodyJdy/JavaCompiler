

package compiler.lexer;

import compiler.enums.Tag;

/**
 * 浮点数字
 **/

public class Real extends Token {
    private final float value;
    public Real(float value) {
        super(Tag.REAL);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
