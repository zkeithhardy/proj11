/*
 * File: MasterController.java
 * Names: Kevin Ahn, Matt Jones, Jackie Hang, Kevin Zhou
 * Class: CS 361
 * Project 4
 * Date: October 2, 2018
 * ---------------------------
 * Edited By: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 * ---------------------------
 * Edited By: Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * Project 6/7
 * Date: October 26, 2018/November 3, 2018
 */

package proj7LiLianKeithHardyZhou;

import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.event.Event;
import org.fxmisc.richtext.CodeArea;

import java.io.File;

import java.util.Optional;


/**
 * This is the master controller for the program. it references
 * the other controllers for proper menu functionality.
 *
 * @author  Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * @author  Kevin Ahn, Jackie Hang, Matt Jones, Kevin Zhou
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * @version 2.0
 * @since   10-3-2018
 */
public class MasterController {
    @FXML private Menu editMenu;
    @FXML private TabPane tabPane;
    @FXML private VBox vBox;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem saveAsMenuItem;
    @FXML private MenuItem closeMenuItem;
    @FXML private Console console;
    @FXML private Button stopButton;
    @FXML private Button compileButton;
    @FXML private Button compileRunButton;
    @FXML private TreeView<String> directoryTree;
    @FXML private TreeView<String> fileStructureTree;

    private EditController editController;
    private FileController fileController;
    private ToolbarController toolbarController;
    private PreferenceController preferenceController;
    private DirectoryController directoryController;
    private HelpMenuController helpMenuController;
    private StructureViewController structureViewController;

    /**
     * Initializes all the controllers and binds some properties
     */
    public void initialize(){
        //initialize the popup menus that are clicked on CodeArea, tab, and console
        CodeAreaContextMenu codeAreaContextMenu = new CodeAreaContextMenu(this);
        TabContextMenu tabContextMenu = new TabContextMenu(this);
        ConsoleContextMenu consoleContextMenu = new ConsoleContextMenu(this);

        //initialize the controllers
        this.editController = new EditController(this.tabPane);
        this.fileController = new FileController(this.vBox,this.tabPane,this,
                codeAreaContextMenu, tabContextMenu);
        this.toolbarController = new ToolbarController(this.console,this.stopButton,this.compileButton,
                this.compileRunButton,this.tabPane);

        this.preferenceController = new PreferenceController(this.vBox,this.console);
        this.helpMenuController = new HelpMenuController();

        this.directoryController = new DirectoryController(this.directoryTree,this.tabPane,
                this.fileController.getFileNames());
        this.structureViewController=new StructureViewController(this.fileStructureTree);
        this.setupStructureViewListener();


        //bind the edit, save, saveAs, close menus to the property of a list of opened tabs
        SimpleListProperty<Tab> tabsProperty = new SimpleListProperty<Tab> (this.tabPane.getTabs());

        this.editMenu.disableProperty().bind(tabsProperty.emptyProperty());
        this.saveMenuItem.disableProperty().bind(tabsProperty.emptyProperty());
        this.saveAsMenuItem.disableProperty().bind(tabsProperty.emptyProperty());
        this.closeMenuItem.disableProperty().bind(tabsProperty.emptyProperty());

        //disable Toolbar items to start and pass controllers and context menu to console
        this.disableToolbar();
        this.console.setToolbarController(this.toolbarController);
        this.console.setContextMenu(consoleContextMenu);
    }

    /**
     * Handler for the Compile in the toolbar. Calls the helper function
     * handleCompileOrCompileRun with a string indicating its identity.
     */
    @FXML public void handleCompile(){
        this.handleCompileOrCompileRun("compile");
    }

    /**
     * Handler for the Compile and Run button in the toolbar.
     * Calls the helper function handleCompileOrCompileRun with a
     * string indicating its identity.
     */
    @FXML public void handleCompileAndRun() {
        this.handleCompileOrCompileRun("compileRun");
    }

    /**
     * Handler for the Stop button in the toolbar.
     * Calls the handleStop() method from Toolbar Controller and re-enables the toolbar buttons.
     */
    @FXML public void handleStop(){
        toolbarController.handleStop();
        if(this.tabPane.getTabs().isEmpty()) {
            this.stopButton.setDisable(true);
            return;
        }
        toolbarController.enableCompRun();
    }

    /**
     * Handler for the "About" menu item in the "File" menu.
     * Creates an Information alert dialog to display author and information of this program
     */
    @FXML public void handleAbout() { this.helpMenuController.handleAbout(); }

    /**
     * Handler for the "New" menu item in the "File" menu.
     * Adds a new Tab to the TabPane, and also adds null to the HashMap
     * Also sets the current tab for both the file and edit controllers.
     */
    @FXML public void handleNew() {
        fileController.handleNew();
        if(!toolbarController.getTaskStatus()) {
            toolbarController.enableCompRun();
        }
        this.updateStructureView();
    }

