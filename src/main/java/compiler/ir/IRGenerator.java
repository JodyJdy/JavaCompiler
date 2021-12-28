

package compiler.ir;

import compiler.ast.*;
import compiler.bytecode.*;
import compiler.enums.Tag;
import compiler.lexer.Num;
import compiler.ast.ArrayType;
import compiler.parser.ClassesLoader;
import compiler.parser.PackageLoader;
import compiler.ast.Type;
import compiler.util.ParserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 生成中间代码，一个IRGenerator可以生成一个类的字节码
 */
public class IRGenerator {

    private final ClassNode classNode;
    /**
     * 记录函数的字节码
     */
    private List<ByteCode> methodCodes;
    /**
     * 变量表
     */
    private Env env = new Env(0);

    /**
     * Break的栈
     */
    private Stack<Label> breakLabel = new Stack<>();
    /**
     * Continue的栈
     */
    private Stack<Label> continueLabel = new Stack<>();
    /**
     * 用来标识执行的 表达式是作为语句还是单纯的表达式
     */
    private int exprLevel = 0;

    private boolean isStatement(){
        return exprLevel == 0;
    }

    /**
     * 作为表达式执行
     */
    private void transToExpr(Expr expr){
        exprLevel++;
        expr.generate(this);
        exprLevel--;
    }
    private void transToExpr(LogicExpr logicExpr){
        Label trueLabel = new Label();
        Label falseLabel = new Label();
        exprLevel++;
        logicExpr.generator(this,trueLabel,falseLabel);
        exprLevel--;
        Label endLabel = new Label();
        boolNeedStore(trueLabel,falseLabel,endLabel);

    }

    private void transToStmt(LogicExpr logicExpr,Label trueLabel,Label falseLabel){
        logicExpr.generator(this,trueLabel,falseLabel);
    }

    /**
     * 表达式作为语句执行
     *
     */
    private void transToStmt(Expr expr){
        expr.generate(this);
    }


    public IRGenerator(ClassNode classNode) {
        this.classNode = classNode;
    }


    /**
     * 执行赋值表达式右边的内容
     */
    private void assignRightExecute(Expr expr){
        if(expr instanceof LogicExpr){
            transToExpr((LogicExpr)expr);
        } else{
            transToExpr(expr);
        }
        //赋值语句作为表达式执行，需要保留值,@todo 无效
        if(!isStatement()){
            methodCodes.add(new Dup());
        }
    }

    /**
     * 加载数组使用字段
     * @param searchStack 是否操作数栈
     */
    private Type loadArrayUseField(PostfixExpr postfixExpr,String type,boolean searchStack){
        Type type1 = null;
        ClassNode temp = ClassesLoader.getClass(type);
        VariableExpr arrayName = (VariableExpr) postfixExpr.getPrimary();
        if(searchStack && env.hasVar(arrayName.getVar())){
            type1 = env.getVar(arrayName.getVar());
            methodCodes.add(new Load(env.varIndex(arrayName.getVar())));
        } else if(temp.getInstanceFields().containsKey(arrayName.getVar())){
            type1 = temp.getInstanceFields().get(arrayName.getVar());
            methodCodes.add(new GetField(type,arrayName.getVar()));
        } else if(temp.getStaticFields().containsKey(arrayName.getVar())){
            type1 = temp.getStaticFields().get(arrayName.getVar()).getTypeExpr();
            methodCodes.add(new GetStatic(type,arrayName.getVar()));
        }
        for(Expr expr : postfixExpr.getArray()){
            transToExpr(expr);
            methodCodes.add(new Aload());
        }
        return type1;
    }

    /**
     * 先不考虑函数重载类型检查
     */
    private Type loadFuncUse(PostfixExpr postfixExpr,String fromType){
        VariableExpr variableExpr = (VariableExpr)postfixExpr.getPrimary();
        String funcName = variableExpr.getVar();
        Args args = postfixExpr.getArgs();
        for(Expr expr : args.getExprs()){
            transToExpr(expr);
        }
        methodCodes.add(new Invoke(fromType,funcName));
        ClassNode classNode1 = ClassesLoader.getClass(fromType);
        Method method = classNode1.getMethod(funcName);
        if(isStatement() && method.getReturnType().getTag() != Tag.VOID){
            methodCodes.add(new Pop());
        }
        return method.getReturnType();
    }

