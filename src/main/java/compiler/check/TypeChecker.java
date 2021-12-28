

package compiler.check;

import compiler.ast.*;
import compiler.enums.Tag;
import compiler.ir.Env;
import compiler.ast.ArrayType;
import compiler.ast.Type;
import compiler.util.CheckerHelpr;

import java.util.List;

/**
 * @author
 * @date 2021/12/22
 **/

public class TypeChecker {
    private final ClassNode classNode;
    /**
     * 变量表
     */
    private Env env = new Env(0);

    public TypeChecker(ClassNode classNode) {
        this.classNode = classNode;
    }


    
   public Type visit(Assign assign){
      Expr left = assign.getLeft();
      Type leftT = left.check(this);
      Expr right = assign.getRight();
      Type rightT = right.check(this);

      if(CheckerHelpr.isNumeric(leftT) && CheckerHelpr.isNumeric(rightT)){

      } else if(CheckerHelpr.isBool(leftT) || CheckerHelpr.isBool(rightT)){

      } else if(leftT.equals(rightT)){

      } else{
          System.out.println("类型不匹配");
      }
      return null;

    }
   public Type visit(IfStmt ifStmt){
        Type type = ifStmt.getCondition().check(this);
        if(!CheckerHelpr.isBool(type)){
            System.out.println("类型检查错误");
        }

        ifStmt.getThenBody().check(this);
        ifStmt.getElseBody().check(this);

        return null;
    }
   public Type visit(Args args){
       return null;
    }
   public Type visit(BinaryExpr bynary){

        Type typea = bynary.getExpr1().check(this);
        Type typeb = bynary.getExpr2().check(this);
        if(!CheckerHelpr.isNumeric(typea) || !CheckerHelpr.isNumeric(typeb)){
            System.out.println("类型检查错误");
            return null;
        }
        return CheckerHelpr.getHighNumeric(typea,typeb);
       
    }
   public Type visit(BlockStmt blockStmt){
       return blockStmt.getStmt().check(this);
    }

   public Type visit(ChoseExpr choseExpr){
        Type a = choseExpr.getTrueExpr().check(this);
        Type b = choseExpr.getFalseExpr().check(this);
        if(CheckerHelpr.isBool(a) && CheckerHelpr.isBool(b)){
            return new Type(Tag.BOOL);
        }
        System.out.println("类型检查错误");
        return null;
   }
   public Type visit(ConstantExpr constantExpr){
        return new Type(constantExpr.token.tag);
    }
   public Type visit(DoStmt doStmt){
      doStmt.getDoBody().check(this);
      Type type = doStmt.getCondition().check(this);
      if(type.getTag() != Tag.BOOL){
          System.out.println("类型检查错误");
      }
      return null;
    }
   public Type visit(Expr expr){
        return null;
    }
   public Type visit(ForStmt forStmt){
        forStmt.getAssign().check(this);
        Type type = forStmt.getCondi().check(this);
        if(!CheckerHelpr.isBool(type)){
            System.out.println("类型错误");
        }
        forStmt.getAssign2().check(this);
        forStmt.getForBody().check(this);

        return null;
    }

    /**
     * 变量声明
     */
   public Type visit(LocalVarDeclare localVarDeclare){

       Type a = localVarDeclare.getTypeExpr();
       if(localVarDeclare.getInitValue() != null){
           Type b = localVarDeclare.getInitValue().check(this);
           if(!a.equals(b)){
               System.out.println("类型检查错误");
           }
       }
       return  null;

    }
   public Type visit(MultiLocalVarDeclare multiLocalVarDeclare){
       for(LocalVarDeclare localVarDeclare : multiLocalVarDeclare.getVarDeclareList()){
           localVarDeclare.check(this);
       }
       return null;
    }
   public Type visit(LogicExpr logicExpr){
        Type a = logicExpr.getExpr1().check(this);
        Type b = logicExpr.getExpr2().check(this);

        if(CheckerHelpr.isBool(a) && CheckerHelpr.isBool(b)){
            return new Type(Tag.BOOL);
        }
        System.out.println("LogixExpr类型检查错误");
        return null;
    }
   public Type visit(NewArrayExpr newArrayExpr){
        Type type;
        if(newArrayExpr.getTag() != null){
            type = new ArrayType(newArrayExpr.getTag(),newArrayExpr.getArray().size());
        } else{
            type = new ArrayType(newArrayExpr.getType(),newArrayExpr.getArray().size());
        }
        return type;
    }
   public Type visit(NewObjectExpr newObjectExpr){
       Type type = new Type(newObjectExpr.getType());
       return type;
    }

    /**
     * 将包含链式调用的 PostfixExpr表达式转成链表
     */
    public List<Expr> postfixExprToList(PostfixExpr postfixExpr){
        return null;
    }

    /**
     * PostfixExpr为数组调用或函数调用时
     */
   public Type postfixExecute(Expr expr){
        return null;
    }
   public Type visit(PostfixExpr postfixExpr){
        return null;
    }
   public Type visit(RelExpr relExpr){

       Type type1 = relExpr.getExpr1().check(this);
       Type type2 = relExpr.getExpr2().check(this);
        if(!CheckerHelpr.isNumeric(type1) || !CheckerHelpr.isNumeric(type2)){
            System.out.println("类型检查错误");
            return null;
        }
        return new Type(Tag.BOOL);
    }
   public Type visit(ReturnStmt returnStmt){
        return null;
    }

   public Type visit(BoolContantExpr expr){
       return new Type(Tag.BOOL);
    }

   public Type visit(NotExpr notExpr){
      Type type = notExpr.getExpr1().check(this);
      if(!CheckerHelpr.isBool(type)){
          System.out.println("NOtExpr类型检查错误");
      }
      return null;
    }

   public Type visit(SeqStmt seqStmt){
        if(seqStmt.getPre() != null) {
            seqStmt.getPre().check(this);
        }
        if(seqStmt.getAfter()!= null){
            seqStmt.getAfter().check(this);
        }
        return null;
    }

   public Type visit(Stmt stmt){
       return null;
    }
   public Type visit(TermExpr termExpr){
        termExpr.getTerm().check(this);
        return null;
    }
   public Type visit(VariableExpr variableExpr){
       return null;
    }
   public Type visit(WhileStmt whileStmt){
        Type type = whileStmt.getCondition().check(this);
        if(!CheckerHelpr.isBool(type)){
            System.out.println("WhileStmt 类型检查错误");
        }
        whileStmt.getWhileBody().check(this);
        return null;
    }
   public Type visit(MethodDeclare methodDeclare){
        Env newEnv = new Env(env.getPreVar());
        newEnv.setPrev(env);
        env = newEnv;
        //将函数的参数加入到局部变量表里面
        for(VirtualArg virtualArg : methodDeclare.getVirtualArgs().getVirtualArgs()){
            env.addVar(virtualArg.getVar(),virtualArg.getType());
        }
        methodDeclare.getStmt().check(this);
        //进行还原
        env = env.getPrev();
        return null;
    }
    
}
