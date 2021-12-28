package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * do-while语句
 */
public class DoStmt extends Stmt implements Generator, Checker {

    private final Stmt doBody;
    private final Expr condition;

    public DoStmt(Stmt doBody, Expr condition) {
        this.doBody = doBody;
        this.condition = condition;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    public Stmt getDoBody() {
        return doBody;
    }

    public Expr getCondition() {
        return condition;
    }

    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }
}
