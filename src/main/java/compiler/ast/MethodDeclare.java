package compiler.ast;

import compiler.enums.Tag;
import compiler.parser.Type;

public class MethodDeclare extends Node {
    /**
     * 返回类型
     */
    private final Type returnType;
    /**
     * 访问权限
     */
    private final Tag access;
    /**
     * 是否是静态的
     */
    private final boolean isStatic;
    /**
     * 函数形参
     */
    private final VirtualArgs virtualArgs;

    /**
     * 代码块
     */
    private final Stmt stmt;

    public MethodDeclare(Type returnType, Tag access, boolean isStatic, VirtualArgs virtualArgs, Stmt stmt) {
        this.returnType = returnType;
        this.access = access;
        this.isStatic = isStatic;
        this.virtualArgs = virtualArgs;
        this.stmt = stmt;
    }
}
