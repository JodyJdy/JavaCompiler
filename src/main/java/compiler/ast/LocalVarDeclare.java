package compiler.ast;

/**
 * 局部变量声明
 */
public class LocalVarDeclare extends Stmt  {
    /**
     * 类型声明
     */
    private final Expr typeExpr;
    /**
     * 变量名
     */
    private final String varName;
    /**
     * 如果声明带有初始值
     */
    private final Expr initValue;

    public LocalVarDeclare(Expr typeExpr, String varName, Expr initValue) {
        this.typeExpr = typeExpr;
        this.varName = varName;
        this.initValue = initValue;
    }
}
