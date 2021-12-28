package compiler.bytecode;

/**
 * 复制栈顶两个指令
 */
public class Dup2 extends ByteCode {
    @Override
    public String toString() {
        return "dup2";
    }
}
