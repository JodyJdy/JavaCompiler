package compiler.ast;

public class DoStmt extends Stmt {

    private final Stmt doBody;
    private final Expr condition;

    public DoStmt(Stmt doBody, Expr condition) {
        this.doBody = doBody;
        this.condition = condition;
    }
}
