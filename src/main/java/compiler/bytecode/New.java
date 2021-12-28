

package compiler.bytecode;

/**
    new
 **/

public class New extends ByteCode {
    private final String type;

    public New(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "new " + type;
    }
}
