

package compiler.bytecode;

/**
    加载一个变量到栈里面
 **/

public class Load extends ByteCode {
    private final int index;

    public Load(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "load "+index;
    }
}
