/**
 * File: ClassMapBuilderVisitor
 * User: djskrien
 * Date: 1/2/14
 */

package proj19KeithHardyLiLian.bantam.semant;

import proj19KeithHardyLiLian.bantam.ast.Class_;
import proj19KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj19KeithHardyLiLian.bantam.util.ErrorHandler;
import proj19KeithHardyLiLian.bantam.util.Error;
import proj19KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Hashtable;

/**
 * This class visits the AST to find all class declarations and add an entry
 * for each of those classes in the classMap.
 */
public class ClassMapBuilderVisitor extends Visitor {
    private Hashtable<String, ClassTreeNode> classMap;
    private ErrorHandler errorHandler;

    ClassMapBuilderVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler
            errorHandler) {
        this.classMap = classMap;
        this.errorHandler = errorHandler;
    }

    /**
     * adds a new ClassTreeNode for this node to the classMap.
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node) {
        if(classMap.keySet().contains(node.getName()))
            errorHandler.register(Error.Kind.SEMANT_ERROR,node.getFilename(),
                    node.getLineNum(),"Two classes declared with the same name; " +
                            node.getName());
        else if(SemanticAnalyzer.reservedIdentifiers.contains(node.getName()))
            errorHandler.register(Error.Kind.SEMANT_ERROR,node.getFilename(),
                    node.getLineNum(),"A class cannot be named 'this', 'super'," +
                            "'void', 'int', 'boolean', or 'null'; " +
                            node.getName());
        else {
            ClassTreeNode treeNode = new ClassTreeNode(node, false, true, classMap);
            classMap.put(node.getName(), treeNode);
        }
        return null;
    }
}
