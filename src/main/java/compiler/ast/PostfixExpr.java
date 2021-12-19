package compiler.ast;

import compiler.enums.Tag;

import java.util.List;

public class PostfixExpr extends Expr {
    private Expr primary;
    //运算符
    private Tag tag;
    //数组使用 a[expr][expr][expr]
    private List<Expr> array;
    //函数调用
    private Args args;
    //. 运算
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

}
