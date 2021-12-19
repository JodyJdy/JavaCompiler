package compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class ClassBody extends Node {
    /**
     * 可用来记录代码顺序
     */
    private List<Node> nodes = new ArrayList<>();
    private List<FieldsDeclare> fieldsDeclares = new ArrayList<>();
    private List<MethodDeclare> methodDeclares = new ArrayList<>();
    private List<Block> blocks = new ArrayList<>();

    public void addBlock(Block block){
        blocks.add(block);
        nodes.add(block);
    }
    public void addMethod(MethodDeclare methodDeclare){
        methodDeclares.add(methodDeclare);
        nodes.add(methodDeclare);
    }
    public void addField(FieldsDeclare fieldsDeclare){
        fieldsDeclares.add(fieldsDeclare);
        nodes.add(fieldsDeclare);
    }
}
