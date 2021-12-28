

package compiler.ir;

import compiler.ast.VirtualArgs;
import compiler.enums.Tag;
import compiler.ast.Type;

import java.util.Objects;

/**
 *
 * 记录方法信息
 **/

public class Method {
    /**
     * 函数名称
     */
    private  String funcName;
    /**
     * 返回类型
     */
    private  Type returnType;
    /**
     * 访问权限
     */
    private  Tag access;
    /**
     * 是否是静态的
     */
    private  boolean isStatic;
    /**
     * 函数形参
     */
    private  VirtualArgs virtualArgs;


    public Method(String funcName,Type returnType, Tag access, boolean isStatic, VirtualArgs virtualArgs) {
        this.funcName = funcName;
        this.returnType = returnType;
        this.access = access;
        this.isStatic = isStatic;
        this.virtualArgs = virtualArgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Method)) {
            return false;
        }
        Method method = (Method) o;
        return isStatic == method.isStatic &&
                Objects.equals(funcName, method.funcName) &&
                access == method.access &&
                Objects.equals(virtualArgs, method.virtualArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcName, access, isStatic, virtualArgs);
    }

    public String getFuncName() {
        return funcName;
    }

    Type getReturnType() {
        return returnType;
    }

    public Tag getAccess() {
        return access;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public VirtualArgs getVirtualArgs() {
        return virtualArgs;
    }
}
