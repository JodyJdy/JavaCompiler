package compiler.ast;

/**
 * 代码块
 */
public class Block extends  Node {
    private final boolean isStatic;
    private final Stmt stmt;

    public Block(Stmt stmt,boolean isStatic) {
        this.stmt = stmt;
        this.isStatic = isStatic;
    }
}
