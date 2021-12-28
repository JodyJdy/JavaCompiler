package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

import java.util.List;

/**
 * 多个变量声明
 */
public class MultiLocalVarDeclare extends Expr implements Generator, Checker {
    private final List<LocalVarDeclare> varDeclareList;

    public MultiLocalVarDeclare(List<LocalVarDeclare> varDeclareList) {
        this.varDeclareList = varDeclareList;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    public List<LocalVarDeclare> getVarDeclareList() {
        return varDeclareList;
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return null;
    }
}
