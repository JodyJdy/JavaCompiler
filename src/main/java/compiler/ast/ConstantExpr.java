package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;
import compiler.lexer.Token;

/**
 * 常量表达式
 */
public class ConstantExpr extends Expr implements Generator, Checker {
    public Token token;
    public ConstantExpr(Token token){
        this.token = token;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }
}
