

package compiler.parser;

import compiler.enums.Tag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放加载的包的信息
 **/

public class PackageLoader {

    private static Map<String,Package> packageMap = new ConcurrentHashMap<>();

    static {
        //将default加入到package里面去
        packageMap.put(Tag.DEFATULT.getStr(),new Package(Tag.DEFATULT.getStr()));
    }
    public static class Package{
        private String name;
        private Map<String,Package> subPackages = new ConcurrentHashMap<>();


        public boolean isEnd(){
            return subPackages.size() == 0;
        }

        public Package(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
        public  Package getPackage(String name){
            return subPackages.get(name);
        }
        public void addPackage(Package p){
            subPackages.put(p.getName(),p);
        }

    }

    public static void addPackage(Package p){
        packageMap.put(p.getName(),p);
    }
    public static Package getPackage(String name){
        return packageMap.get(name);
    }
    public static boolean containsPackage(String name){
        return packageMap.containsKey(name);
    }

}
