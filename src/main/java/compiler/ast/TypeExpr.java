package compiler.ast;

import compiler.parser.Type;

public class TypeExpr extends Expr {
    private Type type;

    public TypeExpr(Type type){
        this.type = type;
    }
}