    /**
     * Handler for the "Open" menu item in the "File" menu.
     * Creates a FileChooser to select a file
     * Use scanner to read the file and write it into a new tab.
     * Also sets the current tab for both the file and edit controllers.
     */
    @FXML public void handleOpen() {
        File file = fileController.handleOpenDialog();
        fileController.handleOpen(file);
        if(!toolbarController.getTaskStatus() && !this.tabPane.getTabs().isEmpty()) {
            toolbarController.enableCompRun();
        }
        this.updateStructureView();
    }

    /**
     * Handler for the "Close" menu item in the "File" menu.
     * Checks to see if the file has been changed since the last save.
     * If changes have been made, redirect to askSave and then close the tab.
     * Otherwise, just close the tab.
     */
    @FXML public void handleClose(Event event) {
        fileController.handleClose(event);
        if (this.tabPane.getTabs().isEmpty()&&!toolbarController.getTaskStatus()){
            disableToolbar();
        }
    }

    /**
     * Handler for the "Save" menu item in the "File" menu.
     * If the current tab has been saved before, writes out the content to its corresponding
     * file in storage.
     * Else if the file has never been saved, opens a pop-up window that allows the user to
     * choose a filename and directory and then store the content of the tab to storage.
     */
    @FXML public void handleSave() {
        fileController.handleSave();
    }

    /**
     * Handler for the "Save as..." menu item in the "File" menu.
     * Opens a pop-up window that allows the user to choose a filename and directory.
     * Calls writeFile to save the file to memory.
     * Changes the name of the current tab to match the newly saved file's name.
     */
    @FXML public void handleSaveAs( ) {
        fileController.handleSaveAs();
    }

    /**
     * Handler for the "Exit" menu item in the "File" menu.
     * Closes all the tabs using handleClose()
     * Returns when the user cancels exiting any tab.
     * @param event an event
     */
    @FXML public void handleExit(Event event) {
        toolbarController.handleStop();
        fileController.handleExit(event);
    }

    /**
     * Handler for the "Undo" menu item in the "Edit" menu.
     */
    @FXML
    public void handleUndo() { editController.handleUndo(); }

    /**
     * Handler for the "Redo" menu item in the "Edit" menu.
     */
    @FXML
    public void handleRedo() {
        editController.handleRedo(); }

    /**
     * Handler for the "Cut" menu item in the "Edit" menu.
     */
    @FXML
    public void handleCut() {
        editController.handleCut(); }

    /**
     * Handler for the "Copy" menu item in the "Edit" menu.
     */
    @FXML
    public void handleCopy() {
        editController.handleCopy();}

    /**
     * Handler for the "Paste" menu item in the "Edit" menu.
     */
    @FXML
    public void handlePaste() {
        editController.handlePaste(); }

    /**
     * Handler for the "Comment with Line Comments" menu item in the "Edit" menu.
     */
    @FXML
    public void handleLineComment(){
        editController.handleLineComment();
    }

    /**
     * Handler for the "Comment with Block Comments" menu item in the "Edit" menu.
     */
    @FXML
    public void handleBlockComment(){
        editController.handleBlockComment();
    }

    /**
     * Handler for the "SelectAll" menu item in the "Edit" menu.
     */
    @FXML
    public void handleSelectAll() {
        editController.handleSelectAll(); }

    /**
     * Handler for the "Entab" menu item in the "Edit" menu.
     */
    @FXML
    public void handleEntab() {
        editController.handleEntabOrDetab("entab");
    }

    /**
     * Handler for the "Detab" menu item in the "Edit" menu.
     */
    @FXML
    public void handleDetab() {
        editController.handleEntabOrDetab("detab");
    }

    /**
     * Handler for the "Find & Replace" menu item in the "Edit menu.
      */
    @FXML
    public void handleFindReplace(){
        editController.handleFindReplace();  }

    /**
     * Handler for the "NightMode" Toggle menu item in the "Preferences" Menu.
      */
    @FXML
    public void handleNightMode(){
        preferenceController.handleNightMode();
    }

    /**
     * Handler for the "Java Tutorial" menu item in the "Help" Menu.
     * When the item is clicked, a Java tutorial will be opened in a browser.
     */
    @FXML
    public void handleJavaTutorial(){
        this.helpMenuController.handleJavaTutorial();
    }

    /**
     * Calls the method that handles the Keyword color menu item from the PreferenceController.
     */
    @FXML public void handleKeywordColorAction() { this.preferenceController.handleColorAction("Keyword"); }

    /**
     * Calls the method that handles the Parentheses/Brackets color menu item from the PreferenceController.
     */
    @FXML public void handleParenColorAction() { this.preferenceController.handleColorAction("Paren"); }

    /**
     * Calls the method that handles the String color menu item from the PreferenceController.
     */
    @FXML public void handleStrColorAction() { this.preferenceController.handleColorAction("Str"); }

    /**
     * Calls the method that handles the Int color menu item from the PreferenceController.
     */
    @FXML public void handleIntColorAction() { this.preferenceController.handleColorAction("Int"); }

