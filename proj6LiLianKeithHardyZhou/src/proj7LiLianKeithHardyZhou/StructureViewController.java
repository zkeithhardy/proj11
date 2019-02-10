/*
 * File: StructureViewController.java
 * CS361 Project 6
 * Names: Melody Mao, Zena Abulhab, Yi Feng, and Evan Savillo
 * Date: 10/27/2018
 * ---------------------------
 * Edited By: Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * Project 7
 * Date: November 3, 2018
*/


package proj7LiLianKeithHardyZhou;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import proj7LiLianKeithHardyZhou.Java8.*;

import java.util.HashMap;
import java.util.Map;


/**
 * Controller that manages the generation and display of the structure of the
 * java code in the file currently being viewed.
 * @author Melody Mao, Zena Abulhab, Yi Feng, and Evan Savillo
 */
public class StructureViewController
{
    private Map<TreeItem, Integer> treeItemLineNumMap;
    private TreeView<String> treeView;
    private final ParseTreeWalker walker;

    /**
     * Constructor for this class
     * @param fileStructureTree the TreeView object passed by master controller
     *                          It holds all structure information
     */
    public StructureViewController(TreeView<String> fileStructureTree) {
        this.walker = new ParseTreeWalker();
        this.treeItemLineNumMap = new HashMap<>();
        this.treeView = fileStructureTree;
    }

    /**
     * Parses a file thereby storing contents as TreeItems in our special tree.
     * @param fileContents the file to be parsed
     */
    public void generateStructureTree(String fileContents)
    {
        TreeItem<String> newRoot = new TreeItem<>(fileContents);

        //build lexer, parser, and parse tree for the given file
        Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(fileContents));
        lexer.removeErrorListeners();

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        parser.removeErrorListeners();

        ParseTree tree = parser.compilationUnit();

        //walk through parse tree with listening for code structure elements
        CodeStructureListener codeStructureListener = new CodeStructureListener(newRoot, this.treeItemLineNumMap);
        this.walker.walk(codeStructureListener, tree);

        this.setRootNode(newRoot);
    }

    /**
     * Sets the currently displaying File TreeItem<String> View.
     *
     * @param root root node corresponding to currently displaying file
     */
    private void setRootNode(TreeItem<String> root)
    {
        this.treeView.setRoot(root);
        this.treeView.setShowRoot(false);
    }

    /**
     * Sets the currently displaying file to nothing.
     */
    public void resetRootNode()
    {
        this.setRootNode(null);
    }

    /**
     * Returns the line number currently associated with the specified tree item
     *
     * @param treeItem Which TreeItem to get the line number of
     * @return the line number corresponding with that tree item
     */
    public Integer getTreeItemLineNum(TreeItem treeItem) {
        return this.treeItemLineNumMap.get(treeItem);
    }

    /**
     * Private helper class that listens for code structure declarations
     * (classes, fields, methods) during a parse tree walk and builds a
     * TreeView subtree representing the code structure.
     */
    private class CodeStructureListener extends Java8BaseListener
    {
        private Image classPic;
        private Image methodPic;
        private Image fieldPic;
        private TreeItem<String> currentNode;
        private Map<TreeItem, Integer> treeItemIntegerMap;

        /**
         * creates a new CodeStructureListener that builds a subtree
         * from the given root TreeItem
         *
         * @param root root TreeItem to build subtree from
         * @param treeItemIntegerMap a map class that maps the tree item to the line number
         */
        public CodeStructureListener(TreeItem<String> root, Map<TreeItem, Integer> treeItemIntegerMap)
        {
            this.currentNode = root;
            this.treeItemIntegerMap = treeItemIntegerMap;
            this.classPic = new Image(getClass().getResource("resources/c.png").toExternalForm());
            this.methodPic = new Image(getClass().getResource("resources/m.png").toExternalForm());
            this.fieldPic = new Image(getClass().getResource("resources/f.png").toExternalForm());
        }

        /**
         * Starts a new subtree for the class declaration entered
         * @param declarationContext the context class for the declared context
         */
        @Override
        public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext declarationContext)
        {
            //get class name
            TerminalNode node = declarationContext.Identifier();
            if(node!=null) {
                String className = node.getText();

                //add class to TreeView under the current class tree
                //set up the icon
                //store the line number of its declaration
                TreeItem<String> newNode = new TreeItem<>(className);
                newNode.setGraphic(new ImageView(this.classPic));
                newNode.setExpanded(true);
                this.currentNode.getChildren().add(newNode);
                this.currentNode = newNode; //move current node into new subtree
                this.treeItemIntegerMap.put(newNode, declarationContext.getStart().getLine());
            }
        }

        /**
         * ends the new subtree for the class declaration exited,
         * returns traversal to parent node
         * @param declarationContext the context class for the declared context
         */
        @Override
        public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext declarationContext)
        {
            this.currentNode = this.currentNode.getParent(); //move current node back to parent
        }

        /**
         * adds a child node for the field entered under the TreeItem for the current class
         * @param fieldDeclarationContext the field context class for the declared context
         */
        @Override
        public void enterFieldDeclaration(Java8Parser.FieldDeclarationContext fieldDeclarationContext)
        {
            //get field name
            TerminalNode node = fieldDeclarationContext.variableDeclaratorList().variableDeclarator(0).variableDeclaratorId().Identifier();
            String fieldName = node.getText();

            //add field to TreeView under the current class tree
            //set up the icon
            //store the line number of its declaration
            TreeItem<String> newNode = new TreeItem<>(fieldName);
            newNode.setGraphic(new ImageView(this.fieldPic));
            this.currentNode.getChildren().add(newNode);
            this.treeItemIntegerMap.put(newNode, fieldDeclarationContext.getStart().getLine());
        }

        /**
         * adds a child node for the method entered under the TreeItem for the current class
         * @param headerDeclarationContext the header context class for the declared context
         */
        @Override
        public void enterMethodHeader(Java8Parser.MethodHeaderContext headerDeclarationContext)
        {
            //get method name
            TerminalNode nameNode = headerDeclarationContext.methodDeclarator().Identifier();
            String methodName = nameNode.getText();

            //add method to TreeView under the current class tree
            //set up the icon
            //store the line number of its declaration
            TreeItem<String> newNode = new TreeItem<>(methodName);
            newNode.setGraphic(new ImageView(this.methodPic));
            this.currentNode.getChildren().add(newNode);
            this.treeItemIntegerMap.put(newNode, headerDeclarationContext.getStart().getLine());

        }
    }
}