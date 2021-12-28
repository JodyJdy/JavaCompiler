package compiler.ast;

import compiler.enums.Tag;
import compiler.ir.Method;
import compiler.parser.ClassesLoader;

import java.util.*;

/**
 * 类节点
 */
public class ClassNode {

    /**
     * 文件类型，默认的是 Class，可以是Interface，Enum的形式
     */
    private Tag fileType = Tag.CLASS;

    /**
     * 包名
     */
    private String packageName = "default";

    /**
     * import语句
     */
    private Set<String> imports = new HashSet<>(2);

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

    /**
     *  实例代码块
     */
    private MethodDeclare instanceMethod;

    /**
     * 静态代码块
     */
    private MethodDeclare staticMethod;

    /**
     * 实例变量
     */
    private Map<String,Type> instanceFields = new HashMap<>();

    /**
     * 静态变量
     */
    private Map<String,FieldDeclare> staticFields = new HashMap<>();

    /**
     * 初始化方法信息
     */
    private Set<Method> methods = new HashSet<>();

    /**
     * 根据import语句生成类中需要的所有信息
     */
    private Set<String> importedClass = new HashSet<>();



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
    public Type getType(){
        return new Type(packageName+"."+name);
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


    public void initClass(){
        initFields();
        createInitMethod();
        initMethods();
    }

    /**
     * 初始化变量信息
     */
    private void initFields(){
        List<MultiFieldDeclare> multiFieldDeclareList = this.getClassBody().getMultiFieldDeclares();
        for(MultiFieldDeclare multiFieldDeclare : multiFieldDeclareList){
            for(FieldDeclare field : multiFieldDeclare.getFieldDeclareList()){
                if(field.isStatic()){
                    staticFields.put(field.getVarName(),field);
                } else{
                    instanceFields.put(field.getVarName(),field.getTypeExpr());
                }
            }
        }
    }
    /**
     * 创建初始化方法
     */
    private void createInitMethod(){
        List<Stmt> instances = new ArrayList<>();
        List<Stmt> statics = new ArrayList<>();
        //将带有变量初始化的语句进行加入
        for(MultiFieldDeclare multiFieldDeclare : this.getClassBody().getMultiFieldDeclares()){
            for(FieldDeclare field : multiFieldDeclare.getFieldDeclareList()){
                if(field.getInitValue() != null){
                    Assign assign = new Assign(new VariableExpr(field.getVarName()),field.getInitValue());
                    if(field.isStatic()){
                        statics.add(new ExprStmt(assign));
                    }else {
                        instances.add(new ExprStmt(assign));
                    }
                }
            }
        }
        for(Block block : this.getClassBody().getBlocks()){
            if(block.isStatic()){
                statics.add(block.getStmt());
            } else{
                instances.add(block.getStmt());
            }
        }
        this.instanceMethod = new MethodDeclare("init",null,Tag.PUBLIC,false,new VirtualArgs(),SeqStmt.transToStmt(instances));
        this.staticMethod = new MethodDeclare("cinit",null,Tag.PUBLIC,true,new VirtualArgs(),SeqStmt.transToStmt(statics));
    }
    /**
     * 收集方法信息
     */
    private void initMethods(){
        for(MethodDeclare methodDeclare : classBody.getMethodDeclares()){
            methods.add(new Method(methodDeclare.getFuncName(),methodDeclare.getReturnType(),methodDeclare.getAccess()
            ,methodDeclare.isStatic(),methodDeclare.getVirtualArgs()));
        }
    }

    /**
     * 收集导入类的信息
     */
    public void initImportedClasses(){
        for(String impo : imports){
            if(ClassesLoader.loadedClass(impo)){
                importedClass.add(impo);
            } else if(impo.endsWith("*")){
                importedClass.addAll(ClassesLoader.listClasses(impo));
            } else{
                System.err.println("错误的import信息，类不存在");
            }
        }
    }




    public Map<String, Type> getInstanceFields() {
        return instanceFields;
    }

    public void setInstanceFields(Map<String, Type> instanceFields) {
        this.instanceFields = instanceFields;
    }

    public Map<String, FieldDeclare> getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(Map<String, FieldDeclare> staticFields) {
        this.staticFields = staticFields;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
    }

    public Method getMethod(String funcName){
        for(Method m : methods){
            if(m.getFuncName().equals(funcName)){
                return m;
            }
        }
        return null;
    }
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }

    public Set<String> getImportedClass() {
        return importedClass;
    }

    public Tag getFileType() {
        return fileType;
    }

    public void setFileType(Tag fileType) {
        this.fileType = fileType;
    }

    public MethodDeclare getInstanceMethod() {
        return instanceMethod;
    }

    public MethodDeclare getStaticMethod() {
        return staticMethod;
    }
}