    /**
     * Jump to the line where the selected class/method/field is declared.
     */
    @FXML
    private void handleTreeItemClicked()
    {
        //get the selected tree item and get the codeArea it corresponds to
        TreeItem selectedTreeItem = (TreeItem) this.fileStructureTree.getSelectionModel().getSelectedItem();
        CodeArea currentCodeArea = editController.getCodeArea();

        //jump to the line in the codeArea where the selected class/method/field is declared
        if (selectedTreeItem != null)
        {
            int lineNum = this.structureViewController.getTreeItemLineNum(selectedTreeItem);
            if (currentCodeArea != null) currentCodeArea.showParagraphAtTop(lineNum - 1);
        }
    }

    /**
     * Event handler to open a file selected from the directory
     *
     * @param event a MouseEvent object
     */
    @FXML
    private void handleDirectoryItemClicked(MouseEvent event){
        // only open file if double clicked
        if (event.getClickCount() == 2 && !event.isConsumed()) {
            event.consume();
            TreeItem selectedItem = (TreeItem) directoryTree.getSelectionModel().getSelectedItem();

            //fixes bug where pressing arrow throws NullPointerException
            if(selectedItem == null){
                return;
            }
            //check if the selected file is a java file and open it if so
            String fileName = (String) selectedItem.getValue();
            if (fileName.endsWith(".java")) {
                this.fileController.handleOpen(this.directoryController.getTreeItemFileMap().get(selectedItem));
            }
        }
    }

    /**
     * Calls handleMatchBracketOrParen() of the editController
     */
    @FXML
    public void handleMatchBracketOrParen() {
        editController.handleMatchBracketOrParen();
    }

    /**
     * Set uo the event handler for Structure view panel so that the structureView is updated when
     * the user type key or change tab
     */
    private void setupStructureViewListener(){
        // Updates the file structure view whenever a key is typed
        this.tabPane.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            this.updateStructureView();
        });
        //update the file structure view when user change tab
        this.tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            this.updateStructureView();
        });
    }

    /**
     * Parses and generates the structure view for the currently open code area
     */
    public void updateStructureView() {
        CodeArea currentCodeArea = editController.getCodeArea();
        File currentFile = fileController.getCurrentFile();
        // if the code area is open
        if (currentCodeArea != null) {
            // if this is not an unsaved file
            if (currentFile != null) {
                String fileName = currentFile.getPath();
                // if this is a java file
                if (fileName.endsWith(".java")) {
                    // Re-generates the tree
                    this.structureViewController.generateStructureTree(currentCodeArea.getText());
                }
            }
            else {
                // Gets rid of open structure view
                this.structureViewController.resetRootNode();;
            }
        }
    }

    /**
     * close all tabs when Close All menu item is clicked
     * @param event an action event
     */
    public void handleCloseAll(Event event){
        fileController.handleCloseAll(event);
    }

    /**
     * clears the console
     */
    public void handleClearConsole(){
        this.console.clear();
    }

    /**
     * Pops up a dialog asking if the user wants to save the changes
     * and return a string indicating which button the user clicked
     * @param title the title of the dialog
     * @param header the header of the dialog
     * @param context the context of the dialog
     * @return a string indicating which button the user clicked
     */
    public String askSaveDialog(String title, String header, String context){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == yesButton){
            return "yesButton";
        }
        else if(result.get() == noButton){
            return "noButton";
        }
        else{
            return "cancelButton";
        }
    }

    /**
     * Disables the Compile, Compile and Run, and Stop buttons in the toolbar
     */
    private void disableToolbar(){
       this.compileButton.setDisable(true);
       this.compileRunButton.setDisable(true);
       this.stopButton.setDisable(true);
   }

    /**
     * If the user tries to compile a file with no previous saved version,
     * an alert pops up.
     */
    private void showAlertIfNoFile(){
        if (fileController.getFileName() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Cannot compile a file with no previous saved version.");
            alert.showAndWait();
        }
    }

    /**
     * Handler for the Compile and CompileRun in the toolbar.
     * Checks if the current file has been saved. If it has not,
     * prompts the user to save, if so, compiles or compiles and runs
     * the program.If user chooses not to save, compiles or compiles and runs
     * the latest version of the file.
     * @param method to compile or to compile and run
     */
    private void handleCompileOrCompileRun(String method){
        toolbarController.disableCompRun();
        if(fileController.getSaveStatus()) {
            toolbarController.startCompileOrCompileRun(fileController.getFileName(), method);
        } else {
            String saveResult = this.askSaveDialog(null,
                    "Do you want to save your changes?",null);
            switch (saveResult) {
                case("yesButton"):
                    fileController.handleSave();
                    toolbarController.startCompileOrCompileRun(fileController.getFileName(), method);
                    break;
                case("noButton"):
                    this.showAlertIfNoFile();
                    toolbarController.startCompileOrCompileRun(fileController.getFileName(), method);
                    break;
                default:
                    toolbarController.enableCompRun();
                    return;
            }
        }
        //If the user closes tab when compiling or compiling and running, the stop
        // button is not disabled in handleClose() immediately but disabled here after the
        // task is finished
        if(this.tabPane.getTabs().isEmpty()){
            this.disableToolbar();
        }
    }

    /**
     * Call the createDirectoryTree function in the directory controller
     */
    public void createDirectoryTree(){
        // Created for more elegant code (so that FileController doesn't have to
        // know DirectoryController)
        this.directoryController.createDirectoryTree();
    }
}