    /**
     * 赋值表达式 左边时数组时
     */
    private void leftIsArray(PostfixExpr postfixExpr,Expr right,String preType,boolean searchStack){
        ClassNode classNode1 = ClassesLoader.getClass(preType);
        String arrayFieldName =  ((VariableExpr)(postfixExpr).getPrimary()).getVar();
        FieldDeclare fieldDeclare = classNode1.getStaticFields().get(arrayFieldName);
        if(searchStack && env.hasVar(arrayFieldName)){
            methodCodes.add(new Load(env.varIndex(arrayFieldName)));
        } else if(fieldDeclare != null){
            methodCodes.add(new GetStatic(preType,arrayFieldName));
        }else{
            methodCodes.add(new GetField(preType,arrayFieldName));
        }
        //加载数组，最后一个维度的数组不用Aload，因为后面要执行astore
        for(int i=0;i<postfixExpr.getArray().size();i++){
            transToExpr(postfixExpr.getArray().get(i));
            if(i!=postfixExpr.getArray().size() - 1){
                methodCodes.add(new Aload());
            }
        }
        //获取结果
        assignRightExecute(right);
        methodCodes.add(new Astore());
    }

    /**
     * 赋值表达式左边时单变量时
     */
    private void leftIsSingleVar( VariableExpr variableExpr,Expr right){
        if(env.hasVar(variableExpr.getVar())){
            assignRightExecute(right);
            methodCodes.add(new Store(env.varIndex(variableExpr.getVar())));
        } else if(classNode.getInstanceFields().containsKey(variableExpr.getVar())){
            methodCodes.add(new Load(env.varIndex(Tag.THIS.getStr())));
            assignRightExecute(right);
            methodCodes.add(new PutField(classNode.getType().getType(),variableExpr.getVar()));
        } else if(classNode.getStaticFields().containsKey(variableExpr.getVar())){
            assignRightExecute(right);
            methodCodes.add(new PutStatic(classNode.getType().getType(),variableExpr.getVar()));
        }
    }

