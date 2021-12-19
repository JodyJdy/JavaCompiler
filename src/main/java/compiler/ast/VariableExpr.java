package compiler.ast;

public class VariableExpr extends Expr {
    private String var;
    public VariableExpr(String var){
        this.var = var;
    }
}
