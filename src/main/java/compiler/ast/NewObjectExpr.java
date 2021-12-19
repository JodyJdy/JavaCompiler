package compiler.ast;

/**
 * 创建对象 new A();
 */
public class NewObjectExpr extends Expr {
    /**
     * 对象类型
     */
    private final String type;
    /**
     * 构造函数参数
     */
    private final Args args;

    public NewObjectExpr(String type, Args args) {
        this.type = type;
        this.args = args;
    }
}
