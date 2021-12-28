package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * for语句
 */
public class ForStmt extends Stmt implements Generator, Checker {
    private final Stmt assign;
    private final Expr condi;
    private final Stmt assign2;
    private final Stmt forBody;

    public ForStmt(Stmt assign, Expr condi, Stmt assign2, Stmt forBody) {
        this.assign = assign;
        this.condi = condi;
        this.assign2 = assign2;
        this.forBody = forBody;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    public Stmt getAssign() {
        return assign;
    }

    public Expr getCondi() {
        return condi;
    }

    public Stmt getAssign2() {
        return assign2;
    }

    public Stmt getForBody() {
        return forBody;
    }

    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }
}
