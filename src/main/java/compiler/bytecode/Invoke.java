

package compiler.bytecode;

/**
    函数调用
 **/

public class Invoke extends ByteCode {
    String type;
    String funcName;

    public Invoke(String type,String funcName){

        this.funcName = funcName;
        this.type = type;
    }
    @Override
    public String toString() {
        return "invoke " + type+"."+funcName;
    }
}
