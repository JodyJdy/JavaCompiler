

package compiler.lexer;

import compiler.enums.Tag;

/**
 * @author
 * @date 2021/12/18
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
