

package compiler.bytecode;

/**
 * 无条件跳转
 **/

public class Goto extends ByteCode {
    protected final Label label;

    public Goto(Label label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "goto " + label.toString();
    }
}
