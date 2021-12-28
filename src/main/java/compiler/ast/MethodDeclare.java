package compiler.ast;

import compiler.bytecode.ByteCode;
import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.enums.Tag;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

import java.util.List;

/**
 *方法声明，包含定义
 */
public class MethodDeclare extends Node implements Generator, Checker {
    /**
     * 函数名称
     */
    private final String funcName;
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

    public MethodDeclare(String funcName, Type returnType, Tag access, boolean isStatic, VirtualArgs virtualArgs, Stmt stmt) {
        this.funcName = funcName;
        this.returnType = returnType;
        this.access = access;
        this.isStatic = isStatic;
        this.virtualArgs = virtualArgs;
        this.stmt = stmt;
    }

    /**
     * 方法对应的字节码
     */
    private List<ByteCode> byteCodes;

    public String getFuncName() {
        return funcName;
    }

    Type getReturnType() {
        return returnType;
    }

    Tag getAccess() {
        return access;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public VirtualArgs getVirtualArgs() {
        return virtualArgs;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return null;
    }

    public List<ByteCode> getByteCodes() {
        return byteCodes;
    }

    public void setByteCodes(List<ByteCode> byteCodes) {
        this.byteCodes = byteCodes;
    }
}
