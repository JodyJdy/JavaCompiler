package compiler.ast;

/**
 * 类型表达式
 */
public class TypeExpr extends Expr {
    private Type type;

    public TypeExpr(Type type){
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
