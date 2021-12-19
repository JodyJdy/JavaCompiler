package compiler.ast;

import compiler.enums.Tag;

/**
 * 二元运算
 */
public class BinaryExpr extends Expr {
    private final Tag tag;
    private final Expr expr1;
    private final Expr expr2;

    public BinaryExpr(Tag tag, Expr expr1, Expr expr2) {
        this.tag = tag;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
}
