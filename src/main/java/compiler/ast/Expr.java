package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 表达式
 */
public class Expr extends Node implements Generator, Checker {
    @Override
    public Type check(TypeChecker checker) {
        return null;
    }

    @Override
    public void generate(IRGenerator irGenerator) {

    }
}
