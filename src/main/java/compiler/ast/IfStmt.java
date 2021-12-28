package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * if语句
 */
public class IfStmt extends Stmt implements Generator, Checker {
    private final Expr condition;
    private final Stmt thenBody;
    private final Stmt elseBody;

    public IfStmt(Expr condition, Stmt thenBody, Stmt elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    public Expr getCondition() {
        return condition;
    }

    public Stmt getThenBody() {
        return thenBody;
    }

    public Stmt getElseBody() {
        return elseBody;
    }

    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }
}
