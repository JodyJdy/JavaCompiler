package compiler.parser;

import compiler.enums.Tag;

public class ArrayType extends Type {
    /**
     * 数组的维度
     */
    private final int dimension;
    public ArrayType(String type,int dimension) {
        super(type);
        this.dimension = dimension;
    }
    public ArrayType(Tag tag, int dimension) {
        super(tag);
        this.dimension = dimension;
    }
}
