package compiler.ast;

import compiler.enums.Tag;

import java.util.List;

/**
 * 创建数组
 */
public class NewArrayExpr extends Expr {
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
}
