

package compiler.ast;

import compiler.bytecode.Label;
import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;


/**
 * 布尔常量
 */
public class BoolContantExpr extends LogicExpr implements Generator, Checker {
    public BoolContantExpr(Tag tag) {
        super(null, null, tag);
    }

    @Override
    public void generator(IRGenerator irGenerator, Label trueLabel, Label falseLabel) {
        irGenerator.visit(this,trueLabel,falseLabel);
    }


    @Override
    public Type check(TypeChecker checker) { checker.visit(this);
        return null;
    }
}
