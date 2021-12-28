package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * while语句
 */
public class WhileStmt extends Stmt implements Generator, Checker {
    private final Expr condition;
    private final Stmt whileBody;

    public WhileStmt(Expr condition, Stmt whileBody) {
        this.condition = condition;
        this.whileBody = whileBody;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) {
        return super.check(checker);
    }

    public Expr getCondition() {
        return condition;
    }

    public Stmt getWhileBody() {
        return whileBody;
    }
}
