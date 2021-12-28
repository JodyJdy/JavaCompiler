package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

public class Assign extends Expr implements Generator, Checker {
    /**
     * 表达式左边
     */
    private final Expr left;
    /**
     * 赋值操纵， >=,<=
     */
    private final Expr right;

    public Assign(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return null;
    }
}
