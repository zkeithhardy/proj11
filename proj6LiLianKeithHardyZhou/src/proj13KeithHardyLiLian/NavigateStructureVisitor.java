/*
 * File: NavigateStructureVisitor.java
 * CS461 Project 13
 * Names: Zeb Keith-Hardy, Michael Li, Iris Lian
 * Date: 3/5/19
 */

package proj13KeithHardyLiLian;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import proj13KeithHardyLiLian.bantam.ast.*;
import proj13KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Map;

public class NavigateStructureVisitor extends Visitor {
    private TreeItem<String> currentNode;
    private Map<TreeItem, Integer> treeItemIntegerMap;
    private Image classPic;
    private Image methodPic;
    private Image fieldPic;
    private boolean searchSymbol;
    private String target;

    /**
     * build a structure tree or search for a class/symbol
     * @param currentNode the tree item root
     * @param root the ast root
     * @param treeItemIntegerMap the map that stores the tree items and line numbers
     * @param target the target name for class/symbol
     * @param searchSymbol whether is called to search for a symbol
     * @return a tree item root
     */
    public TreeItem<String> buildOrNavigateStructureTree(TreeItem<String> currentNode, Program root,
                                                         Map<TreeItem, Integer> treeItemIntegerMap, String target,
                                                         boolean searchSymbol){
        this.currentNode = currentNode;
        this.treeItemIntegerMap = treeItemIntegerMap;
        this.classPic = new Image(getClass().getResource("resources/c.png").toExternalForm());
        this.methodPic = new Image(getClass().getResource("resources/m.png").toExternalForm());
        this.fieldPic = new Image(getClass().getResource("resources/f.png").toExternalForm());
        this.searchSymbol = searchSymbol;
        this.target = target;

        root.accept(this);
        return currentNode;
    }


    @Override
    public Object visit(Class_ node) {
        // if is building structure view tree or searching for symbols or searching for a class which has a match
        if (target == null || searchSymbol || node.getName().equals(target)){
            TreeItem<String> newNode=buildNodeAndUpdate(node.getName(), node );
            newNode.setGraphic(new ImageView(this.classPic));
            newNode.setExpanded(true);
            this.currentNode=newNode;
        }

        // if not building structure view tree and is searching for symbols
        if (target == null || searchSymbol) {
            super.visit(node);
            if(this.currentNode.getChildren().isEmpty()){
                TreeItem<String> removedNode = this.currentNode;
                this.currentNode = this.currentNode.getParent();
                this.currentNode.getChildren().remove(removedNode);
                this.treeItemIntegerMap.remove(removedNode);
            }else{
                this.currentNode = this.currentNode.getParent();
            }

        }
        return null;
    }


    @Override
    public Object visit(Field node) {
        if(target == null || node.getName().equals(target)) {
            TreeItem<String> newNode = buildNodeAndUpdate(node.getName(), node);
            newNode.setGraphic(new ImageView(this.fieldPic));
        }
        return null;
    }

    @Override
    public Object visit(Method node) {
        if(target == null || node.getName().equals(target)) {
            TreeItem<String> newNode = buildNodeAndUpdate(node.getName(), node);
            newNode.setGraphic(new ImageView(this.methodPic));
        }
        return null;
    }

    private TreeItem<String> buildNodeAndUpdate( String name, ASTNode node){
        TreeItem<String> newNode= new TreeItem<>(name);
        this.currentNode.getChildren().add(newNode);
        this.treeItemIntegerMap.put(newNode, node.getLineNum());
        return newNode;
    }
}
