package compiler.lexer;

import compiler.enums.Tag;

public class Ch extends Token {
    private final char ch;
    public Ch(char ch) {
        super(Tag.CH);
        this.ch = ch;
    }
}
