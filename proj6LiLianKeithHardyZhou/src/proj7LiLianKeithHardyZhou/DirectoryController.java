/*
 * File: DirectoryController.java
 * CS361 Project 6
 * Names: Douglas Abrams, Martin Deutsch, Robert Durst, Matt Jones
 * Date: 10/27/2018
 * This file contains the DirectoryController class, handling the file directory portion of the GUI.
 * ---------------------------
 * Edited By: Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * Project 7
 * Date: November 3, 2018
 */
package proj7LiLianKeithHardyZhou;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.*;

/**
 * This controller handles directory related actions.
 *
 * @author Douglas Abrams, Martin Deutsch, Robert Durst, Matt Jones
 * @author Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 */
public class DirectoryController {
    private TreeView<String> directoryTree;
    //a HashMap mapping the tabs and the associated files
    private Map<Tab, File> tabFileMap;
    //A HashMap mapping the TreeItems and associated files
    private Map<TreeItem<String>, File> treeItemFileMap;
    private TabPane tabPane;

    /**
     * This is the constructor of DirectoryController.
     * @param directoryTree the directory tree with file structures
     * @param tabPane the tab pane
     * @param tabFileMap  the HashMap mapping the tabs and the associated files
     */
    public DirectoryController(TreeView<String> directoryTree, TabPane tabPane, Map<Tab, File> tabFileMap){
        this.directoryTree = directoryTree;
        this.tabFileMap = tabFileMap;
        this.treeItemFileMap = new HashMap<>();
        this.tabPane = tabPane;
        // add listener to tab selection to switch directories based on open file
        this.tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) ->
                this.createDirectoryTree());
    }

    /**
     * Returns the directory tree for the given file
     *
     * @param file the file
     * @return the root TreeItem of the tree
     */
    private TreeItem<String> getNode(File file) {
        // create root, which is returned at the end
        TreeItem<String> root = new TreeItem<>(file.getName());
        this.treeItemFileMap.put(root, file);

        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                // recursively traverse file directory
                root.getChildren().add(getNode(f));
            } else {
                TreeItem<String> leaf = new TreeItem<>(f.getName());
                root.getChildren().add(leaf);
                this.treeItemFileMap.put(leaf, f);
            }
        }
        return root;
    }

    /**
     * Adds the directory tree for the current file to the GUI
     */
    public void createDirectoryTree() {
        // capture current file
        File file = this.tabFileMap.get(this.tabPane.getSelectionModel().getSelectedItem());
        // create the directory tree
        if (file != null) {
            this.directoryTree.setRoot(this.getNode(file.getParentFile()));
            this.directoryTree.getRoot().setExpanded(true);
        }
    }

    /**
     * Returns the tree item map
     * @return the HashMap mapping the TreeItems and associated files
     */
    public Map<TreeItem<String>, File> getTreeItemFileMap(){
        return this.treeItemFileMap;
    }

}
