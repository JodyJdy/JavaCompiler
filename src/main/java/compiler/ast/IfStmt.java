package compiler.ast;

public class IfStmt extends Stmt {
    private final Expr condition;
    private final Stmt thenBody;
    private final Stmt elseBody;

    public IfStmt(Expr condition, Stmt thenBody, Stmt elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }
}
