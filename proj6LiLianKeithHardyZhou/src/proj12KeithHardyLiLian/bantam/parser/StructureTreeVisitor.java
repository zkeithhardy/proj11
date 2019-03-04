package proj12KeithHardyLiLian.bantam.parser;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Map;

public class StructureTreeVisitor extends Visitor {
    private TreeItem<String> currentNode;
    private Map<TreeItem, Integer> treeItemIntegerMap;
    private Image classPic;
    private Image methodPic;
    private Image fieldPic;

    public TreeItem<String> buildStructureTree(TreeItem<String> currentNode, Program root, Map<TreeItem, Integer> treeItemIntegerMap){
        this.currentNode=currentNode;
        this.treeItemIntegerMap=treeItemIntegerMap;
        this.classPic = new Image(getClass().getResource("../resources/c.png").toExternalForm());
        this.methodPic = new Image(getClass().getResource("../resources/m.png").toExternalForm());
        this.fieldPic = new Image(getClass().getResource("../resources/f.png").toExternalForm());

        root.accept(this);
        return currentNode;
    }


    @Override
    public Object visit(Class_ node) {
        TreeItem<String> newNode=buildNodeAndUpdate(node.getName(), node );
        newNode.setGraphic(new ImageView(this.classPic));
        newNode.setExpanded(true);
        this.currentNode=newNode;

        //mimic exitNormalClassDeclaration
        super.visit(node);
        this.currentNode=this.currentNode.getParent();
        return null;
    }


    @Override
    public Object visit(Field node) {
        TreeItem<String> newNode=buildNodeAndUpdate(node.getName(), node );
        newNode.setGraphic(new ImageView(this.fieldPic));
        return null;
    }

    @Override
    public Object visit(Method node) {
        TreeItem<String> newNode=buildNodeAndUpdate(node.getName(), node );
        newNode.setGraphic(new ImageView(this.methodPic));
        return null;
    }

    private TreeItem<String> buildNodeAndUpdate( String name, ASTNode node){
        TreeItem<String> newNode= new TreeItem<>(name);
        this.currentNode.getChildren().add(newNode);
        this.treeItemIntegerMap.put(newNode, node.getLineNum());
        return newNode;
    }
}
