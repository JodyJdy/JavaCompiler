

package compiler.parser;

import compiler.ast.*;
import compiler.enums.Tag;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.Word;
import compiler.util.ParserHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static compiler.enums.Tag.*;

/**
 * 用于生成抽象语法树
 **/

public  class Parser {
    private Lexer lex;
    /**
     * 当前查看的 Token
     */
    private Token look;

    /**
     * 使用队列进行回溯
     */
    private List<Token> tokenList = new LinkedList<>();

    /**
     * 读取到到队列到下标
     */
    private  int tokenIndex = 0;

    /**
     * 当前解析的类
     */
    private ClassNode classNode = new ClassNode();

    public Parser(Lexer l)  {
        lex = l;
        move();
    }

    /***
     * 扫描到下一个 Token， 赋值给 look
     */
    private  void move()  {
        try {
            if(tokenIndex >= tokenList.size()){
                tokenList.add(lex.scan());
            }
            look = tokenList.get(tokenIndex++);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 回溯到n个字符前,不包括当前字符a,b 回到1个字符前是a
     */
    void back(int n){
        tokenIndex-=n+1;
        move();
    }

    /**
     * 回溯到第n个字符
     */
    private void backToN(int n){
        tokenIndex = n;
        look = tokenList.get(tokenIndex++);
    }
    /**
     * 向前看,
     */
    private void lookForWard(Runnable runnable){
        runnable.run();
    }

    /**
     * 获取当前look所在的下标
     * 因为 move()里面取了后会加1 tokenIndex++,所以当前下标要减1
     */
    private int getCurTokenIndex(){
        return tokenIndex - 1;
    }


    private void match(Tag t) {
        if (look.tag == t) {
            move();
        } else {
            error("syntax error");
        }
    }
    private void error(String s) {
        System.err.println(s);
    }
    /**
     * 处理  ID.ID.ID 格式的类名数据
     */
    private String processClassName(){
        StringBuilder sb = new StringBuilder();
        if(!(look instanceof Word)){
            return null;
        }
        Word word = (Word)look;
        sb.append(word.getStr());
        move();
        while(look.tag == POINT){
            match(POINT);
            sb.append(".");
            if(look.tag == ID){
                word = (Word)look;
                sb.append(word.getStr());
            }
            move();
        }
        return sb.toString();
    }




    /**
     * 类信息的预处理
     * 1.判断package语句是否合法
     * 2.加载import语句，先不解析import语句
     * 3.获取类的名称，结合包名，拿到一个完整的类标识符
     */
    public void classPreProcess(boolean isDefault){
        if(!isDefault && look.tag != PACKAGE){
            System.err.println("错误的包名开头");
        }
        if(look.tag == PACKAGE){
            match(PACKAGE);
            classNode.setPackageName(processClassName());match(END);
        }
        //处理import语句
        while(look.tag == IMPORT){
            match(IMPORT);
            classNode.getImports().add(processClassName());match(END);
        }
        //获取类名称
        while(look.tag != CLASS && look.tag != INTERFACE && look.tag != ENUM){
            move();
        }
        if(look.tag == CLASS){
            match(CLASS);
            classNode.setFileType(CLASS);
        }
        if(look.tag == INTERFACE){
            match(INTERFACE);
            classNode.setFileType(INTERFACE);
        }
        //此时的look就是类名称
        Word id = (Word)look;
        String className = id.getStr();
        classNode.setName(className);
        //import语句中加入自身
        classNode.getImports().add(classNode.getPackageName()+"."+classNode.getName());
        ClassesLoader.addClass(classNode.getPackageName()+"."+classNode.getName(),classNode);
        backToN(0);
    }

    /**
     * 获取连续的多个类型
     */
    private List<Type> getMultiType(){
        List<Type> impls = new ArrayList<>();
        while(look.tag != BIG_LEFT){
            String interFaceName = ParserHelper.tranClassNameWithPackage(processClassName(),classNode);
            impls.add(new Type(interFaceName));
            if(look.tag == BIG_LEFT){
                break;
            }
            match(SPLIT);
        }
        return impls;
    }
    /**
     * 处理类
     */
    public void clazz(){
        //初始化已经导入的类的信息，
        classNode.initImportedClasses();
        //跳转到处理类的部分
        while(!ParserHelper.isAccessControl(look) && look.tag != classNode.getFileType()){
            move();
        }
        //访问权限修饰符
        if(ParserHelper.isAccessControl(look)){
            classNode.setAccess(look.tag);
            move();
        }
        //类名已经预处理过
        match(classNode.getFileType());
        match(ID);
        // 判断有无extends
        if(look.tag == EXTENDS){
            match(EXTENDS);
            //加载父类的名称
            if(CLASS == classNode.getFileType()) {
                String parentClassName = ParserHelper.tranClassNameWithPackage(processClassName(), classNode);
                classNode.setExtendsC(new Type(parentClassName));
            } else if(INTERFACE == classNode.getFileType()){
                classNode.setImpls(getMultiType());
            }
        }
        //判断有无接口继承
        if(look.tag == IMPLEMENTS){
            match(IMPLEMENTS);
            classNode.setImpls(getMultiType());
        }
        match(BIG_LEFT);
        ClassBody classBody = new ClassBody();
        classBody(classBody);
        classNode.setClassBody(classBody);
        match(BIG_RIGHT);
        //初始化类的相关信息
        classNode.initClass();
    }

    /**
     * 处理类的内容，主要包括
     * 变量声明， 函数声明， 代码块
     */
    private void classBody(ClassBody classBody){
        if(look.tag == BIG_RIGHT){
            return;
        }
        Tag access = null;
        // 访问修饰符
        if(ParserHelper.isAccessControl(look)){
            access = look.tag;
            move();
        }
        // static 或者 final
        boolean isStatic = ParserHelper.isStatic(look);
        if(isStatic){
            move();
        }
        boolean isFinal = ParserHelper.isFinal(look);
        if(isFinal){
            move();
        }
        if(look.tag == BIG_LEFT){
            //代码块
            match(BIG_LEFT);
            classBody.addBlock(new Block(stmts(),isStatic));
            match(BIG_RIGHT);
            //继续处理 classBody
            classBody(classBody);
            return;
        }

        Token pre;
        //匹配类型
        if(ParserHelper.isBasic(look.tag)){
            pre = look;
            move();
        } else{
            String typeName = ParserHelper.tranClassNameWithPackage(processClassName(),classNode);
            //重新生成Token
            pre = Token.wordToken(typeName);
        }
        Type typeExpr = getArrayType(pre);
        //变量名或者，方法名
        String varName;
        Word word = (Word)look;
        varName = word.getStr();
        move();
        List<FieldDeclare> fieldDeclares = new ArrayList<>();
        if(look.tag == END){
            fieldDeclares.add(new FieldDeclare(access,typeExpr,varName,null, isStatic));
            classBody.addField(new MultiFieldDeclare(fieldDeclares));
            move();
            classBody(classBody);
            return;
        }
        //变量声明支持多个
        while (look.tag == ASSIGN || look.tag == SPLIT ){
            if(look.tag == ASSIGN){
                match(ASSIGN);
                fieldDeclares.add(new FieldDeclare(access,typeExpr,varName, expr(), isStatic));
            } else{
                //处理下一个变量
                move();
                fieldDeclares.add(new FieldDeclare(access,typeExpr,varName,null, isStatic));
                match(ID);
            }
        }
        //字段声明添加进去
        if(!fieldDeclares.isEmpty()){
            classBody.addField(new MultiFieldDeclare(fieldDeclares));
        }
        //如果是 ; 变量声明结束
        if(look.tag == END){
            move();
            classBody(classBody);
            return;
        }
        //方法声明
        if(look.tag == S_LEFT){
            match(S_LEFT);
            //形参列表
            VirtualArgs virtualArgs = virtualArgs();
            match(S_RIGHT);
            Stmt me = null;
            if(classNode.getFileType() == CLASS){
                me = block();
            } else if (classNode.getFileType() == INTERFACE){
                //方法声明以 ; 结尾
                match(END);
            }
            classBody.addMethod(new MethodDeclare(varName, typeExpr, access, isStatic, virtualArgs, me));
            classBody(classBody);
        }
    }

    /**
     * 代码块
     */
    private Stmt block(){
        Stmt result;
        match(BIG_LEFT);
        result = stmts();
        match(BIG_RIGHT);
        return new BlockStmt(result);
    }

    /**
     * 语句列表
     */
    private Stmt stmts(){
        if(look.tag == BIG_RIGHT){
            return null;
        }
        return new SeqStmt(stmt(),stmts());
    }

    /**
     * 单个语句
     */
    private Stmt stmt(){
        switch (look.tag){
            case END:move();return Stmt.NULL;
            case IF:
                match(IF);
                match(S_LEFT);
                Expr condition = expr();
                match(S_RIGHT);
                boolean hasBig = false;
                if(look.tag == BIG_LEFT){
                    match(BIG_LEFT);
                    hasBig = true;
                }
                Stmt thenStmt = stmt();
                if(hasBig){
                    match(BIG_RIGHT);
                }
                Stmt elseStmt = null;
                //能够解决悬空else的问题
                if(look.tag == ELSE){
                    match(ELSE);
                    if(look.tag == BIG_LEFT){
                        match(BIG_LEFT);
                        hasBig = true;
                    }
                    elseStmt = stmt();
                    if(hasBig){
                        match(BIG_RIGHT);
                    }
                }
                return new IfStmt(condition,thenStmt,elseStmt);
            case FOR:
                match(FOR);
                match(S_LEFT);
                Stmt as1;
                if(look.tag == END){
                    as1 = null;
                } else{
                    as1 = new ExprStmt(expr());
                }
                Expr cond = expr();
                match(END);
                Stmt as2;
                if(look.tag == END){
                    as2 = null;
                } else{
                    as2 = new ExprStmt(expr());
                }
                match(S_RIGHT);
                //为了for语句里面的变量作用域和for{}括号外面相同，需要自己处理{}
                match(BIG_LEFT);
                Stmt forBody = stmt();
                match(BIG_RIGHT);
                return new ForStmt(as1,cond,as2,forBody);
            case WHILE:
                match(WHILE);
                match(S_LEFT);
                cond = expr();
                match(S_RIGHT);
                match(BIG_LEFT);
                Stmt stmt = stmts();
                match(BIG_RIGHT);
                return new WhileStmt(cond,stmt);
                //暂不处理switch，
//            case SWITCH:
//                match(SWITCH);
//                // postfix包含了 a.b.c这种
//                postfix();
//                match(BIG_LEFT);
//                while (look.tag == CASE){
//                    match(CASE);
//                    match(Mu);
//                    if(look.tag == CASE || look.tag == DEFATULT){
//                        continue;
//                    }
//                    stmt();
//                }
//                if(look.tag == DEFATULT){
//                    match(DEFATULT);
//                    match(Mu);
//                    if(look.tag != BIG_RIGHT){
//                        stmt();
//                    }
//                }
//                match(BIG_RIGHT);
//                break;
            case DO:
                match(DO);
                match(BIG_LEFT);
                Stmt doBody = stmt();
                match(BIG_RIGHT);
                match(WHILE);
                match(S_LEFT);
                cond = expr();
                match(S_RIGHT);
                match(END);
                return new DoStmt(doBody,cond);
            case BREAK:match(BREAK);match(END);return new BreakStmt();
            case CONTINUE:match(CONTINUE);match(END);return new ContinueStmt();
            case BIG_LEFT:return block();
            case RETURN:match(RETURN);
            Stmt result;
            if(look.tag == END){
                result = new ReturnStmt(null);
            } else{
                result = new ReturnStmt(expr());
            }
            match(END);
            return result;
            //变量声明，变量赋值
            default:result = new ExprStmt(expr());match(END);return result;
        }
    }


    /**
     * 赋值表达式 级别最低的表达式
     */
    private Expr expr(){

        //需要明确第一个符号是类型 还是表达式的开头，对于 Type.x()这种以类型作为开头的表达式，类型的具体解析到中间代码阶段处理
         Token pre = null;
         //记录当前下标
         int curN = getCurTokenIndex();
         if(ParserHelper.isBasic(look.tag)){
             pre = look;
             move();
         } else{
            String typeName = ParserHelper.tranClassNameWithPackage(processClassName(),classNode);
            //变量定义
             if(typeName != null) {
                 pre = Token.wordToken(typeName);
             }
         }
         //代表不是变量声明，而是可执行的表达式
         if(pre == null){
             backToN(curN);
             return expr12();
         }
        //局部变量定义
        Type typeExpr = getArrayType(pre);
        String varName;
        Word word = (Word)look;
        varName = word.getStr();
        move();
        //单个变量声明
        if(look.tag == END || look.tag == S_RIGHT){
            return new LocalVarDeclare(typeExpr,varName,null);
        }
        List<LocalVarDeclare>varDeclares = new ArrayList<>();
        //变量声明支持多个
        while (look.tag == ASSIGN || look.tag == SPLIT){
            if(look.tag == ASSIGN){
                match(ASSIGN);
                varDeclares.add(new LocalVarDeclare(typeExpr,varName,expr11()));
            } else{
                varDeclares.add(new LocalVarDeclare(typeExpr,varName,null));
                //处理下一个变量
                move();
                word = (Word)look;
                varName = word.getStr();
                move();
            }
        }
        return new MultiLocalVarDeclare(varDeclares);
    }
    /**
     * 不包含变量声明的赋值表达式
     */
    private Expr expr12(){
        //记录此时的下标，move里面
        int curN = getCurTokenIndex();
        lookForWard(this::term);
        //单操作符运算 ++x.a.b, x.a.b++
        if(look.tag == END || look.tag == S_RIGHT){
            backToN(curN);
            return term();
        }
        //赋值运算
        if(look.tag == ASSIGN) {
            backToN(curN);
            Expr left = term();
            move();
            Expr right = expr();
            return new Assign(left,right);
        }
        backToN(curN);
        //普通的表达式
        return expr11();
    }

    /**
     * 表达式
     */
    private Expr expr11(){
        Expr expr = null;
        // 创建对象数组， 创建对象等同于方法调用
        if(look.tag == NEW){
            match(NEW);
            Token type = look;
            if(look.tag == ID ){
                //解析出类名
                String name = ParserHelper.tranClassNameWithPackage(processClassName(),classNode);
                type = Token.wordToken(name);
            } else if(ParserHelper.isBasic(look.tag)){
                move();
            }
            if(look.tag == S_LEFT){
                match(S_LEFT);
                Args objectArgs = args();
                Word word = (Word)type;
                expr = new NewObjectExpr(word.getStr(),objectArgs);
                match(S_RIGHT);
            //数组创建
            } else if(look.tag == MID_LEFT){
                List<Expr> arrayArgs = new ArrayList<>();
                while (look.tag == MID_LEFT){
                    match(MID_LEFT);
                    arrayArgs.add(expr());
                    match(MID_RIGHT);
                }
                if(type.tag == ID){
                    expr = new NewArrayExpr(((Word)type).getStr(),arrayArgs);
                } else{
                    expr = new NewArrayExpr(type.tag,arrayArgs);
                }

            }

        } else {
            expr = expr10();
        }
        return expr;
    }

    /**
     * 优先级越高的 数字越小
     * 三元表达式
     */

    private Expr expr10(){
        Expr expr = expr9();
        if(look.tag == QUES){
            match(QUES);
            Expr trueExpr = expr9();
            match(Mu);
            Expr falseExpr = expr9();
            return new ChoseExpr(expr,trueExpr,falseExpr);
        }
        return expr;
    }

    /**
     * bool表达式最上层  ||
     */
    private Expr expr9(){
        Expr expr = expr8();
        while (look.tag == OR){
            Tag tag = look.tag;
            move();
            expr = new LogicExpr(expr,expr8(),tag);
        }
        return expr;

    }
    /**
     * bool表达式 第二层 &&
     */
    private Expr expr8(){
        Expr expr = expr7();
        while(look.tag == AND){
            Tag tag = look.tag;
            move();
            expr = new LogicExpr(expr, expr7(), tag);
        }
        return expr;

    }

    /**
     * 关系表达式
     */
    private Expr expr7(){
        Expr expr = expr6();
        while(ParserHelper.isRelation(look)){
            Tag tag = look.tag;
            move();
            expr = new RelExpr(expr,expr6(),tag);
        }
        return expr;
    }

    /**
     *  位运算 |
     */
    private Expr expr6(){
        Expr expr = expr5();
        while(look.tag == BITOR){
            move();
            expr = new BinaryExpr(look.tag,expr,expr5());
        }
        return expr;
    }
    /**
     *  位运算 ^
     */
    private Expr expr5(){
        Expr expr = expr4();
        while (look.tag == XOR){
            move();
            expr = new BinaryExpr(look.tag,expr,expr4());
        }
        return expr;
    }
    /**
     *  位运算 &
     */
    private Expr expr4(){
        Expr expr = expr3();
        while(look.tag == BITAND){
            Tag tag = look.tag;
            move();
            expr = new BinaryExpr(tag,expr,expr3());
        }
        return expr;
    }

    /**
     * 移位操作
     */
    private Expr expr3(){
        Expr expr = expr2();
        while(look.tag == LSHIFT || look.tag == RSHIFT){
            Tag tag = look.tag;
            move();
            expr = new BinaryExpr(tag,expr,expr2());
        }
        return expr;
    }

    /**
     * 加减操作
     */
    private Expr expr2(){
        Expr expr = expr1();
        while(look.tag == ADD || look.tag == SUB){
            Tag tag = look.tag;
            move();
            expr = new BinaryExpr(tag,expr, expr1());
        }
        return expr;
    }
    private Expr expr1(){
        Expr expr = term();
        while(look.tag == MUL || look.tag == DIV || look.tag == MOD){
            Tag tag = look.tag;
            move();
            expr = new BinaryExpr(tag,expr,term());
        }
        return expr;
    }

    /**
     * 项 以及前缀处理相关的
     */
    private Expr term(){
        switch (look.tag){
            case NOT:move();return new NotExpr(term());
            case ADDADD:
            case SUBSUB:
            case ADD:
            case SUB:
            case BITNOT:
                Tag tag = look.tag;
                move();
                return new TermExpr(tag,term());
            default:return postfix();
        }
    }

    /**
     * 后缀
     */
    private Expr postfix(){
        Expr expr = primary();
        if(look.tag == ADDADD || look.tag == SUBSUB){
            expr = new PostfixExpr(expr,look.tag);
            move();
        } else {
            //数组调用，多维
            if(look.tag == MID_LEFT){
                List<Expr> arrayArgs = new ArrayList<>();
                while (look.tag == MID_LEFT){
                    match(MID_LEFT);
                    arrayArgs.add(expr());
                    match(MID_RIGHT);
                }
                expr = new PostfixExpr(expr,arrayArgs);
                //当是 a[10]++; 这种时
                if(look.tag == ADDADD || look.tag == SUBSUB){
                    PostfixExpr p = (PostfixExpr)expr;
                    p.setTag(look.tag);
                    move();
                }
            }
            //函数调用
            if(look.tag == S_LEFT){
                match(S_LEFT);
                expr = new PostfixExpr(expr,args());
                match(S_RIGHT);
            }
            // .运算符
            if(look.tag == POINT){
                move();
                expr = new PostfixExpr(expr,postfix());
            }
        }
        return expr;
    }

    /**
     * 代表各种量，也可以是类型 ，因为静态变量支持 类型.xx 访问
     */
    private Expr primary(){
        Expr result = null;
        if(look.tag == ID || look.tag == NUM || look.tag == REAL
                || look.tag == STR || look.tag == CH || look.tag == TRUE
                || look.tag == FALSE || look.tag == THIS || look.tag == SUPER || look.tag == NULL){
            //将id识别成变量，具体的含义等到 语义分析时再处理
          if(look.tag == ID){
                Word word = (Word)look;
                result = new VariableExpr(word.getStr());
            //布尔常量
            } else if(look.tag == TRUE || look.tag == FALSE){
                result = new BoolContantExpr(look.tag);
            } else{
                result = new ConstantExpr(look);
            }
            move();
        } else if(look.tag == S_LEFT){
            match(S_LEFT);
            result = expr();
            match(S_RIGHT);
        }
        return result;
    }


    /**
     * 函数实参列表
     */
    private Args args(){
        //无参函数
        if(look.tag == S_RIGHT){
            return new Args();
        }
        List<Expr> exprs = new ArrayList<>();
        exprs.add(expr());
        while(look.tag == SPLIT){
            match(SPLIT);
            exprs.add(expr());
        }
        return new Args(exprs);
    }
    /**
     * 函数形参列表
     */
    private VirtualArgs virtualArgs(){
        List<VirtualArg> virtualArgs = new ArrayList<>();
        //无参函数的形参
        if(look.tag == S_RIGHT){
            return new VirtualArgs(virtualArgs);
        }
        while(look.tag != S_RIGHT){
            Token pre;
            //基本数据类型
            if(ParserHelper.isBasic(look.tag)){
                pre = look;
            //复杂数据类型
            } else{
                pre = Token.wordToken(ParserHelper.tranClassNameWithPackage(processClassName(),classNode));
            }
            move();
            Type typeExpr = getArrayType(pre);
            String varName = ((Word)look).getStr();
            move();
            virtualArgs.add(new VirtualArg(typeExpr,varName));
        }
        return new VirtualArgs(virtualArgs);
    }

    /**
     * 获取数组类型
     */
    private Type getArrayType(Token type){
        Type typeExpr;
        //含有数组
        if(look.tag == MID_LEFT){
            int d = 0;
            while(look.tag == MID_LEFT){
                match(MID_LEFT);
                match(MID_RIGHT);
                d++;
            }
            typeExpr = ParserHelper.arrayType(type,d);
        } else{
            typeExpr = ParserHelper.type(type);
        }
        return typeExpr;
    }

    public ClassNode getClassNode() {
        return classNode;
    }
}
