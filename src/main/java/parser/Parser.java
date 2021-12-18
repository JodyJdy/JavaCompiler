package parser;

import inter.*;
import lexer.*;
import symbols.Array;
import symbols.Env;
import symbols.Type;

import java.io.IOException;

public class Parser {
    private Lexer lex;
    /**
     * 当前查看的 Token
     */
    private Token look;
    Env top = null;
    int used = 0;

    public Parser(Lexer l) throws IOException {
        lex = l;
        move();
    }

    /***
     * 扫描到下一个 Token， 赋值给 look
     */
    void move() throws IOException {
        look = lex.scan();
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) throws IOException {
        if (look.tag == t) move();
        else error("syntax error");
    }

    /**
     * 程序， 由 代码块构成 { xxx }
     */
    public void program() throws IOException {
        Stmt s = block();
        int begin = s.newlabel();
        int after = s.newlabel();
        s.emitlabel(begin);
        s.gen(begin, after);
        s.emitlabel(after);
    }

    /**
     * 代码块 由  大括号 以及里面的语句构成
     */
    Stmt block() throws IOException {
        match('{');
        // 解析过程中会存在嵌套的Block，需要存储当前的 Env,Env里面存放的是变量表， Env里面是一个链表
        // b所在的block能够访问到 a所在的block1里面的变量，从而使得变量不能重复定义

        /**
         *
         *   {
         *      in a;
         *       {
         *         int b;
         *        }
         *   }
         *
         */
        Env savedEnv = top;
        top = new Env(top);
        // 大括号里面的内容
        Stmt s = stmts();
        match('}');
        top = savedEnv;
        return s;
    }

    /**
     * 使用Env 存储变量，Env保存了prev的Env，可以查看变量是否重定义
     * @throws IOException
     */
    void decls() throws IOException {
        // Basic 是 int,float这种变量声明
        while (look.tag == Tag.BASIC) {
            Type p = type();
            while (true) {
                Token tok = look;
                match(Tag.ID);
                Id id = new Id((Word) tok, p, used);
                if (top.table.get(tok) == null)
                    top.put(tok, id);
                else
                    error("redefine of id " + tok.toString());
                used = used + p.width;
                if (look.tag == ';') {
                    match(';');
                    break;
                } else if (look.tag == '=') {
                    match('=');

                } else {
                    match(',');
                }
            }
        }
    }

    /**
     * 普通类型变量
     */
    Type type() throws IOException {
        Type p = (Type) look;
        match(Tag.BASIC);
        if (look.tag != '[') return p;
        else return dims(p);
    }

    /**
     * 数组类型变量
     */
    Type dims(Type p) throws IOException {
        match('[');
        Token tok = look;
        match(Tag.NUM);
        match(']');
        if (look.tag == '[') {
            p = dims(p);
        }
        return new Array((((Num) tok).value), p);
    }

    Stmt stmts() throws IOException {
        //遇到了变量定义
        if (look.tag == Tag.BASIC) decls();
        // 代表是一个空的{}
        if (look.tag == '}') return Stmt.Null;
        //{}里面有一系列的语句
        else return new Seq(stmt(), stmts());
    }

    Stmt stmt() throws IOException {
        Expr x;
        Stmt s, s1, s2;
        Stmt savedStmt;
        switch (look.tag) {
            case ';':
                move();
                return Stmt.Null;
            case Tag.IF:
                match(Tag.IF);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                if (look.tag != Tag.ELSE) return new If(x, s1);
                match(Tag.ELSE);
                s2 = stmt();
                return new Else(x, s1, s2);
            case Tag.WHILE:
                While whilenode = new While();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = whilenode;
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                whilenode.init(x, s1);
                Stmt.Enclosing = savedStmt;
                return whilenode;
            case Tag.DO:
                Do donode = new Do();
                // Stmt.Enclosing用来跳出循环
                savedStmt = Stmt.Enclosing;
                //记录跳出的节点
                Stmt.Enclosing = donode;
                match(Tag.DO);
                s1 = stmt();
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                match(';');
                donode.init(s1, x);
                Stmt.Enclosing = savedStmt;
                return donode;
            case Tag.BREAK:
                match(Tag.BREAK);
                match(';');
                return new Break();
            case Tag.CONTINUE:
                match(Tag.CONTINUE);
                match(';');
                return new Continue();
            case '{':
                return block();
            default:
                return assign();
        }
    }

