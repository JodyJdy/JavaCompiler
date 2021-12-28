package compiler.ast;

import java.util.List;

/**
 * 多字段声明
 */
public class MultiFieldDeclare extends Node {
    private final List<FieldDeclare> fieldDeclareList;

    public List<FieldDeclare> getFieldDeclareList() {
        return fieldDeclareList;
    }

    public MultiFieldDeclare(List<FieldDeclare> fieldDeclareList) {
        this.fieldDeclareList = fieldDeclareList;

    }
}