    public void visit(Assign assign){
        Expr right = assign.getRight();
        // a=x 这种赋值
        if(assign.getLeft() instanceof VariableExpr) {
            VariableExpr variableExpr = (VariableExpr)assign.getLeft();
            leftIsSingleVar(variableExpr,right);
            return;
        }


        //a.b.c[x] =xx的复杂情况的赋值
        List<Expr> list = postfixExprToList((PostfixExpr)assign.getLeft());
        //如果size==1，一定是数组赋值 a[]=x,函数调用不允许在左边出现
        if(list.size() == 1){
            PostfixExpr postfixExpr = (PostfixExpr)list.get(0);
            leftIsArray(postfixExpr,right,classNode.getName(),true);
            return;
        }
        Type preType = new Type(classNode.getName());
        for(int i=0;i<list.size() - 1;i++){
            if(preType == null){
                break;
            }
            preType = postfixExprExecute(list.get(i),preType,i == 0, i ==0 ? null : list.get(i-1));
        }
        //赋值
        Expr last = list.get(list.size() - 1);
        if(preType == null){
            return;
        }
        ClassNode classNode1 = ClassesLoader.getClass(preType.getType());
        if(last instanceof VariableExpr){
            assignRightExecute(right);
            String field = ((VariableExpr)last).getVar();
            //先尝试取静态变量
            FieldDeclare fieldDeclare = classNode1.getStaticFields().get(field);
            if(fieldDeclare != null) {
                methodCodes.add(new PutStatic(preType.getType(), field));
            } else{
                methodCodes.add(new PutField(preType.getType(),field));
            }
        } else{
            PostfixExpr p = (PostfixExpr)last;
            leftIsArray(p,right,classNode.getName(),true);
        }
    }
    public void visit(IfStmt ifStmt){
        Label thenLabel = new Label();
        Label elseLabel = new Label();
        Label endLabel = new Label();
        LogicExpr logicExpr = (LogicExpr) ifStmt.getCondition();
        //无else
        if(ifStmt.getElseBody() == null){
            transToStmt(logicExpr,thenLabel,endLabel);
            methodCodes.add(thenLabel);
            ifStmt.getThenBody().generate(this);
        //带有else
        } else{
            transToStmt(logicExpr,thenLabel,elseLabel);
            methodCodes.add(thenLabel);
            ifStmt.getThenBody().generate(this);
            methodCodes.add(new Goto(endLabel));
            methodCodes.add(elseLabel);
            ifStmt.getElseBody().generate(this);
        }
        methodCodes.add(endLabel);
    }
    public void visit(Args args){
        for(Expr expr : args.getExprs()){
            if(expr instanceof LogicExpr){
                transToExpr((LogicExpr)expr);
            }else {
                transToExpr(expr);
            }
        }
    }
    public void visit(BinaryExpr bynary){
        Expr expr1 = bynary.getExpr1();
        Expr expr2 = bynary.getExpr2();
        expr1.generate(this);
        expr2.generate(this);
        methodCodes.add(BinaryOp.byteCode(bynary.getTag()));
    }
    public void visit(BlockStmt blockStmt){
        //需要考虑到作用域
        Env newEnv = new Env(env.getPreVar());
        newEnv.setPrev(env);
        env = newEnv;
        blockStmt.getStmt().generate(this);
        //进行还原
        env = env.getPrev();
    }
    public void visit(BreakStmt breakStmt){
        methodCodes.add(new Goto(breakLabel.peek()));
    }
    public void visit(ChoseExpr choseExpr){
        Label trueLabel = new Label();
        Label falseLabel = new Label();
        Label endLabel = new Label();
        transToStmt((LogicExpr)choseExpr.getCondition(),trueLabel,falseLabel);
        methodCodes.add(trueLabel);
        if(isStatement()){
            transToStmt(choseExpr.getTrueExpr());
        } else{
            transToExpr(choseExpr.getTrueExpr());
        }
        methodCodes.add(new Goto(endLabel));
        methodCodes.add(falseLabel);
        if(isStatement()){
            transToStmt(choseExpr.getFalseExpr());
        }else{
            transToExpr(choseExpr.getFalseExpr());
        }
        methodCodes.add(endLabel);
    }
    public void visit(ConstantExpr constantExpr){
        methodCodes.add(new Constant(constantExpr.token));
    }
    public void visit(ContinueStmt continueStmt){
        methodCodes.add(new Goto(continueLabel.peek()));
    }
    public void visit(DoStmt doStmt){
        Label beginLabel = new Label();
        Label endLabel = new Label();
        methodCodes.add(beginLabel);
        continueLabel.push(beginLabel);
        breakLabel.push(endLabel);
        doStmt.getDoBody().generate(this);
        continueLabel.pop();
        breakLabel.pop();
        LogicExpr expr = (LogicExpr)doStmt.getCondition();
        transToStmt(expr,beginLabel,endLabel);
        methodCodes.add(endLabel);
    }
    public void visit(Expr expr){
        transToExpr(expr);
    }
    public void visit(ForStmt forStmt){
        Label beginLabel  = new Label();
        Label bodyLabel  = new Label();
        Label endLabel = new Label();
        forStmt.getAssign().generate(this);
        methodCodes.add(beginLabel);
        LogicExpr logicExpr = (LogicExpr)forStmt.getCondi();
        transToStmt(logicExpr,bodyLabel,endLabel);
        methodCodes.add(bodyLabel);
        continueLabel.push(beginLabel);
        breakLabel.push(endLabel);
        forStmt.getForBody().generate(this);
        forStmt.getAssign2().generate(this);
        continueLabel.pop();
        breakLabel.pop();
        methodCodes.add(new Goto(beginLabel));
        methodCodes.add(endLabel);
    }

