

package compiler.bytecode;

import compiler.enums.Tag;

/**
 * 多维数组创建
 **/

public class MultiNewArray extends ByteCode {
    private int dim;

    /**
       基本数据类型数组
     */
    private Tag tag;
    /**
     * 复杂数据类型数组
     */
    private String type;
    public MultiNewArray(Tag tag,int dim){
        this.tag = tag;
        this.dim = dim;
    }
    public MultiNewArray(String type,int dim){
        this.type = type;
        this.dim = dim;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultiNewArray ");
        if(tag != null){
            sb.append(tag.getStr());
        } else{
            sb.append(type);
        }
        for(int i=0;i<dim;i++){
            sb.append("[]");
        }
        return sb.toString();
    }
}
