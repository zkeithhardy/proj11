/*
 * File: NavigationController.java
 * CS461 Project 13
 * Names: Zeb Keith-Hardy, Michael Li, Iris Lian
 * Date: 3/5/19
 */

package proj19KeithHardyLiLian;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import proj19KeithHardyLiLian.bantam.ast.*;
import proj19KeithHardyLiLian.bantam.parser.Parser;
import proj19KeithHardyLiLian.bantam.util.CompilationException;
import proj19KeithHardyLiLian.bantam.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * this the the navigation controller class that handles all features
 * this tabpane. The features include find certain class/field and find
 * the superclass of a existing class.
 * @author Michael Li, Iris Lian, Zeb Keith-Hardy
 */
public class NavigationController {
    private CodeTabPane codeTabPane;
    private Map<TreeItem,Integer> treeItemLineNumMap;
    private Console console;
    private boolean searchSymbol;
    private String searchTarget;

    public NavigationController(CodeTabPane codeTabPane,Console console){
        this.codeTabPane = codeTabPane;
        this.treeItemLineNumMap = new HashMap<>();
        this.console = console;
    }

    /**
     * Finds a symbol or class in the current code area based on which option the user clicked on.
     */
    private void handleFindClassOrSymbol(){
        new Thread (()->{
            StructureTreeTask structureTreeTask = new StructureTreeTask();
            FutureTask<TreeItem> curFutureTask = new FutureTask<TreeItem>(structureTreeTask);
            ExecutorService curExecutor = Executors.newFixedThreadPool(1);
            curExecutor.execute(curFutureTask);
            try{
                TreeItem<String> newRoot = curFutureTask.get();
                Platform.runLater(()-> this.showResultDialog(newRoot));
            }catch(InterruptedException| ExecutionException e){
                Platform.runLater(()-> this.console.writeToConsole("Parsing failed \n", "Error"));
            }
        }).start();


    }

    /**
     * Asks the user what they want to search for in  a dialog box
     * @param title Title for dialog box
     * @param searchSymbol boolean for searching for class or symbol
     */
    public void getSearchValue(String title,boolean searchSymbol){
        this.searchSymbol=searchSymbol;
        Dialog<ButtonType> searchDialog = new Dialog<>();
        searchDialog.setTitle(title);
        ButtonType searchButton = new ButtonType("Search");
        ButtonType cancelButton = ButtonType.CANCEL;
        searchDialog.getDialogPane().getButtonTypes().addAll(searchButton,cancelButton);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField searchField = new TextField();
        gridPane.add(new Label("Search:"), 0, 0);
        gridPane.add(searchField, 0, 1);

        searchDialog.getDialogPane().setContent(gridPane);


        final Button findButton = (Button) searchDialog.getDialogPane().lookupButton(searchButton);
        findButton.addEventFilter(ActionEvent.ACTION, event -> {
            this.searchTarget = searchField.getText();
            searchDialog.close();
            this.handleFindClassOrSymbol();
            event.consume();
        });

        searchDialog.showAndWait();
    }

    /**
     * places result of AST search in a dialog box and allows the user to move to one of the results
     * @param root root of tree
     */
    private void showResultDialog(TreeItem<String> root){
        Dialog<ButtonType> treeItemDialog = new Dialog<>();

        TreeView treeView = new TreeView();

        treeView.setRoot(root);
        treeView.setShowRoot(false);

        treeItemDialog.setTitle("Results");
        treeItemDialog.getDialogPane().setContent(treeView);
        ButtonType goToButton = new ButtonType("GoTo");
        treeItemDialog.getDialogPane().getButtonTypes().add(goToButton);

        final Button goButton = (Button) treeItemDialog.getDialogPane().lookupButton(goToButton);
        goButton.addEventFilter(ActionEvent.ACTION, event -> {
            if(treeView.getSelectionModel().getSelectedItem() != null){
                int lineNum = this.treeItemLineNumMap.get((TreeItem) treeView.getSelectionModel().getSelectedItem());
                treeItemDialog.close();

                this.codeTabPane.getCodeArea().showParagraphAtTop(lineNum - 1);

            }
            event.consume();
        });

        treeItemDialog.showAndWait();
    }

    /**
     * Finds the super class for the highlighted class.
     * Implementation to open the Object class not implemented yet, Object class does not yet exist
     * Once we have Object class, will add this implementation
     */
    public void getSuperClass(){
        new Thread (()->{
            SuperClassTask superClassTask = new SuperClassTask();
            FutureTask<Integer> curFutureTask = new FutureTask<Integer>(superClassTask);
            ExecutorService curExecutor = Executors.newFixedThreadPool(1);
            curExecutor.execute(curFutureTask);
            try{
                Integer lineNum = curFutureTask.get();
                if(lineNum != null){
                    Platform.runLater(()-> this.codeTabPane.getCodeArea().showParagraphAtTop(lineNum -1));
                }
            }catch(InterruptedException| ExecutionException e){
                Platform.runLater(()-> this.console.writeToConsole("Parsing failed \n", "Error"));
            }
        }).start();

    }

    /**
     * An inner class used to parse a file in a separate thread.
     * Prints error info to the console
     */
    private class StructureTreeTask implements Callable {

        /**
         * Create a Parser and use it to create an AST
         * Search the AST for a symbol or class
         * @return The tree to be displayed
         */
        @Override
        public TreeItem call(){
            ErrorHandler errorHandler = new ErrorHandler();
            Parser parser = new Parser(errorHandler);
            String filename = NavigationController.this.codeTabPane.getFileName();
            Program AST = null;
            TreeItem<String> newRoot = new TreeItem<>(filename);
            try{

                AST = parser.parse(filename);
                NavigateStructureVisitor structureViewVisitor = new NavigateStructureVisitor();
                newRoot = structureViewVisitor.buildOrNavigateStructureTree(newRoot, AST,
                        NavigationController.this.treeItemLineNumMap, NavigationController.this.searchTarget,
                        NavigationController.this.searchSymbol);
            }
            catch (CompilationException e){
                Platform.runLater(()-> {
                    NavigationController.this.console.writeToConsole(
                            "Could not parse file to find Class or Symbol", "Error");
                });
            }
            return newRoot;
        }
    }

    /**
     * An inner class used to parse a file in a separate thread.
     * Prints error info to the console
     */
    private class SuperClassTask implements Callable {

        /**
         * Create a Parser and use it to create an AST
         * Search the AST for a symbol or class
         * @return The tree to be displayed
         */
        @Override
        public Integer call(){
            ErrorHandler errorHandler = new ErrorHandler();
            Parser parser = new Parser(errorHandler);
            String filename = NavigationController.this.codeTabPane.getFileName();
            Program AST = null;
            int lineNum = NavigationController.this.codeTabPane.getCodeArea().getCaretPosition();
            String highlightedText = NavigationController.this.codeTabPane.getCodeArea().getSelectedText();
            Integer superClassLineNum = lineNum;
            try{
                AST = parser.parse(filename);
                SuperClassVisitor superClassVisitor = new SuperClassVisitor();
                superClassLineNum = superClassVisitor.findSuperClass(AST,highlightedText);
            }
            catch (CompilationException e){
                Platform.runLater(()-> {
                    NavigationController.this.console.writeToConsole(
                            "Could not parse file to find Super Class", "Error");
                });
            }
            return superClassLineNum;
        }
    }
}
