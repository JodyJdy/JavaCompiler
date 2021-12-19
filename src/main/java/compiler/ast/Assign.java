package compiler.ast;

public class Assign extends Stmt {
    /**
     * 表达式左边
     */
    private final Expr left;
    /**
     * 赋值操纵， >=,<=
     */
    private final Expr right;

    public Assign(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }
}
