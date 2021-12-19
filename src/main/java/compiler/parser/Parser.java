

package compiler.parser;

import compiler.ast.*;
import compiler.enums.Tag;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.Word;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static compiler.enums.Tag.*;

/**
 * @author
 * @date 2021/12/18
 **/

public  class Parser {
    private Lexer lex;
    /**
     * 当前查看的 Token
     */
    private Token look;

    public Parser(Lexer l)  {
        lex = l;
        move();
    }

    /***
     * 扫描到下一个 Token， 赋值给 look
     */
    void move()  {
        try {
            look = lex.scan();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void match(Tag t) {
        if (look.tag == t) {
            move();
        } else {
            error("syntax error");
        }
    }
    void error(String s) {
    }

    /**
     * 是否有访问控制符
     */
    private boolean isAccessControl(){
        switch (look.tag){
            case PUBLIC:
            case DEFATULT:
            case PROTECTED:
            case PRIVATE:return true;
            default:return false;
        }
    }

    /**
     * 是否是静态的
     */
    public boolean isStatic(){
        return look.tag == STATIC;
    }
    /**
     * 是否是final的
     */
    public boolean isFinal(){
        return look.tag == FINAL;
    }
    /**
     * 是否是基本数据类型
     */
    public boolean isBasic(){
        switch (look.tag){
            case BYTE:
            case CHAR:
            case SHORT:
            case INT:case LONG:case FLOAT:case DOUBLE:case VOID: case BOOL:return true;
            default:return false;
        }
    }

    /**
     * 是否是 比较关系的运算符
     */
    public boolean isRelation(){
       switch (look.tag){
           case EQ:case NE:case GE:case GI:case LE:case LI:return true;
           default:return false;
       }
    }

    /**
     * 处理类
     * @param isForType  第一次将所有类的类型进行加载，之后再进行完整的分析
     */
    public void clazz(boolean isForType){
        //访问权限修饰符
        if(isAccessControl()){
            move();
        }
        //class
        match(CLASS);
        // 类名称
        if(look.tag == ID){
            TypeMap.addType(((Word)look).getStr());
            move();
        }

        // 判断有无extends
        if(look.tag == EXTENDS){
            match(EXTENDS);
            if(look.tag == ID){
                TypeMap.addType(((Word)look).getStr());
                move();
            }
        }
        //判断有无接口继承
        if(look.tag == IMPLEMENTS){
            match(IMPLEMENTS);
            while(look.tag != BIG_LEFT){
                TypeMap.addType(((Word)look).getStr());
                move();
                match(SPLIT);
            }
        }
        if(isForType){
            return;
        }
        match(BIG_LEFT);
        //处理类的内容
        classBody();
        match(BIG_RIGHT);
    }

    /**
     * 处理类的内容，主要包括
     * 变量声明， 函数声明， 代码块
     */
    public void classBody(){
        if(look.tag == BIG_RIGHT){
            return;
        }
        // 访问修饰符
        if(isAccessControl()){
            move();
        }
        // static 或者 final
        boolean isStatic = isStatic();
        if(isStatic){
            move();
        }
        boolean isFinal = isFinal();
        if(isFinal){
            move();
        }
        if(look.tag == BIG_LEFT){
            //代码块
            match(BIG_LEFT);
            stmts();
            match(BIG_RIGHT);
            //继续处理 classBody
            classBody();
            return;
        }
        //匹配类型，此处的ID 其实就是 class的一种
        if(TypeMap.contains(look)){
            move();
        }
        //有数组,数组可能是函数返回值也可能是变量类型，s用i记录数组的维度
        int i = 0;
        if(look.tag == MID_LEFT){
            for (;;){
                match(MID_LEFT);
                match(MID_RIGHT);
                i++;
                if(look.tag != MID_LEFT){
                    break;
                }
            }
        }
        //变量名或者，方法名
        match(ID);
        //变量声明支持多个
        while (look.tag == ASSIGN || look.tag == SPLIT){
            if(look.tag == ASSIGN){
                match(ASSIGN);
                expr();
            } else{
                //处理下一个变量
                move();
                match(ID);
            }
        }
        //如果是 ; 变量声明结束
        if(look.tag == END){
            System.out.println("变量声明");
            move();
            classBody();
            return;
        }
        //方法声明
        if(look.tag == S_LEFT){
            System.out.println("方法声明");
            match(S_LEFT);
            //形参列表
            virtualArgs();
            match(S_RIGHT);
            method();
            classBody();
        }
    }
    public void method(){
        match(BIG_LEFT);
        stmts();
        match(BIG_RIGHT);
    }

    /**
     * 语句列表
     */
    public void stmts(){
        if(look.tag == BIG_RIGHT){
            return;
        }
        stmt();
        stmts();
    }

    /**
     * 单个语句
     */
    public void stmt(){
        switch (look.tag){
            case END:move();return;
            case IF:
                match(IF);
                match(S_LEFT);
                expr();
                match(S_RIGHT);
                stmt();
                //能够解决悬空else的问题
                if(look.tag == ELSE){
                    match(ELSE);
                    stmt();
                }
                break;
            case FOR:
                match(FOR);
                match(S_LEFT);
                assign();
                match(END);
                expr();
                match(END);
                assign();
                match(S_RIGHT);
                stmt();
                break;
            case WHILE:
                match(WHILE);
                match(S_LEFT);
                expr();
                match(S_RIGHT);
                stmt();
                break;
            case SWITCH:
                match(SWITCH);
                // postfix包含了 a.b.c这种
                postfix();
                match(BIG_LEFT);
                while (look.tag == CASE){
                    match(CASE);
                    match(Mu);
                    if(look.tag == CASE || look.tag == DEFATULT){
                        continue;
                    }
                    stmt();
                }
                if(look.tag == DEFATULT){
                    match(DEFATULT);
                    match(Mu);
                    if(look.tag != BIG_RIGHT){
                        stmt();
                    }
                }
                match(BIG_RIGHT);
                break;
            case DO:match(DO);stmt();match(WHILE);match(S_LEFT);expr();match(S_RIGHT);match(END);break;
            case BREAK:match(BREAK);match(END);break;
            case CONTINUE:match(CONTINUE);match(END);break;
            case BIG_LEFT:match(BIG_LEFT);stmts();match(BIG_RIGHT);break;
            //变量声明，变量赋值
            default:break;
        }
    }

    /**
     * 赋值表达式
     */
    public void assign(){
        //变量定义或者带有变量定义的赋值表达式
        if(TypeMap.contains(look)){
            move();
            match(ID);
            //变量声明支持多个
            while (look.tag == ASSIGN || look.tag == SPLIT){
                if(look.tag == ASSIGN){
                    match(ASSIGN);
                    expr();
                } else{
                    //处理下一个变量
                    move();
                    match(ID);
                }
            }
            if(look.tag == END){
                match(END);
                return;
            }
        }
        //赋值表达式，用postfix作为左边，虽然他的范围要更加广一点
        postfix();
        //跳过的这个符号可以用来支持 += -= *= /= ....
        move();
        expr();
    }
    /**
     * 表达式
     */
    public Expr expr(){
        Expr expr = null;
        // 创建对象数组， 创建对象等同于方法调用
        if(look.tag == NEW){
            match(NEW);
            Token type = look;
            if(look.tag == ID || isBasic()){
                move();
            }
            if(look.tag == S_LEFT){
                match(S_LEFT);
                Args objectArgs = args();
                Word word = (Word)type;
                expr = new NewObjectExpr(word.getStr(),objectArgs);
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

    public Expr expr10(){
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
    public Expr expr9(){
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
    public Expr expr8(){
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
    public Expr expr7(){
        Expr expr = expr6();
        while(isRelation()){
            Tag tag = look.tag;
            move();
            expr = new RelExpr(expr,expr6(),tag);
        }
        return expr;
    }

    /**
     *  位运算 |
     */
    public Expr expr6(){
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
    public Expr expr5(){
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
    public Expr expr4(){
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
    public Expr expr3(){
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
    public Expr expr2(){
        Expr expr = expr1();
        while(look.tag == ADD || look.tag == SUB){
            Tag tag = look.tag;
            move();
            expr = new BinaryExpr(tag,expr, expr1());
        }
        return expr;
    }
    public Expr expr1(){
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
    public Expr term(){
        switch (look.tag){
            case ADDADD:
            case SUBSUB:
            case ADD:
            case SUB:
            case NOT:
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
    public Expr postfix(){
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
    public Expr primary(){
        Expr result = null;
        if(look.tag == ID || look.tag == NUM || look.tag == REAL
                || TypeMap.contains(look) || look.tag == STR || look.tag == CH || look.tag == TRUE
                || look.tag == FALSE || look.tag == THIS || look.tag == SUPER || look.tag == NULL){
            if(TypeMap.contains(look) && !isBasic()){
                Word word = (Word)look;
                result =  new TypeExpr(new Type(word.getStr()));
            } else if(look.tag == ID){
                Word word = (Word)look;
                result = new VariableExpr(word.getStr());
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
    public Args args(){
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
    public void virtualArgs(){
        //基本数据类型 或对象类型
        if(TypeMap.contains(look)){
            move();
            move();
            while(look.tag == SPLIT){
                move();
                if(TypeMap.contains(look)){
                    move();
                    move();
                } else{
                    break;
                }
            }
        }
    }

}
