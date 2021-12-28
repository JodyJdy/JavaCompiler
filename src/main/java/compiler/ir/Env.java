package compiler.ir;

import compiler.ast.Type;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 变量表
 */
public class Env {
    private Env prev = null;
    /**
     * 存放变量名 和 变量类型
     */
    private LinkedHashMap<String, Type> vars = new LinkedHashMap<>(2);

    /**
     * 前置变量的个数
     */
    private int preVar;

    public Env(int preVar){
        this.preVar =preVar;
    }

    public Env getPrev() {
        return prev;
    }

    public void setPrev(Env prev) {
        this.prev = prev;
        preVar = prev.preVar + prev.vars.size();
    }
    public void addVar(String varName,Type type){
        vars.put(varName,type);
    }
    boolean hasVar(String varName){
        return getVar(varName) != null;
    }
    Type getVar(String varName){
        Env temp = this;
        do {
            if(temp.vars.containsKey(varName)){
                return temp.vars.get(varName);
            }
            temp = temp.prev;
        }while (temp != null);
        return null;
    }

    /**
     * 获取变量的下标
     */
    int varIndex(String varName){
       if(hasVar(varName)){
           Env temp = this;
           while(temp.prev != null){
               //找到变量
               if(temp.vars.containsKey(varName)){
                   int v = 0;
                   for(Map.Entry<String,Type> entry : temp.vars.entrySet()){
                       if(entry.getKey().equals(varName)){
                           return temp.preVar + v;
                       }
                       v++;
                   }

               } else{
                   temp = temp.prev;
               }
           }
       }
       return -1;
    }


    public int getPreVar() {
        return preVar;
    }

}
