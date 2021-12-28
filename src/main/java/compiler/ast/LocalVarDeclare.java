package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 局部变量声明
 */
public class LocalVarDeclare extends Expr implements Generator, Checker {
    /**
     * 类型声明
     */
    private final Type typeExpr;
    /**
     * 变量名
     */
    private final String varName;
    /**
     * 如果声明带有初始值
     */
    private final Expr initValue;

    public LocalVarDeclare(Type typeExpr, String varName, Expr initValue) {
        this.typeExpr = typeExpr;
        this.varName = varName;
        this.initValue = initValue;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    public String getVarName() {
        return varName;
    }

    public Type getTypeExpr() {
        return typeExpr;
    }

    public Expr getInitValue() {
        return initValue;
    }

    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }
}
