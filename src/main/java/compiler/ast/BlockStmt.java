

package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 代码块,方法体或者方法中的代码块
 */
public class BlockStmt extends Stmt implements Generator, Checker {
    private final Stmt stmt;

    public BlockStmt(Stmt stmt) {
        this.stmt = stmt;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
       irGenerator.visit(this);
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }
}
