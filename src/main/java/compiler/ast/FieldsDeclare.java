package compiler.ast;

import java.util.List;

public class FieldsDeclare extends Node {
    private final List<FieldDeclare> fieldDeclareList;

    public FieldsDeclare(List<FieldDeclare> fieldDeclareList) {
        this.fieldDeclareList = fieldDeclareList;
    }
}