    /**
     * 变量声明
     */
    public void visit(LocalVarDeclare localVarDeclare){
        if(env.hasVar(localVarDeclare.getVarName())){
            System.err.println("变量重复定义");
        }
        env.addVar(localVarDeclare.getVarName(),localVarDeclare.getTypeExpr());
        if(localVarDeclare.getInitValue() != null){
            if(localVarDeclare.getInitValue() instanceof LogicExpr){
               transToExpr((LogicExpr) localVarDeclare.getInitValue());
            } else{
                transToExpr(localVarDeclare.getInitValue());
            }
            //存储变量内容
            int index = env.varIndex(localVarDeclare.getVarName());
            methodCodes.add(new Store(index));
        }


    }
    public void visit(MultiLocalVarDeclare multiLocalVarDeclare){
        for(LocalVarDeclare localVarDeclare : multiLocalVarDeclare.getVarDeclareList()){
            localVarDeclare.generate(this);
        }
    }
    public void visit(LogicExpr logicExpr, Label trueLabel , Label falseLabel){
        // &&运算代表左边为真的跳转， ||运算代表左边为false的跳转
        Label label = new Label();
        LogicExpr leftExpr = (LogicExpr) logicExpr.getExpr1();
        if(logicExpr.getTag() == Tag.AND){
            if(leftExpr != null) {
                leftExpr.generator(this,label,falseLabel);
            }
        } else{
            if(leftExpr != null) {
                leftExpr.generator(this,trueLabel,label);
            }
        }
        methodCodes.add(label);
        LogicExpr relExpr = (LogicExpr)logicExpr.getExpr2();
        if(relExpr != null) {
            relExpr.generator(this,trueLabel,falseLabel);
        }
    }
    public void visit(NewArrayExpr newArrayExpr){
        boolean isMulti = newArrayExpr.getArray().size() > 1;
        boolean isBasi = newArrayExpr.getTag() != null;
        //初始化数组创建参数
        for(Expr expr : newArrayExpr.getArray()){
             transToExpr(expr);
        }
        if(isMulti){
            if(isBasi) {
                methodCodes.add(new MultiNewArray(newArrayExpr.getTag(), newArrayExpr.getArray().size()));
            } else{
                methodCodes.add(new MultiNewArray(newArrayExpr.getType(),newArrayExpr.getArray().size()));
            }
        } else{
            if(isBasi) {
                methodCodes.add(new NewArray(newArrayExpr.getTag()));
            } else{
                methodCodes.add(new NewArray(newArrayExpr.getType()));
            }
        }
    }
    public void visit(NewObjectExpr newObjectExpr){
        methodCodes.add(new New(newObjectExpr.getType()));
        methodCodes.add(new Dup());
        //构造函数的参数
        Args args = newObjectExpr.getArgs();
        for(Expr arg : args.getExprs()){
            if(arg instanceof LogicExpr){
                transToExpr((LogicExpr)arg);
            } else {
                transToExpr(arg);
            }
        }
        methodCodes.add(new Invoke(newObjectExpr.getType(),"init"));
    }

