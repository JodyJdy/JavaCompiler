

package compiler.bytecode;

import java.util.concurrent.atomic.AtomicInteger;

/**
    标签
 **/

public class Label extends ByteCode {
    private static AtomicInteger num = new AtomicInteger(0);
    public final int label;
    public Label(){
        this.label = num.getAndIncrement();
    }

    @Override
    public String toString() {
        return "label:" + label;
    }
}
