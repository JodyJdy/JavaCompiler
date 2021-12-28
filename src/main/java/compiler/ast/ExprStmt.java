package compiler.ast;

import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 当成语句执行的表达式
 */
public class ExprStmt extends Stmt implements Generator {

    private final Expr expr;

    public ExprStmt(Expr expr) {
        this.expr = expr;
    }


    @Override
    public void generate(IRGenerator irGenerator) {
       irGenerator.visit(this);
    }

    public Expr getExpr() {
        return expr;
    }
}