    /**
     * 将包含链式调用的 PostfixExpr表达式转成链表
     */
    private List<Expr> postfixExprToList(PostfixExpr postfixExpr){
        List<Expr> linked = new ArrayList<>();
        PostfixExpr temp = postfixExpr;
        do {
            if(temp.getPoint() == null){
                linked.add(temp);
                break;
            }
            linked.add(temp.getPrimary());
            Expr expr = temp.getPoint();
            if(expr instanceof  VariableExpr){
                linked.add(expr);
                break;
            }
            temp = (PostfixExpr)expr;
        }while(true);
        //进行解析, 对于以包名+类名开头的表达式要特殊处理，与带.的表达式冲突
        Expr first = linked.get(0);
        if(first instanceof  VariableExpr){
            String var = ((VariableExpr)first).getVar();
            //如果名称是已经定义的变量，不进行特殊处理
            if(env.hasVar(var) ||classNode.getInstanceFields().containsKey(var) || classNode.getStaticFields().containsKey(var)){
            //考虑是包名.类名
            } else{
                //考虑是类名
                String className = ParserHelper.tranClassNameWithPackage(var,classNode);
                if(className != null) {
                    TypeExpr typeExpr = new TypeExpr(new Type(className));
                    //更新第0个元素的内容
                    linked.set(0, typeExpr);
                //考虑是 包名.类名
                }else if(PackageLoader.containsPackage(var)){
                    PackageLoader.Package p = PackageLoader.getPackage(var);
                    int i=1;
                    for(;i<linked.size();i++){
                        if(p.isEnd()){
                            break;
                        }
                        Expr e = linked.get(i);
                        if(e instanceof VariableExpr){
                            VariableExpr v = (VariableExpr)e;
                            PackageLoader.Package tempP = p.getPackage(v.getVar());
                            if(tempP == null){
                                i = -1;
                                break;
                            }else{
                                p = tempP;
                            }
                        } else{
                            i = -1;
                            break;
                        }
                    }
                    //寻找到了包名,在0-i-1范围内，且取得到第i个，将其作为类名
                    if(p.isEnd() && i != -1 && i != linked.size() && linked.get(i) instanceof VariableExpr){
                        //解析出类名
                        StringBuilder sb = new StringBuilder();
                        for(int j=0;j<i;j++){
                            sb.append(((VariableExpr)linked.get(j)).getVar()).append(".");
                        }
                        sb.append(((VariableExpr)linked.get(i++)).getVar());
                        //确认是已经加载的类，进行linked的调整
                        if(ClassesLoader.loadedClass(sb.toString())){
                            List<Expr> result = new ArrayList<>();
                            TypeExpr typeExpr = new TypeExpr(new Type(sb.toString()));
                            result.add(typeExpr);
                            for(;i < linked.size();i++){
                                result.add(linked.get(i));
                            }
                            return result;
                        }
                    }

                }
            }
        }
        return linked;
    }

    /**
     * 从fromType里面加载字段
     */
    private Type loadField(String field, Type fromType){
        ClassNode tempClass = ClassesLoader.getClass(fromType.getType());
        FieldDeclare fieldDeclare = tempClass.getStaticFields().get(field);
        if(fieldDeclare != null) {
            methodCodes.add(new GetStatic(fromType.getType(), field));
            return fieldDeclare.getTypeExpr();
            //取不到再去取实例变量
        } else{
            methodCodes.add(new GetField(fromType.getType(),field));
            return tempClass.getInstanceFields().get(field);
        }
    }

