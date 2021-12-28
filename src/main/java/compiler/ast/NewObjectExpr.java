package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 创建对象 new A();
 */
public class NewObjectExpr extends Expr  implements Generator, Checker {
    /**
     * 对象类型
     */
    private final String type;
    /**
     * 构造函数参数
     */
    private final Args args;

    public NewObjectExpr(String type, Args args) {
        this.type = type;
        this.args = args;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    public String getType() {
        return type;
    }

    public Args getArgs() {
        return args;
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return null;
    }
}
