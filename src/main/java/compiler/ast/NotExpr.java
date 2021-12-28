

package compiler.ast;

import compiler.bytecode.Label;
import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

/**
 * 非 表达式
 */
public class NotExpr extends LogicExpr implements Generator, Checker {
    public NotExpr(Expr expr1) {
        super(expr1,null, null);
    }

    @Override
    public void generator(IRGenerator irGenerator, Label trueLabel, Label falseLabel) {
        super.generator(irGenerator, trueLabel, falseLabel);
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return super.check(checker);
    }
}
