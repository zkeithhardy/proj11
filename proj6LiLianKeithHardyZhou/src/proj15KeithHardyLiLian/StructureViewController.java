/*
 * File: StructureViewController.java
 * CS361 Project 6
 * Names: Melody Mao, Zena Abulhab, Yi Feng, and Evan Savillo
 * Date: 10/27/2018
 * ---------------------------
 * Edited By: Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * Project 6/7/9
 * Date: October 26, 2018/ November 3, 2018/ November 20, 2018
*/

package proj15KeithHardyLiLian;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import javafx.scene.input.KeyEvent;

import org.fxmisc.richtext.CodeArea;
import proj15KeithHardyLiLian.bantam.ast.Program;
import proj15KeithHardyLiLian.bantam.parser.*;
import proj15KeithHardyLiLian.bantam.util.ErrorHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


/**
 * Controller that manages the generation and display of the structure of the
 * java code in the file currently being viewed.
 * @author Melody Mao, Zena Abulhab, Yi Feng, and Evan Savillo
 * @author Modified by Zeb Keith-Hardy, Michael Li, and Iris Lian.
 */
public class StructureViewController
{
    private Map<TreeItem, Integer> treeItemLineNumMap;
    private CodeTabPane codeTabPane;
    private TreeView<String> treeView;
    private TreeItem<String> structureTreeRoot;
    private ErrorHandler errorHandler;
    private Console console;


    /**
     * Constructor for this class
     * @param fileStructureTree the TreeView object passed by master controller
     *                          It holds all structure information
     */
    public StructureViewController(TreeView<String> fileStructureTree, CodeTabPane codeTabPane, Console console) {
        this.codeTabPane=codeTabPane;
        this.treeItemLineNumMap = new HashMap<>();
        this.treeView = fileStructureTree;
        this.console = console;

        //update the file structure view when user change tab
        this.codeTabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            this.updateStructureView();
        });

        this.errorHandler = new ErrorHandler();
    }

    /**
     * Parses a file thereby storing contents as TreeItems in our special tree.
     * @param fileName the file to be parsed
     */
    public TreeItem<String> generateStructureTree(String fileName)
    {
        TreeItem<String> newRoot = new TreeItem<>(fileName);
        errorHandler.clear();
        Parser parser = new Parser(errorHandler);
        Program ast = parser.parse(fileName);

        if(!errorHandler.errorsFound()) {
            NavigateStructureVisitor structureViewVisitor = new NavigateStructureVisitor();
            newRoot = structureViewVisitor.buildOrNavigateStructureTree(newRoot, ast, this.treeItemLineNumMap,
                    null, true);

        }
        return newRoot;
    }

    /**
     * updates the Structure Tree by parsing the AST
     */
    public void updateStructureView(){
        //declare a new thread and assign it with the work of updating the structure view
        new Thread(()->{
            UpdateStructureViewTask updateStructViewTask = new UpdateStructureViewTask(this.codeTabPane,
                    this);
            FutureTask<TreeItem<String>> curFutureTask = new FutureTask<>(updateStructViewTask );
            ExecutorService curExecutor = Executors.newFixedThreadPool(1);
            curExecutor.execute(curFutureTask);
            //when the updating structure task is successfully done, close the current executor
            try {
                this.structureTreeRoot = curFutureTask.get();
                curExecutor.shutdown();
                //if the update failed, an dialog box will pops up reporting error
            }catch (InterruptedException| ExecutionException e){
                Platform.runLater(()-> {
                    this.console.writeToConsole("","Error");
                });
            }
            //set the root node of the current structure view to the new root node
            Platform.runLater(()-> {
                this.setRootNode(this.structureTreeRoot);
            });
        }).start();
    }

    /**
     * Sets the currently displaying File TreeItem<String> View.
     *
     * @param root root node corresponding to currently displaying file
     */
    public void setRootNode(TreeItem<String> root)
    {
        this.treeView.setRoot(root);
        this.treeView.setShowRoot(false);
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
     * An inner class used for a thread to execute the run task
     * Designed to be used for compilation or running.
     * Writes the input/output error to the console.
     */
    private class UpdateStructureViewTask implements Callable<TreeItem<String>> {
        private CodeTabPane codeTabPane;
        private StructureViewController structureViewController;

        /**
         * Constructor for the task class that updates the structure view
         * @param codeTabPane the codeTabPane class to access the codearea and current file
         * @param structureViewController the structure view controller class to access the structure tree
         */

        public UpdateStructureViewTask(CodeTabPane codeTabPane,
                                       StructureViewController structureViewController){
            this.codeTabPane=codeTabPane;
            this.structureViewController = structureViewController;
        }

        /**
         * the run function that override the original ones offered by the runnable class
         * This one updates the structure view
         * @return the TreeItem root that indicates the file to be parsed
         */
        @Override
        public TreeItem<String> call(){
            CodeArea currentCodeArea=codeTabPane.getCodeArea();
            File currentFile=codeTabPane.getCurrentFile();
            // if the code area is open
            if (currentCodeArea != null) {
                // if this is not an unsaved file
                if (currentFile != null) {
                    String fileName = currentFile.getPath();
                    // if this is a bantam file
                    if (fileName.endsWith(".btm")) {
                        // Re-generates the tree
                        return structureViewController.generateStructureTree(fileName);
                    }
                }
            }
            return null;
        }

    }
}