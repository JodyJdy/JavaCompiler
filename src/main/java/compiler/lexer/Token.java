

package compiler.lexer;

import compiler.enums.Tag;

/**
 * @author
 * @date 2021/12/18
 **/

public class Token {
    public final Tag tag;

    public Token(Tag t) {
        tag = t;
    }
    @Override
    public String toString() {
        return tag.toString();
    }

}
