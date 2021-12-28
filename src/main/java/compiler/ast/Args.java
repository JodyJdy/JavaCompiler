package compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class Args extends Node {
    private final List<Expr> exprs;

    public Args(List<Expr> exprs) {
        this.exprs = exprs;
    }
    public Args(){
        this.exprs = new ArrayList<>();
    }

    public List<Expr> getExprs() {
        return exprs;
    }
}
