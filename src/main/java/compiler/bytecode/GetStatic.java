

package compiler.bytecode;

/**
    取静态字段
 **/

public class GetStatic extends ByteCode {
    private String type;
    private String field;

    public GetStatic(String type, String field) {
        this.type = type;
        this.field = field;
    }

    @Override
    public String toString() {
        return "getstatic "+type+"."+field;
    }
}
