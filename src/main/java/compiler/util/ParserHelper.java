

package compiler.util;

import compiler.ast.ClassNode;
import compiler.enums.Tag;
import compiler.lexer.Token;
import compiler.lexer.Word;
import compiler.ast.ArrayType;
import compiler.parser.ClassesLoader;
import compiler.ast.Type;

import java.util.HashSet;
import java.util.Set;

import static compiler.enums.Tag.FINAL;
import static compiler.enums.Tag.STATIC;

/**
 * 解析工具类
 **/

public class ParserHelper {
    private static Set<String> typeSet = new HashSet<>(2);
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
    public static boolean isBasic(Tag tag){
        switch (tag){
            case BYTE:
            case CHAR:
            case SHORT:
            case INT:case LONG:case FLOAT:case DOUBLE:case VOID: case BOOL:return true;
            default:return false;
        }
    }
    public static Type type(Token token){
        if(isBasic(token.tag)){
            return new Type(token.tag);
        }
        Word word = (Word)token;
        return new Type(word.getStr());
    }
    public static Type arrayType(Token token,int dimen){
        if(isBasic(token.tag)){
            return new ArrayType(token.tag,dimen);
        }
        Word word = (Word)token;
        return new ArrayType(word.getStr(),dimen);
    }

    /**
     * 是否有访问控制符
     */
    public static boolean isAccessControl(Token look){
        switch (look.tag){
            case PUBLIC:
            case DEFATULT:
            case PROTECTED:
            case PRIVATE:return true;
            default:return false;
        }
    }

    /**
     * 是否是静态的
     */
    public static boolean isStatic(Token look){
        return look.tag == STATIC;
    }
    /**
     * 是否是final的
     */
    public static boolean isFinal(Token look){
        return look.tag == FINAL;
    }
    /**
     * 是否是 比较关系的运算符
     */
    public static boolean isRelation(Token look){
        switch (look.tag){
            case EQ:case NE:case GE:case GT:case LE:case LI:return true;
            default:return false;
        }
    }

    /**
     * 将类名转成完整名称
     * 有两种 1. A需要转换  2. xx.xx.xx.A 不需要转换
     */
    public static String tranClassNameWithPackage(String name, ClassNode classNode){
        if(name == null){
            return null;
        }
        //输入的是完整类路径
        if(ClassesLoader.loadedClass(name)){
            return name;
        }
        //输入的是类名，需要查找出完整路径
        int count = 0;
        String result = null;
        for(String c : classNode.getImportedClass()){
            if(c.endsWith("." + name)){
                count++;
                result = c;
            }
        }
        if(count > 1){
            System.err.println("类名冲突");
        }
        return result;
    }

}
