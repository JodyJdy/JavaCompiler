package compiler.ast;

/**
 * 代码块，类的代码块
 */
public class Block extends    Node {
    private final boolean isStatic;
    private final Stmt stmt;

    public Block(Stmt stmt,boolean isStatic) {
        this.stmt = stmt;
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public Stmt getStmt() {
        return stmt;
    }
}
