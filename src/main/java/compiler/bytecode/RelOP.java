

package compiler.bytecode;

import compiler.enums.Tag;

/**
 关系运算符
 **/

public class RelOP {
    private static class Gt extends Goto{
        Gt(Label label) {
            super(label);
        }

        @Override
        public String toString() {
            return "Gt " + super.label;
        }
    }
    private static class Ge extends Goto{
        Ge(Label label) {
            super(label);
        }

        @Override
        public String toString() {
            return "Ge " + super.label;
        }
    }
    private static class Li extends Goto{
        Li(Label label) {
            super(label);
        }

        @Override
        public String toString() {
            return "Li " + super.label;
        }
    }
    private static class Le extends Goto{
        Le(Label label) {
            super(label);
        }

        @Override
        public String toString() {
            return "Le " + super.label;
        }
    }
    private static class Eq extends Goto{
        Eq(Label label) {
            super(label);
        }

        @Override
        public String toString() {
            return "Eq " + super.label;
        }
    }
    private static class Ne extends Goto{
        Ne(Label label) {
            super(label);
        }

        @Override
        public String toString() {
            return "Ne " + super.label;
        }
    }

    public static ByteCode byteCode(Tag tag, Label label){
        switch (tag){
            case GT:return new Gt(label);
            case GE:return new Ge(label);
            case LI:return new Li(label);
            case LE:return new Le(label);
            case EQ:return new Eq(label);
            case NE:return new Ne(label);
            default:return null;
        }
    }
}
