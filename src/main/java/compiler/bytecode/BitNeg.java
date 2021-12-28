package compiler.bytecode;

/**
 * 将数字进行位取反，在放到栈里面去
 */
public class BitNeg extends ByteCode {
    @Override
    public String toString() {
        return "bitneg";
    }
}