    private Type postfixExprExecute(Expr postfixExpr,Type preType,boolean isFirst,Expr preExpr){


        //取表达式开头的字段 a.b.c 中的a
        if(isFirst && postfixExpr instanceof VariableExpr) {
            VariableExpr variableExpr = (VariableExpr) postfixExpr;
            String field = variableExpr.getVar();
            Type result = env.getVar(field);
            if (result != null) {
                return result;
            }
            return loadField(field, preType);
        }
        //取非开头的字段
        else if(postfixExpr instanceof VariableExpr){
            VariableExpr variableExpr = (VariableExpr) postfixExpr;
            // 对于数组来说，a.length代表数组的长度
            if("length" .equals(variableExpr.getVar()) && preExpr instanceof PostfixExpr && preType instanceof ArrayType){
                PostfixExpr p = (PostfixExpr)preExpr;
                ArrayType a = (ArrayType)preType;
                if(p.getArray() != null && p.getArray().size() < a.getDimension()){
                    methodCodes.add(new ArrayLength());
                    return new Type(Tag.INT);
                }
            }
            return loadField(variableExpr.getVar(),preType);
        //类型
        } else if(postfixExpr instanceof TypeExpr){
            TypeExpr typeExpr = (TypeExpr)postfixExpr;
            return typeExpr.getType();
        }else{
            final PostfixExpr p = (PostfixExpr)postfixExpr;
            //数组调用
            if(p.getArray() != null && p.getTag() == null) {
                return loadArrayUseField(p, preType.getType(), isFirst);
            //函数调用
            } else if(p.getArgs() != null){
                return loadFuncUse(p,preType.getType());
            } else{
                ClassNode tempNode = ClassesLoader.getClass(preType.getType());
                VariableExpr variableExpr = (VariableExpr) p.getPrimary();
                // 0 普通变量，1 实例变量 2 静态变量 3数组赋值
                int type = 0;
                int index = 0;
                if(isFirst && env.hasVar(variableExpr.getVar())){
                    index = env.varIndex(variableExpr.getVar());
                    methodCodes.add(new Load(index));
                    type = 0;
                } else if(tempNode.getInstanceFields().containsKey(variableExpr.getVar())){
                    methodCodes.add(new GetField(tempNode.getType().toString(),variableExpr.getVar()));
                    type = 1;
                } else if(tempNode.getStaticFields().containsKey(variableExpr.getVar())){
                    methodCodes.add(new GetStatic(tempNode.getType().toString(),variableExpr.getVar()));
                    type = 2;
                }

                //数组操作的指令集比较特殊
                if(p.getArray() != null){
                    int size = p.getArray().size();
                    for(int i=0;i<size;i++){
                        transToExpr(p.getArray().get(i));
                        if(i == size - 1){
                            methodCodes.add(new Dup2());
                        }
                        methodCodes.add(new Aload());
                        if(i == size - 1 && !isStatement()){
                            methodCodes.add(new Dup_X2());
                        }
                    }
                    methodCodes.add(new Constant(new Num(1)));
                    if(Tag.ADDADD == p.getTag()) {
                        methodCodes.add(BinaryOp.byteCode(Tag.ADD));
                    }else{
                        methodCodes.add(BinaryOp.byteCode(Tag.SUB));
                    }
                    methodCodes.add(new Astore());
                    return null;
                }
                //普通变量
                if(!isStatement()){
                    methodCodes.add(new Dup());
                }
                methodCodes.add(new Constant(new Num(1)));
                if(Tag.ADDADD == p.getTag()) {
                    methodCodes.add(BinaryOp.byteCode(Tag.ADD));
                }else{
                    methodCodes.add(BinaryOp.byteCode(Tag.SUB));
                }
                if(type == 1){
                    methodCodes.add(new Store(index));
                } else if(type == 2){
                    methodCodes.add(new PutField(tempNode.getType().getType(),variableExpr.getVar()));
                } else{
                    methodCodes.add(new PutStatic(tempNode.getType().getType(),variableExpr.getVar()));
                }

                return null;
            }
        }
    }
    public void visit(PostfixExpr postfixExpr){
        List<Expr> list = postfixExprToList(postfixExpr);
        Type preType =classNode.getType();
        for(int i=0;i<list.size();i++){
            if(preType == null){
                break;
            }
            preType = postfixExprExecute(list.get(i),preType, i == 0,i==0 ? null : list.get(i-1));
        }
    }
    public void visit(RelExpr relExpr,Label trueLabel, Label falseLabel){
        Expr expr1 = relExpr.getExpr1();
        Expr expr2 = relExpr.getExpr2();
        transToStmt(expr1);
        transToStmt(expr2);
        methodCodes.add(RelOP.byteCode(relExpr.getTag(),trueLabel));
        methodCodes.add(new Goto(falseLabel));
    }
    public void visit(ReturnStmt returnStmt){
        if(returnStmt.getExpr() != null){
            transToExpr(returnStmt.getExpr());
        }
        methodCodes.add(new Return());
    }

    public void visit(BoolContantExpr expr,Label trueLabel, Label falseLabel){
         if(expr.getTag() == Tag.TRUE){
             methodCodes.add(new Goto(trueLabel));
         } else{
             methodCodes.add(new Goto(falseLabel));
         }
    }

