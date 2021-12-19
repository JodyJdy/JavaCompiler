package compiler.ast;

import compiler.enums.Tag;

public class TermExpr extends Expr {
    private final Tag tag;
    private final Expr term;

    public TermExpr(Tag tag, Expr term) {
        this.tag = tag;
        this.term = term;
    }
}
