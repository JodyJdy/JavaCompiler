package compiler.parser;

import compiler.ast.ClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放已经加载的类
 *  类名称 = package + classname
 */
public class ClassesLoader {
    private static Map<String, ClassNode> loadedClass = new ConcurrentHashMap<>(2);

    static void addClass(String className, ClassNode classNode){
        loadedClass.put(className,classNode);
    }
    public static ClassNode getClass(String className){
        return loadedClass.get(className);

    }
    public static boolean loadedClass(String className){
        return loadedClass.containsKey(className);
    }
    public static List<String> listClasses(String pattern){
        //匹配多个类
        String multiMatch = "*";
        if(pattern.endsWith(multiMatch)){
           pattern = pattern.replaceAll("\\.\\*","");
        }
        final String patt = pattern;
        List<String> result = new ArrayList<>();
        loadedClass.keySet().forEach(k->{
            if(k.startsWith(patt)){
                result.add(k);
            }
        });
        return result;
    }
}
