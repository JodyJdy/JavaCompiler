

package compiler.check;

import compiler.ast.Type;

/**
 * 用来类型检查
 */
public interface Checker {

    Type check(TypeChecker checker);

}
