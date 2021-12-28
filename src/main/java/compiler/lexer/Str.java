package compiler.lexer;

import compiler.enums.Tag;

/**
 * 字符串
 */
public class Str extends Token {
    private final String str;
    public Str(String str) {
        super(Tag.STR);
        this. str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
