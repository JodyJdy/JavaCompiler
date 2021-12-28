package compiler.ast;

import compiler.ast.Expr;
import compiler.enums.Tag;

/**
 * 类型
 */
public class Type extends Expr {
    /**
     * 类名 = package+className
     */
    private String type;
    /**
     * 基本数据类型
     */
    private Tag tag;
    public Type(String type){
        this.type = type;
    }
    public Type(Tag tag){
        this.tag = tag;
    }

    @Override
    public String toString() {
        if(type != null){
            return type;
        }
        return tag.getStr();
    }

    public String getType() {
        return type;
    }

    public Tag getTag() {
        return tag;
    }
}
