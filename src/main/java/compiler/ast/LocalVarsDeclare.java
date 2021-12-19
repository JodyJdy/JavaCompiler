package compiler.ast;

import java.util.List;

/**
 * 多个变量声明
 */
public class LocalVarsDeclare extends Stmt {
    private final List<LocalVarDeclare> varDeclareList;

    public LocalVarsDeclare(List<LocalVarDeclare> varDeclareList) {
        this.varDeclareList = varDeclareList;
    }
}