    public void visit(NotExpr notExpr,Label trueLabel,Label falseLabel){
        LogicExpr expr = (LogicExpr) notExpr.getExpr1();
        if(isStatement()){
            transToStmt(expr,trueLabel,falseLabel);
        } else{
            transToExpr(expr);
        }

    }

    public void visit(SeqStmt seqStmt){
        if(seqStmt.getPre() != null){
            seqStmt.getPre().generate(this);
        }
        if(seqStmt.getAfter() != null){
            seqStmt.getAfter().generate(this);
        }
    }
    public void visit(Stmt stmt){
        stmt.generate(this);
    }
    public void visit(TermExpr termExpr){
        Expr expr = termExpr.getTerm();
        //表达式执行
        if(termExpr.getTag() == Tag.ADD){
            transToExpr(expr);
        } else if(termExpr.getTag() == Tag.SUB){
            transToExpr(expr);
            methodCodes.add(new Neg());
        } else if(termExpr.getTag() == Tag.BITNOT){
            transToExpr(expr);
            methodCodes.add(new BitNeg());
        } else{
            //自增或自减，将 ++a 转成 a++
            if(expr instanceof VariableExpr){
                PostfixExpr p = new PostfixExpr(expr,termExpr.getTag());
                p.generate(this);
            } else if(expr instanceof PostfixExpr){
                List<Expr> postfixExprs = postfixExprToList((PostfixExpr) expr);
                ((PostfixExpr)postfixExprs.get(postfixExprs.size() - 1)).setTag(termExpr.getTag());
                expr.generate(this);
            }
        }
    }

    public void visit(ExprStmt exprStmt){
        transToStmt(exprStmt.getExpr());
    }

    public void visit(VariableExpr variableExpr){
        int index = env.varIndex(variableExpr.getVar());
        methodCodes.add(new Load(index));
    }
    public void visit(WhileStmt whileStmt){
        Label beginLabel  = new Label();
        Label bodyLabel  = new Label();
        Label endLabel = new Label();
        methodCodes.add(beginLabel);
        LogicExpr logicExpr = (LogicExpr)whileStmt.getCondition();
        transToStmt(logicExpr,bodyLabel,endLabel);
        methodCodes.add(bodyLabel);
        //用于 break语句 和 continue语句的处理
        continueLabel.push(beginLabel);
        breakLabel.push(endLabel);
        whileStmt.getWhileBody().generate(this);
        continueLabel.pop();
        breakLabel.pop();
        //跳转到开始标签
        methodCodes.add(new Goto(beginLabel));
        //添加结束标签
        methodCodes.add(endLabel);

    }
    public void visit(MethodDeclare methodDeclare){
        methodCodes = new ArrayList<>();
        Env newEnv = new Env(env.getPreVar());
        newEnv.setPrev(env);
        env = newEnv;
        //如果不是静态方法,需要把this添加到变量表里面
        if(!methodDeclare.isStatic()){
            env.addVar(Tag.THIS.getStr(),new Type(classNode.getName()));
        }
        //将函数的参数加入到局部变量表里面
        for(VirtualArg virtualArg : methodDeclare.getVirtualArgs().getVirtualArgs()){
            env.addVar(virtualArg.getVar(),virtualArg.getType());
        }
        methodDeclare.getStmt().generate(this);
        //进行还原
        env = env.getPrev();
        methodDeclare.setByteCodes(methodCodes);
        System.out.println(classNode.getType().getType()+":"+methodDeclare.getFuncName());
        System.out.println(methodCodes);
        methodCodes = null;
    }

    /**
     * 布尔值需要存储时的通用操作
     */
    private void boolNeedStore(Label trueLabel,Label falseLabel,Label endLabel){
        methodCodes.add(trueLabel);
        methodCodes.add(new Constant(new Num(1)));
        //跳转到结尾
        methodCodes.add(new Goto(endLabel));
        //假
        methodCodes.add(falseLabel);
        methodCodes.add(new Constant(new Num(0)));
        //结尾
        methodCodes.add(endLabel);
    }
}
