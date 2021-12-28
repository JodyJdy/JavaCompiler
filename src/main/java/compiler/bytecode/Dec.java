

package compiler.bytecode;

/**
 * 自减
 */
public class Dec extends ByteCode {
    /**
     * 变量的下标
     */
    private final String var;

    public Dec(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return  "dec: " + var;
    }
}
