package compiler.ast;

/**
 * 选择表达式
 */
public class ChoseExpr extends Expr {
    private final Expr condition;
    private final Expr trueExpr;
    private final Expr falseExpr;

    public ChoseExpr(Expr condition, Expr trueExpr, Expr falseExpr) {
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }
}
