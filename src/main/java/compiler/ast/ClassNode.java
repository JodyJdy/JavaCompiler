package compiler.ast;

import compiler.enums.Tag;
import compiler.parser.Type;

import java.util.List;

/**
 * 类节点
 */
public class ClassNode {
    /**
     * 访问修饰符
     */
    private Tag access = Tag.DEFATULT;
    /**
     * 类名
     */
    private String name;
    /**
     * 父类， 如果有继承的话
     */
    private Type extendsC;
    /**
     * 父类接口，如果有 实现的话
     */
    private List<Type> impls;

    /**
     * 类的内容
     */
    private ClassBody classBody;

    public ClassNode() {
    }

    public Tag getAccess() {
        return access;
    }

    public void setAccess(Tag access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getExtendsC() {
        return extendsC;
    }

    public void setExtendsC(Type extendsC) {
        this.extendsC = extendsC;
    }

    public List<Type> getImpls() {
        return impls;
    }

    public void setImpls(List<Type> impls) {
        this.impls = impls;
    }

    public ClassBody getClassBody() {
        return classBody;
    }

    public void setClassBody(ClassBody classBody) {
        this.classBody = classBody;
    }
}
