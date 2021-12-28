package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

import java.util.List;

/**
 * 包含后置处理的表达式
 */
public class PostfixExpr extends Expr implements Generator, Checker {
    private Expr primary;
    /**
     * 运算符， 后置的 ++,--
     */
    private Tag tag;
    /**
     * 数组使用 a[expr][expr][expr], primary是数组名称
     */
    private List<Expr> array;
    /**
     * 函数调用 此时的 primary是函数名
     */
    private Args args;
    /**
     * . 运算
     */
    private Expr point;


    public PostfixExpr(Expr primary) {
        this.primary = primary;
    }
    public PostfixExpr(Expr primary, Tag tag){
        this(primary);
        this.tag = tag;
    }
    public PostfixExpr(Expr primary, List<Expr> array){
        this(primary);
        this.array = array;
    }
    public PostfixExpr(Expr primary, Args args){
        this(primary);
        this.args = args;
    }
    public PostfixExpr(Expr primary, Expr point){
        this(primary);
        this.point = point;
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

    public Expr getPrimary() {
        return primary;
    }

    public Tag getTag() {
        return tag;
    }

    public List<Expr> getArray() {
        return array;
    }

    public Args getArgs() {
        return args;
    }

    public Expr getPoint() {
        return point;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
