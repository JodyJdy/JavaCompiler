

package compiler.lexer;

import compiler.enums.Tag;

/**
   读取到的一个词语，标识符就是一个Word
 **/

public class Word extends Token {
    private String str;
    public Word(String str) {
        super(Tag.ID);
        this. str = str;
    }

    public String getStr() {
        return str;
    }
}
