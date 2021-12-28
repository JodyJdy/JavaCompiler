package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 项
 */
public class TermExpr extends Expr implements Generator, Checker {
    private final Tag tag;
    private final Expr term;

    public TermExpr(Tag tag, Expr term) {
        this.tag = tag;
        this.term = term;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return super.check(checker);
    }

    public Tag getTag() {
        return tag;
    }

    public Expr getTerm() {
        return term;
    }
}
