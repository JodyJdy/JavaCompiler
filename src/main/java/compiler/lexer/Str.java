package compiler.lexer;

import compiler.enums.Tag;

public class Str extends Token {
    private final String str;
    public Str(String str) {
        super(Tag.STR);
        this. str = str;
    }
}
