

package compiler;

import compiler.ast.ClassNode;
import compiler.ast.MethodDeclare;
import compiler.ir.IRGenerator;
import compiler.lexer.Lexer;
import compiler.parser.PackageLoader;
import compiler.parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 **/

public class Main {

    public static void main(String[] args) {
        String classPath = "";
        loadPackage(classPath, null);
        classPreProcess(classPath);
        classProcess();
    }


    /**
     * 加载包信息，从classPath开始处理时， p 为null
     */
    private static void loadPackage(String classPath, PackageLoader.Package p) {
        File dir = new File(classPath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File d : files) {
                    if (d.isDirectory()) {
                        PackageLoader.Package tempP = new PackageLoader.Package(d.getName());
                        if (p == null) {
                            PackageLoader.addPackage(tempP);
                        } else {
                            p.addPackage(tempP);
                        }
                        loadPackage(d.getAbsolutePath(), tempP);
                    }
                }
            }
        }
    }

    private static void preprocess(File f, boolean isDefault) {
        if (f.isFile()) {
            try {
                Parser parser = new Parser(new Lexer(f));
                parser.classPreProcess(isDefault);
                parserList.add(parser);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            File[] files = f.listFiles();
            if (files != null) {
                for (File tempF : files) {
                    preprocess(tempF, isDefault);
                }
            }
        }
    }

    /**
     * 类信息预处理，将parser加入到列表中
     */
    private static void classPreProcess(String classPath) {
        File dir = new File(classPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    preprocess(f, true);
                } else {
                    preprocess(f, false);
                }
            }
        }
    }

    private static List<Parser> parserList = new ArrayList<>();

    /**
     * 类信息处理
     */
    private static void classProcess() {
        parserList.forEach((parser -> {
            parser.clazz();
            //编译字节码
            ClassNode node = parser.getClassNode();
            IRGenerator irGenerator = new IRGenerator(node);
            for (MethodDeclare methodDeclare : node.getClassBody().getMethodDeclares()) {
                if (methodDeclare.getStmt() != null) {
                    irGenerator.visit(methodDeclare);
                }
            }
            if (node.getInstanceMethod() != null && node.getInstanceMethod().getStmt() != null) {
                irGenerator.visit(node.getInstanceMethod());
            }
            if (node.getStaticMethod() != null && node.getStaticMethod().getStmt() != null) {
                irGenerator.visit(node.getStaticMethod());
            }

        }));
    }

}