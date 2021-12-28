package compiler.ast;

import compiler.enums.Tag;

/**
 * 类字段声明
 */
public class FieldDeclare extends Node {
    /**
     * Tag 访问权限
     */
    private final Tag access;
    /**
     * 类型声明
     */
    private final Type typeExpr;
    /**
     * 变量名
     */
    private final String varName;
    /**
     * 如果声明带有初始值
     */
    private final Expr initValue;
    /**
     * 是否是静态的
     */
    private final boolean isStatic;

    public FieldDeclare(Tag access, Type typeExpr, String varName, Expr initValue, boolean isStatic) {
        this.access = access;
        this.typeExpr = typeExpr;
        this.varName = varName;
        this.initValue = initValue;
        this.isStatic = isStatic;
    }

    public Tag getAccess() {
        return access;
    }

    public Type getTypeExpr() {
        return typeExpr;
    }

    public String getVarName() {
        return varName;
    }

    public Expr getInitValue() {
        return initValue;
    }

    public boolean isStatic() {
        return isStatic;
    }
}
