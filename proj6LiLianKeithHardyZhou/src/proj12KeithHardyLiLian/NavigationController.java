package proj12KeithHardyLiLian;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.parser.Parser;
import proj12KeithHardyLiLian.bantam.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

public class NavigationController {
    private CodeTabPane codeTabPane;
    private Map<TreeItem,Integer> treeItemLineNumMap;
    private ErrorHandler errorHandler;

    public NavigationController(CodeTabPane codeTabPane){
        this.codeTabPane = codeTabPane;
        this.treeItemLineNumMap = new HashMap<>();
        this.errorHandler = new ErrorHandler();
    }

    /**
     * Handles Class Finder
     */
    public void handleFindClass(String className){
        TreeItem<String> newRoot = new TreeItem<>(this.codeTabPane.getFileName());

        errorHandler.clear();
        Parser parser = new Parser(errorHandler);
        Program ast = parser.parse(this.codeTabPane.getFileName());
        if(!errorHandler.errorsFound()){
            NavigateStructureVisitor navigateStructureVisitor = new NavigateStructureVisitor();
            newRoot = navigateStructureVisitor.buildOrNavigateStructureTree(newRoot,ast,this.treeItemLineNumMap,
                    className,true);
        }
        this.showResultDialog(newRoot);
    }

    /**
     * Handles Symbol Finder
     */
    public void handleFindSymbol(String symbolName){
        TreeItem<String> newRoot = new TreeItem<>(this.codeTabPane.getFileName());

        errorHandler.clear();
        Parser parser = new Parser(errorHandler);
        Program ast = parser.parse(this.codeTabPane.getFileName());
        if(!errorHandler.errorsFound()){
            NavigateStructureVisitor navigateStructureVisitor = new NavigateStructureVisitor();
            newRoot = navigateStructureVisitor.buildOrNavigateStructureTree(newRoot,ast,this.treeItemLineNumMap,
                    symbolName,true);
            this.showResultDialog(newRoot);
        }


    }

    public String getSearchValue(String title,boolean searchClass){
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
            String searchText = searchField.getText();
            searchDialog.close();
            if(searchClass){
                this.handleFindClass(searchText);
            }
            else{
                this.handleFindSymbol(searchText);
            }
            event.consume();
        });

        searchDialog.showAndWait();
        return null;
    }

    public void showResultDialog(TreeItem<String> root){
        Dialog<TreeView> treeItemDialog = new Dialog<>();

        TreeView treeView = new TreeView();
        treeView.setRoot(root);

        treeItemDialog.setTitle("Results");
        treeItemDialog.getDialogPane().setContent(treeView);
    }
}
