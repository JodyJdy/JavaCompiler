

package compiler.bytecode;

/**
    设置静态字段的值
 **/

public class PutStatic extends ByteCode {
    private final  String var;
    private final String type;

    public PutStatic(String type,String var) {
        this.var = var;
        this.type = type;
    }

    @Override
    public String toString() {
        return "putstatic "+type+"."+var;
    }
}
