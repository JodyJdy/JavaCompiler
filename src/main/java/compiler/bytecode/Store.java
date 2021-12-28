

package compiler.bytecode;

/**
 * 将栈顶的内容存储到变量中
 */
public class Store extends ByteCode {
    private final int index;

    public Store(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "store "+index;
    }
}
