package compiler.ast;

/**
 * 函数形参
 */
public class VirtualArg {
    /**
     * 参数类型
     */
    private final Type type;
    /**
     * 参数名称
     */
    private final String var;

    public VirtualArg(Type type, String var) {
        this.type = type;
        this.var = var;
    }

    public Type getType() {
        return type;
    }

    public String getVar() {
        return var;
    }
}
