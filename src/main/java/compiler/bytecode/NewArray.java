

package compiler.bytecode;

import compiler.enums.Tag;

/**
    数组创建
 **/

public class NewArray extends ByteCode {
    /**
     * 基本数据类型数组
     */
    private Tag tag;
    /**
     * 复杂数据类型数组
     */
    private String type;
    public NewArray(Tag tag){
        this.tag = tag;
    }
    public NewArray(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        if(tag != null){
            return "newArray "+tag.getStr()+"[]";
        }
        return "newArray "+type + "[]";
    }
}
