package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 二元运算
 */
public class BinaryExpr extends Expr implements Generator, Checker {
    private final Tag tag;
    private final Expr expr1;
    private final Expr expr2;

    public BinaryExpr(Tag tag, Expr expr1, Expr expr2) {
        this.tag = tag;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public Tag getTag() {
        return tag;
    }

    public Expr getExpr1() {
        return expr1;
    }

    public Expr getExpr2() {
        return expr2;
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
}
