package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 选择表达式
 */
public class ChoseExpr extends Expr  implements Generator, Checker {
    private final Expr condition;
    private final Expr trueExpr;
    private final Expr falseExpr;

    public ChoseExpr(Expr condition, Expr trueExpr, Expr falseExpr) {
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }

    public Expr getCondition() {
        return condition;
    }

    public Expr getTrueExpr() {
        return trueExpr;
    }

    public Expr getFalseExpr() {
        return falseExpr;
    }
}
