

package compiler.lexer;

import compiler.enums.Tag;

/**
 * token
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

    public static Token tagToken(Tag tag){
        return new Token(tag);
    }
    public static Token wordToken(String word){
        return new Word(word);
    }

}
