package compiler.ast;

import compiler.bytecode.Label;
import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 关系表达式
 */
public class RelExpr extends LogicExpr implements Generator, Checker {
    private final Expr expr1;
    private final Expr expr2;
    private final Tag tag;

    public RelExpr(Expr expr1, Expr expr2, Tag tag) {
        super(expr1,expr2,tag);
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.tag = tag;
    }

    @Override
    public void generator(IRGenerator irGenerator, Label trueLabel, Label falseLabel) {
        irGenerator.visit(this,trueLabel,falseLabel);
    }

    @Override
    public Expr getExpr1() {
        return expr1;
    }

    @Override
    public Expr getExpr2() {
        return expr2;
    }

    @Override
    public Tag getTag() {
        return tag;
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return super.check(checker);
    }
}
