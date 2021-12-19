package compiler.ast;

public class WhileStmt extends Stmt {
    private final Expr condition;
    private final Stmt whileBody;

    public WhileStmt(Expr condition, Stmt whileBody) {
        this.condition = condition;
        this.whileBody = whileBody;
    }
}
