

package compiler.bytecode;

import compiler.enums.Tag;

/**
    二元操作符指令
 **/

public class BinaryOp {
    private static class Add extends ByteCode{
        @Override
        public String toString() {
            return "add";
        }
    }
    private static class Sub extends ByteCode{
        @Override
        public String toString() {
            return "sub";
        }
    }
    private static class Multi extends ByteCode{
        @Override
        public String toString() {
            return "mul";
        }
    }
    private static class Div extends ByteCode{
        @Override
        public String toString() {
            return "div";
        }
    }
    private static class Mod extends ByteCode{
        @Override
        public String toString() {
            return "mod";
        }
    }
    private static class And extends ByteCode{
        @Override
        public String toString() {
            return "and";
        }
    }
    private static class Or extends ByteCode{
        @Override
        public String toString() {
            return "or";
        }
    }
    private static class Xor extends ByteCode{
        @Override
        public String toString() {
            return "xor";
        }
    }
    private static class Lshift extends ByteCode{
        @Override
        public String toString() {
            return "lshift";
        }
    }
    private static class Rshift extends ByteCode{
        @Override
        public String toString() {
            return "rshfit";
        }
    }

    private static Add add = new Add();
    private static Sub sub = new Sub();
    private static Multi multi = new Multi();
    private static Div div = new Div();
    private static Mod mod = new Mod();
    private static And and = new And();
    private static Or or = new Or();
    private static Xor xor = new Xor();
    private static Lshift lshift = new Lshift();
    private static Rshift rshift = new Rshift();

    public static ByteCode byteCode(Tag tag){
        switch (tag){
            case ADD:return add;
            case SUB:return sub;
            case MUL:return multi;
            case DIV:return div;
            case MOD:return mod;
            case BITAND:return and;
            case BITOR:return or;
            case XOR:return xor;
            case LSHIFT:return lshift;
            case RSHIFT:return rshift;
            default:return null;
        }
    }
}
