package compiler.bytecode;

/**
    弹出栈顶的一个操作数
 **/

public class Pop extends ByteCode {

    @Override
    public String toString() {
        return "pop";
    }
}
