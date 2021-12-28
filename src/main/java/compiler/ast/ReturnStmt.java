package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * return 语句
 */
public class ReturnStmt extends Stmt implements Generator, Checker {
    private Expr expr;

    public ReturnStmt(Expr expr){
        this.expr = expr;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return null;
    }

    public Expr getExpr() {
        return expr;
    }
}
