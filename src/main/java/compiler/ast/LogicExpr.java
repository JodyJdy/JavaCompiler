package compiler.ast;

import compiler.enums.Tag;

public class LogicExpr extends Expr {
    private final Expr expr1;
    private final Expr expr2;
    private final Tag tag;

    public LogicExpr(Expr expr1, Expr expr2, Tag tag) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.tag = tag;
    }
}
