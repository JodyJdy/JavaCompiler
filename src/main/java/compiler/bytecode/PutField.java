

package compiler.bytecode;

/**
 设置实例字段的值
 **/

public class  PutField extends ByteCode {
    private final String type;
    private final String var;


    public PutField(String type,String var) {
        this.var = var;
        this.type = type;
    }

    @Override
    public String toString() {
        return "putfield "+type+"."+var;
    }
}