    Stmt assign() throws IOException {
        Stmt stmt;
        Token t = look;
        match(Tag.ID);
        Id id = top.get(t);
        if (id == null) error(t.toString() + " undeclared");
        if (look.tag == '=') {
            move();
            stmt = new Set(id, bool());
        } else {
            Access x = offset(id);
            match('=');
            stmt = new SetElem(x, bool());
        }
        match(';');
        return stmt;
    }

    /**
     * 处理And表达式
     */
    Expr bool() throws IOException {
        Expr x = join();
        while (look.tag == Tag.OR) {
            Token tok = look;
            move();
            x = new Or(tok, x, join());
        }
        return x;
    }

    /**
     * 处理 And表达式
     */
    Expr join() throws IOException {
        Expr x = equality();
        while (look.tag == Tag.AND) {
            Token tok = look;
            move();
            x = new And(tok, x, equality());

        }
        return x;
    }

    /**
     * 等值表达式
     */
    Expr equality() throws IOException {
        Expr x = rel();
        while (look.tag == Tag.EQ || look.tag == Tag.NE) {
            Token tok = look;
            move();
            x = new Rel(tok, x, rel());
        }
        return x;
    }

    /**
     * 处理好算术运算后 再处理 关系表达式
     */
    Expr rel() throws IOException {
        Expr x = expr();
        switch (look.tag) {
            case '<':
            case Tag.LE:
            case Tag.GE:
            case '>':
                Token tok = look;
                move();
                return new Rel(tok, x, expr());
            default:
                return x;
        }
    }

    /**
     * 优先处理 * / 运算
     */
    Expr expr() throws IOException {
        Expr x = term();
        while (look.tag == '+' || look.tag == '-') {
            Token tok = look;
            move();
            x = new Arith(tok, x, term());
        }
        return x;
    }

    Expr term() throws IOException {
        /**
         * 优先处理 一元运算符， 之后再处理 * /
         */
        Expr x = unary();
        while (look.tag == '*' || look.tag == '/') {
            Token tok = look;
            move();
            x = new Arith(tok, x, unary());
        }
        return x;
    }

    /**
     * 一元表达式
     */
    Expr unary() throws IOException {
        if (look.tag == '-') {
            move();
            return new Unary(Word.minus, unary());
        } else if (look.tag == '!') {
            Token tok = look;
            move();
            return new Not(tok, unary());
        } else return factor();
    }

    /**
     *项
     */
    Expr factor() throws IOException {
        Expr x = null;
        switch (look.tag) {
            case '(':
                move();
                x = bool();
                match(')');
                return x;
            case Tag.NUM:
                x = new Constant(look, Type.Int);
                move();
                return x;
            case Tag.REAL:
                x = new Constant(look, Type.Float);
                move();
                return x;
            case Tag.TRUE:
                x = Constant.True;
                move();
                return x;
            case Tag.FALSE:
                x = Constant.False;
                move();
                return x;
            case Tag.ID:
                String s = look.toString();
                Id id = top.get(look);
                if (id == null) error(look.toString() + " undeclared");
                move();
                if (look.tag != '[') return id;
                else return offset(id);
            default:
                error("syntax error");
                return x;
        }
    }

    /**
     * 数组访问
     */
    Access offset(Id a) throws IOException {
        Expr i;
        Expr w;
        Expr t1, t2;
        Expr loc;
        Type type = a.type;
        match('[');
        i = bool();
        match(']');
        try {
            type = ((Array) type).of;
        } catch (Exception e) {
            error("this object doesn't have so many dimensions");
        }
        w = new Constant(type.width);
        t1 = new Arith(new Token('*'), i, w);
        loc = t1;
        while (look.tag == '[') {
            match('[');
            i = bool();
            match(']');
            try {
                type = ((Array) type).of;
            } catch (Exception e) {
                error("this object doesn't have so many dimensions");
            }
            w = new Constant(type.width);
            t1 = new Arith(new Token('*'), i, w);
            t2 = new Arith(new Token('+'), loc, t1);
            loc = t2;
        }
        return new Access(a, loc, type);
    }
}
