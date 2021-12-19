package compiler.ast;

public class ForStmt extends Stmt {
    private final Stmt assign;
    private final Expr condi;
    private final Stmt assign2;
    private final Stmt forBody;

    public ForStmt(Stmt assign, Expr condi, Stmt assign2, Stmt forBody) {
        this.assign = assign;
        this.condi = condi;
        this.assign2 = assign2;
        this.forBody = forBody;
    }
}
