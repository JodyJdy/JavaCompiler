

package compiler.enums;

public enum Tag {
    /**
     * 导入
     */
    IMPORT("import"),
    /**
     * 文件格式
     */
    CLASS("class"),ENUM("enum"),INTERFACE("interface"),PACKAGE("package"),
    /**
     * 继承，实现
     */
    EXTENDS("extends"),IMPLEMENTS("implements"),
    /**
     * 访问修饰符
     */
    PUBLIC("public"),PROTECTED("protected"),DEFATULT("default"),PRIVATE("private"),
    /**
     * 其他修饰符
     */
    FINAL("final"),STATIC("static"),
    /**
     * 基本数据类型
     */
    BYTE("byte"),CHAR("char"),SHORT("short"),INT("int"),LONG("long"),FLOAT("float"),DOUBLE("double"),BOOL("boolean"), VOID("void"),
    /**
     * 值
     */
    TRUE("true"),FALSE("false"),NULL("null"),THIS("this"),SUPER("super"),
    /**
     * 控制语句
     */
    FOR("for"),WHILE("while"),DO("do"),IF("if"),ELSE("else"),BREAK("break"),CONTINUE("continue"),SWITCH("switch"),CASE("case"),RETURN("return"),NEW("new"),
    /**
     * 运算符 && || ! > >= == <= < != = + - * / & | ^ >> << . ++ --
     */
    AND("&&"),OR("||"),NOT('!'),GI('>'),GE(">="),EQ("=="),
    LE("<="),LI('<'),NE("!="),ASSIGN('='),ADD('+'),SUB('-'),MUL('*'),DIV('/')
    ,BITAND('&'),BITOR('|'),XOR('^'),BITNOT('~'),LSHIFT("<<"),RSHIFT(">>"),POINT('.'),ADDADD("++"),SUBSUB("--"),MOD('%'),
    /**
     * 分割符
     */
    END(';'),SPLIT(','),Mu(':'),QUES('?'),D_MARK('"'),MARK('\''),
    /**
     * 界定符
     */
    BIG_LEFT('{'),BIG_RIGHT('}'),MID_LEFT('['),MID_RIGHT(']'),S_LEFT('('),S_RIGHT(')'),
    /**
     * 其他
     */
    UNDER_LINE('_'),
    /**
     * 含有语义的量
     */
     ID,NUM,REAL,STR,CH;

    private String str = null;
    public Character ch = null;
     Tag(String str){
        this.str = str;
     }
     Tag(){
     }
     Tag(char ch){
         this.ch = ch;
     }

    public String getStr() {
        return str;
    }
    public String getCh(){
         if(ch == null){
             return null;
         }
         return ch.toString();
    }

}
