package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

import java.util.List;

/**
 * 创建数组
 */
public class NewArrayExpr extends Expr implements Generator, Checker {
    /**
     * 类
     */
    private String type;
    /**
     * 基本数据类型
     */
    private Tag tag;
    /**
     * 数组创建的参数
     */
    private List<Expr> array;

    public NewArrayExpr(String type, List<Expr> array) {
        this.type = type;
        this.array = array;
    }

    public NewArrayExpr(Tag tag, List<Expr> array) {
        this.tag = tag;
        this.array = array;
    }

    public String getType() {
        return type;
    }

    public Tag getTag() {
        return tag;
    }

    public List<Expr> getArray() {
        return array;
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
}
