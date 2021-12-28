

package compiler.ast;

import compiler.check.Checker;
import compiler.check.TypeChecker;
import compiler.ir.Generator;
import compiler.ir.IRGenerator;

import java.util.List;

/**
 语句序列
 *
 * */

public class SeqStmt extends Stmt implements Generator, Checker {
    private final Stmt pre;
    private final Stmt after;

    public SeqStmt(Stmt pre, Stmt after) {
        this.pre = pre;
        this.after = after;
    }


    static Stmt transToStmt(List<Stmt> list){
        SeqStmt seqStmt = null;
        for(int i=list.size() - 1; i>=0;i--){
            if(seqStmt == null){
                seqStmt = new SeqStmt(list.get(i),null);
            } else{
                seqStmt = new SeqStmt(list.get(i),seqStmt);
            }
        }
        return seqStmt;
    }

    public Stmt getPre() {
        return pre;
    }

    public Stmt getAfter() {
        return after;
    }

    @Override
    public void generate(IRGenerator irGenerator) {
        irGenerator.visit(this);
    }

    @Override
    public Type check(TypeChecker checker) {
        checker.visit(this);
        return null;
    }
}
