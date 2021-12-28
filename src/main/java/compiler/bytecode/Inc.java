

package compiler.bytecode;

/**
 * 自增长
 */
public class Inc extends ByteCode {
    /**
     * 变量的名称
     */
    public final String var;

    public Inc(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return  "inc: " + var;
    }
}
