

package compiler.bytecode;

/**
 取实例字段
 **/

public class GetField extends ByteCode {
    private String type;
    private String field;

    public GetField(String type,String field){
        this.type = type;
        this.field = field;
    }

    @Override
    public String toString() {
        return "getfield "+type+"."+field;
    }
}
