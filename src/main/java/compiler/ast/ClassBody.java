package compiler.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * 类的内容
 */
public class ClassBody extends Node {
    /**
     * 可用来记录代码顺序
     */
    private List<Node> nodes = new ArrayList<>();
    private List<MultiFieldDeclare> multiFieldDeclares = new ArrayList<>();
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
    public void addField(MultiFieldDeclare multiFieldDeclare){
        multiFieldDeclares.add(multiFieldDeclare);
        nodes.add(multiFieldDeclare);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<MultiFieldDeclare> getMultiFieldDeclares() {
        return multiFieldDeclares;
    }

    public List<MethodDeclare> getMethodDeclares() {
        return methodDeclares;
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
