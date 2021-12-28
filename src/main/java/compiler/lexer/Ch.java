package compiler.lexer;

import compiler.enums.Tag;

/**
 * 字符
 */
public class Ch extends Token {
    private final char ch;
    Ch(char ch) {
        super(Tag.CH);
        this.ch = ch;
    }
}
