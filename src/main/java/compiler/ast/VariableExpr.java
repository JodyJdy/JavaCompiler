package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 变量表达式
 */
public class VariableExpr extends Expr implements Generator, Checker {
    private String var;
    public VariableExpr(String var){
        this.var = var;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return super.check(checker);
    }

    public String getVar() {
        return var;
    }
}
