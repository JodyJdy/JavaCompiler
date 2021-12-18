package compiler.parser;

import compiler.enums.Tag;
import compiler.lexer.Token;

import java.util.HashSet;
import java.util.Set;

/**
 *存放运行中牵扯到的所有类型
 */
public class TypeMap {
    private static Set<String> typeSet = new HashSet<String>(2);
    static {
        typeSet.add(Tag.INT.getStr());
        typeSet.add(Tag.BYTE.getStr());
        typeSet.add(Tag.CHAR.getStr());
        typeSet.add(Tag.SHORT.getStr());
        typeSet.add(Tag.LONG.getStr());
        typeSet.add(Tag.FLOAT.getStr());
        typeSet.add(Tag.DOUBLE.getStr());
        typeSet.add(Tag.VOID.getStr());
        typeSet.add(Tag.BOOL.getStr());
    }

    public static void  addType(String type){
        typeSet.add(type);
    }
    public static boolean contains(String type){
        return typeSet.contains(type);
    }
    public static boolean contains(Token token){
        return typeSet.contains(token.tag.getStr());
    }
}
