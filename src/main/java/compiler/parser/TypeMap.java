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
    public static Type generateType(Token token){
        if(isBasic(token.tag)) {
            return new Type(token.tag);
        }
        return null;
    }
    public static boolean isBasic(Tag tag){
        switch (tag){
            case BYTE:
            case CHAR:
            case SHORT:
            case INT:case LONG:case FLOAT:case DOUBLE:case VOID: case BOOL:return true;
            default:return false;
        }
    }
    public static Type generateType(String type){
        if(typeSet.contains(type)){
            return new Type(type);
        }
        return null;
    }
}
