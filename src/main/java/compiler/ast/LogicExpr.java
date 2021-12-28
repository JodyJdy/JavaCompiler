package compiler.ast;

import compiler.bytecode.Label;
import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 逻辑表达式
 */
public class LogicExpr extends Expr implements Generator, Checker {
    private final Expr expr1;
    private final Expr expr2;
    private final Tag tag;

    public LogicExpr(Expr expr1, Expr expr2, Tag tag) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.tag = tag;
    }

    public void generator(IRGenerator irGenerator, Label trueLabel,Label falseLabel){
        irGenerator.visit(this,trueLabel,falseLabel);
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return super.check(checker);
    }

    public Expr getExpr1() {
        return expr1;
    }

    public Expr getExpr2() {
        return expr2;
    }

    public Tag getTag() {
        return tag;
    }
}
