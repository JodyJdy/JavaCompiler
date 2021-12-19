package compiler.ast;

public class ReturnStmt extends Stmt {
    private Expr expr;

    public ReturnStmt(Expr expr){
        this.expr = expr;
    }

}
