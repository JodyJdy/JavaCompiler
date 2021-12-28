package compiler.bytecode;

/**
 * 栈顶的数字变成相反数，再放到栈里面去
 */
public class Neg extends ByteCode {
    @Override
    public String toString() {
        return "neg";
    }
}
