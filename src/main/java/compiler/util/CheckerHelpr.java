

package compiler.util;

import compiler.enums.Tag;
import compiler.ast.Type;

import java.util.Arrays;
import java.util.List;

/**
 类型检查工具类
 **/

public class CheckerHelpr {
    public static boolean isBool(Type type){
        if(type.getTag() == null){
            return false;
        }
        return  type.getTag() == Tag.BOOL || type.getTag() == Tag.TRUE || type.getTag() == Tag.FALSE;
    }
    public static boolean isNumeric(Type type){
        if(type.getTag() == null){
            return false;
        }
        return numTag.contains(type.getTag());

    }

    private static List<Tag> numTag = Arrays.asList(Tag.NUM,Tag.REAL,Tag.BYTE,Tag.SHORT,Tag.INT,Tag.LONG,Tag.FLOAT,Tag.DOUBLE);
    /**
     * 获取高优先级的类型
     */
    public static Type getHighNumeric(Type a,Type b){
        int aindex = numTag.indexOf(a.getTag());
        int bindex =numTag.indexOf(b.getTag());
        if(aindex > bindex){
            return a;
        }
        return b;
    }
}
