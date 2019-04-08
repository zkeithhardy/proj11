/*
 * File: ToolbarController.java
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
 * Project 6/7/9
 * Date: October 26, 2018/ November 3, 2018/ November 20, 2018
 *  ---------------------------
 * Edited By: Zeb Keith-Hardy, Danqing Zhao, Tia Zhang
 * Class: CS 461
 * Project 11
 * Date: February 13, 2019
 */

package proj16KeithHardyLiLian;

import javafx.application.Platform;
import javafx.scene.control.Button;
import proj16KeithHardyLiLian.bantam.ast.Program;
import proj16KeithHardyLiLian.bantam.codegenmips.MipsCodeGenerator;
import proj16KeithHardyLiLian.bantam.parser.Parser;
import proj16KeithHardyLiLian.bantam.semant.*;
import proj16KeithHardyLiLian.bantam.treedrawer.Drawer;
import proj16KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj16KeithHardyLiLian.bantam.util.CompilationException;
import proj16KeithHardyLiLian.bantam.util.ErrorHandler;
import proj16KeithHardyLiLian.bantam.util.Error;
import proj16KeithHardyLiLian.bantam.lexer.Scanner;
import proj16KeithHardyLiLian.bantam.lexer.Token;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * This class is the controller for all of the toolbar functionality.
 * Specifically, the compile, compile and run, and stop buttons
 *
 * @author  Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * @author  Kevin Ahn, Jackie Hang, Matt Jones, Kevin Zhou
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou
 * @version 2.0
 * @since   10-3-2018
 *
 */
public class ToolbarController {

    private FutureTask<Boolean> curFutureTask;
    private boolean scanIsDone;
    private boolean parseIsDone;
    private boolean checkIsDone;
    private boolean assembleSuccessful;
    private Console console;
    private CodeTabPane codeTabPane;
    private Button stopMipsButton;
    private Button assembleButton;
    private Button assembleRunButton;
    private Button scanButton;
    private Button scanParseButton;
    private Button checkButton;
    private Button compileButton;

    /**
     * This is the constructor of ToolbarController.
     * @param console the console
     * @param codeTabPane the tab pane
     */
    public ToolbarController(Console console, CodeTabPane codeTabPane, Button stopMipsButton,
                             Button assembleButton, Button assembleRunButton, Button scanButton, Button scanParseButton,
                             Button checkButton, Button compileButton){
        this.console = console;
        this.codeTabPane = codeTabPane;
        this.scanIsDone = true;
        this.parseIsDone = true;
        this.checkIsDone = true;
        this.stopMipsButton = stopMipsButton;
        this.assembleButton = assembleButton;
        this.assembleRunButton = assembleRunButton;
        this.scanButton = scanButton;
        this.scanParseButton = scanParseButton;
        this.checkButton = checkButton;
        this.compileButton = compileButton;

        this.codeTabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            this.enableCorrectButtons();
        });
    }

    /**
     * Handles actions for scan or scanParse buttons
     * @param method type of button clicked on the toolbar
     */
    public void handleScanOrScanParse(String method){
        switch (method){
            case "scan":
                this.handleScan();
                break;
            case "scanParse":
                this.handleParsing();
                break;
            default: //checker or generator
                this.handleCheckingOrCompiling(method);
                break;
        }
    }

    /**
     * Handles scanning the current CodeArea, prints results to a new code Area.
     */
    public void handleScan(){
        this.scanIsDone = false;
        //declare a new thread and assign it with the work of scanning the current tab
        new Thread(()-> {
            ScanTask scanTask = new ScanTask();
            FutureTask<String> curFutureTask = new FutureTask<>(scanTask);
            ExecutorService curExecutor = Executors.newFixedThreadPool(1);
            curExecutor.execute(curFutureTask);
        }).start();
    }

    /**
     * creates a new thread for parsing the AST.
     * Once AST is parsed, draws the AST
     */
    public void handleParsing(){
        this.parseIsDone = false;
        new Thread (()->{
            ParseTask parseTask = new ParseTask();
            FutureTask<Program> curFutureTask = new FutureTask<Program>(parseTask);
            ExecutorService curExecutor = Executors.newFixedThreadPool(1);
            curExecutor.execute(curFutureTask);
            try{
                Program AST = curFutureTask.get();
                if(AST != null){

                    // build AST image
                    Drawer drawer = new Drawer();
                    drawer.draw(this.codeTabPane.getFileName(),AST);
                }
                this.parseIsDone = true;
            }catch(InterruptedException| ExecutionException e){
                Platform.runLater(()-> this.console.writeToConsole("Parsing failed \n", "Error"));
            }
        }).start();
    }


    /**
     * creates a new thread for semantically analyzing the AST
     * Prints out all errors found by Semantic Analyzer
     * @param method
     */
    public void handleCheckingOrCompiling(String method){
        this.checkIsDone = false;
        new Thread (()->{
            CheckerCompileTask checkOrCompileTask = new CheckerCompileTask(method);
            FutureTask<ClassTreeNode> curFutureTask = new FutureTask<ClassTreeNode>(checkOrCompileTask);
            ExecutorService curExecutor = Executors.newFixedThreadPool(1);
            curExecutor.execute(curFutureTask);
            try{
                ClassTreeNode classTree = curFutureTask.get();
                this.checkIsDone = true;
//                ErrorHandler errorHandler = new ErrorHandler();
//
//                Platform.runLater(()-> {
//                    if (errorHandler.errorsFound()) {
//
//                        List<Error> errorList = errorHandler.getErrorList();
//                        Iterator<Error> errorIterator = errorList.iterator();
//                        ToolbarController.this.console.writeToConsole("\n", "Error");
//                        while (errorIterator.hasNext()) {
//                            ToolbarController.this.console.writeToConsole(errorIterator.next().toString() +
//                                    "\n", "Error");
//                        }
//
//                    }else{
//                        Platform.runLater(()->ToolbarController.this.console.writeToConsole(
//                                "Code Generation Successful.\n", "Output"));
//                    }
//                });

            }catch(InterruptedException| ExecutionException e){
                Platform.runLater(()-> this.console.writeToConsole("Semantic Analyzer failed: " + e.toString()
                                + "\n",
                        "Error"));
            }
        }).start();
    }

    /**
     * Start the thread to assemble or assemble&run the input file
     * @param method to assemble or to assemble and run
     */
    public void startAssembleOrAssembleRun(String method){
        Thread thread;
        if(method.equals("assemble"))
            thread = new Thread(()->handleAssemble());
        else
            thread = new Thread(()->handleAssembleRun());
        thread.start();
    }

    /**
     * handles Assembling MIPS code
     */
    public void handleAssemble(){
        // create assemble task and run it
        ProcessBuilder processBuilder = new ProcessBuilder("java","-jar", "include/Mars4_5.jar",
                "a", this.codeTabPane.getFileName());
        AssembleOrRunTask AssembleTask = new AssembleOrRunTask(this.console, processBuilder);
        this.curFutureTask = new FutureTask<Boolean>(AssembleTask);
        ExecutorService compileExecutor = Executors.newFixedThreadPool(1);
        compileExecutor.execute(this.curFutureTask);

        // Check if assemble was successful, and if so, indicate this in the console
        this.assembleSuccessful = false;
        try {
            this.assembleSuccessful = this.curFutureTask.get();
            if (this.assembleSuccessful) {
                Platform.runLater(() ->
                        this.console.writeToConsole("\nAssembly was Successful.\n",
                                "ProcessInfo"));
            }
            compileExecutor.shutdown();
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            AssembleTask.stop();
        }

        //The stop button would not be disabled when there is a process running
        //even if there is no tabs opened
        //This if statements checks if the user closed a tab when assembling and disables
        //the stop button after assemble finishes.
        if (this.codeTabPane.getTabs().isEmpty()){
            this.stopMipsButton.setDisable(true);
        }
        else{
            this.enableAssembleRun();
        }
    }

    /**
     * handles Assembling and Running MIPS Code
     */
    public void handleAssembleRun(){

        // Try to assemble
        if(!this.assembleSuccessful){
            return;
        }
        // Disable appropriate assemble buttons
        this.disableAssembleRun();
        // Run the MIPS program
        ProcessBuilder processBuilder = new ProcessBuilder("java","-jar", "include/Mars4_5.jar",
                this.codeTabPane.getFileName());
        AssembleOrRunTask runTask = new AssembleOrRunTask(this.console,processBuilder);
        this.curFutureTask = new FutureTask<Boolean>(runTask);
        ExecutorService curExecutor = Executors.newFixedThreadPool(1);
        curExecutor.execute(this.curFutureTask);

        try{
            this.curFutureTask.get();
            curExecutor.shutdown();
            Platform.runLater(() ->
                    this.console.writeToConsole("\nProcess was Successful.\n",
                            "ProcessInfo"));
        }
        // if the program is interrupted, stop running
        catch (InterruptedException|ExecutionException|CancellationException e){
            runTask.stop();
        }

        //If the user close the tab when the process is running, the stop button would not
        //be disabled. This statement checks is the stop button should be disabled.
        if (this.codeTabPane.getTabs().isEmpty()){
            this.stopMipsButton.setDisable(true);
        }
        else{
            this.enableAssembleRun();
        }
    }

    /**
     * Stops execution of MIPS code.
     */
    public void handleStopMips(){
        if(this.curFutureTask!=null) {
            this.curFutureTask.cancel(true);
            this.console.writeToConsole("Process terminated.\n", "ProcessInfo");
        }
    }

    /**
     * Disables the Assemble and Assemble and Run buttons, enables the Stop button.
     */
    public void disableAssembleRun() {
        this.assembleButton.setDisable(true);
        this.assembleRunButton.setDisable(true);
        this.stopMipsButton.setDisable(false);
    }

    /**
     * Enables the Assemble and Assemble and Run buttons, disables the Stop button.
     */
    public void enableAssembleRun() {
        this.assembleButton.setDisable(false);
        this.assembleRunButton.setDisable(false);
        this.stopMipsButton.setDisable(true);
    }

    /**
     * enable assemble, assemble run buttons when open a MIPS file and disable the rest
     * enable scan, scan&parse, scan parse & check buttons when open a btm file
     */
    public void enableCorrectButtons(){
        if (this.codeTabPane.getFileName()==null){
            return;
        }
        else if (this.codeTabPane.getFileName().endsWith(".asm") || this.codeTabPane.getFileName().endsWith(".s")) {
            this.assembleButton.setDisable(false);
            this.assembleRunButton.setDisable(false);
            this.stopMipsButton.setDisable(true);
            this.scanButton.setDisable(true);
            this.scanParseButton.setDisable(true);
            this.checkButton.setDisable(true);
            this.compileButton.setDisable(true);
        } else if (this.codeTabPane.getFileName().endsWith(".btm")) {
            this.assembleButton.setDisable(true);
            this.assembleRunButton.setDisable(true);
            this.stopMipsButton.setDisable(true);
            this.scanButton.setDisable(false);
            this.scanParseButton.setDisable(false);
            this.checkButton.setDisable(false);
            this.compileButton.setDisable(false);
        }else{
            this.assembleButton.setDisable(true);
            this.assembleRunButton.setDisable(true);
            this.stopMipsButton.setDisable(true);
            this.scanButton.setDisable(true);
            this.scanParseButton.setDisable(true);
            this.checkButton.setDisable(true);
            this.compileButton.setDisable(true);
        }
    }

    /**
     * Check if the scan task is still running.
     * @return true if this task is done, and false otherwise
     */
    public boolean scanIsDone(){
        return this.scanIsDone;
    }

    /**
     * Check if the parse task is still running.
     * @return true if this task is done, and false otherwise
     */
    public boolean parseIsDone(){
        return this.parseIsDone;
    }

    /**
     * Check if analyze task is done
     *
     */
    public boolean checkIsDone(){return this.checkIsDone;}

    /**
     * Check if the task is still running.
     * @return true if this task is not running, and false otherwise
     */
    public boolean getTaskStatus(){
        if(this.curFutureTask == null){
            return true;
        }
        else{
            return this.curFutureTask.isDone();
        }
    }

    /**
     * An inner class used to analyze a file in a separate thread.
     * Prints error info to the console
     */
    private class CheckerCompileTask implements Callable{
        private String method;

        private CheckerCompileTask(String method){
            this.method = method;
        }
        /**
         * Create a Parser and use it to create an AST
         * Passes AST to the semantic analyzer
         * @return ClassTree generated by the Analyzer
         */
        @Override
        public ClassTreeNode call(){
            ErrorHandler errorHandler = new ErrorHandler();
            Parser parser = new Parser(errorHandler);
            SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
            String filename = ToolbarController.this.codeTabPane.getFileName();
            Program AST;
            ClassTreeNode classTree = null;
            try{
                AST = parser.parse(filename);
                Platform.runLater(()->ToolbarController.this.console.writeToConsole(
                        "Parsing Successful.\n", "Output"));
                classTree = analyzer.analyze(AST);

                if(method.equals("generator")) {
                    MipsCodeGenerator mipsCodeGenerator = new MipsCodeGenerator(errorHandler, true, true);

                    String outFile = ToolbarController.this.codeTabPane.getFileName().split("\\.")[0] + ".asm";
                    mipsCodeGenerator.generate(classTree, outFile, AST);
                    File outString = new File(outFile);
                    //print to a new tab
                    Platform.runLater(()-> {
                        ToolbarController.this.codeTabPane.makeTabFromFile(outString,true);});
                }

                Platform.runLater(()-> {
                    if (errorHandler.errorsFound()) {

                        List<Error> errorList = errorHandler.getErrorList();
                        Iterator<Error> errorIterator = errorList.iterator();
                        ToolbarController.this.console.writeToConsole("\n", "Error");
                        while (errorIterator.hasNext()) {
                            ToolbarController.this.console.writeToConsole(errorIterator.next().toString() +
                                    "\n", "Error");
                        }

                    }else{
                        Platform.runLater(()->ToolbarController.this.console.writeToConsole(
                                "Compilation Successful.\n", "Output"));
                    }
                });
            }
            catch (CompilationException e){
                Platform.runLater(()-> {
                    ToolbarController.this.console.writeToConsole("Parsing Failed\n","Error");
                    ToolbarController.this.console.writeToConsole("There were: " +
                            errorHandler.getErrorList().size() + " errors in " +
                            ToolbarController.this.codeTabPane.getFileName() + "\n", "Output");

                    if (errorHandler.errorsFound()) {
                        List<Error> errorList = errorHandler.getErrorList();
                        Iterator<Error> errorIterator = errorList.iterator();
                        ToolbarController.this.console.writeToConsole("\n", "Error");
                        while (errorIterator.hasNext()) {
                            ToolbarController.this.console.writeToConsole(errorIterator.next().toString() +
                                    "\n", "Error");
                        }
                    }
                });
            }
            return classTree;
        }
    }


    /**
     * An inner class used to parse a file in a separate thread.
     * Prints error info to the console
     */
    private class ParseTask implements Callable{

        /**
         * Create a Parser and use it to create an AST
         * @return AST tree created by a parser
         */
        @Override
        public Program call(){
            ErrorHandler errorHandler = new ErrorHandler();
            Parser parser = new Parser(errorHandler);
            String filename = ToolbarController.this.codeTabPane.getFileName();
            Program AST = null;
            try{
                AST = parser.parse(filename);
                Platform.runLater(()->ToolbarController.this.console.writeToConsole(
                        "Parsing Successful.\n", "Output"));
            }
            catch (CompilationException e){
                Platform.runLater(()-> {
                    ToolbarController.this.console.writeToConsole("Parsing Failed\n","Error");
                    ToolbarController.this.console.writeToConsole("There were: " +
                            errorHandler.getErrorList().size() + " errors in " +
                            ToolbarController.this.codeTabPane.getFileName() + "\n", "Output");

                    if (errorHandler.errorsFound()) {
                        List<Error> errorList = errorHandler.getErrorList();
                        Iterator<Error> errorIterator = errorList.iterator();
                        ToolbarController.this.console.writeToConsole("\n", "Error");
                        while (errorIterator.hasNext()) {
                            ToolbarController.this.console.writeToConsole(errorIterator.next().toString() +
                                    "\n", "Error");
                        }
                    }
                });
            }
            return AST;
        }
    }

    /**
     * A private inner class used to scan a file in a separate thread
     * Print error messages to the console and write tokens in a new tab
     */
    private class ScanTask implements Callable {
        /**
         * Start the process by creating a scanner and use it to scan the file
         * @return a result string containing information about all the tokens
         */
        @Override
        public String call(){
            ErrorHandler errorHandler = new ErrorHandler();
            Scanner scanner = new Scanner(ToolbarController.this.codeTabPane.getFileName(), errorHandler);
            Token token = scanner.scan();
            StringBuilder tokenString = new StringBuilder();

            while(token.kind != Token.Kind.EOF){
                tokenString.append(token.toString() + "\n");
                token = scanner.scan();
            }
            String resultString = tokenString.toString();
            Platform.runLater(()-> {
                ToolbarController.this.console.writeToConsole("There were: " +
                        errorHandler.getErrorList().size() + " errors in " +
                        ToolbarController.this.codeTabPane.getFileName() + "\n","Output");
                if(errorHandler.errorsFound()){
                    List<Error> errorList= errorHandler.getErrorList();
                    Iterator<Error> errorIterator = errorList.iterator();
                    ToolbarController.this.console.writeToConsole("\n","Error");

                    while(errorIterator.hasNext()){
                        ToolbarController.this.console.writeToConsole(
                                errorIterator.next().toString() + "\n","Error");
                    }
                }
                ToolbarController.this.codeTabPane.createTabWithContent(resultString);
                ToolbarController.this.scanIsDone = true;
            });
            return tokenString.toString();
        }
    }

    /**
     * An inner class used for a thread to execute the run task
     * Designed to be used for assembling or running.
     * Writes the input/output error to the console.
     */
    private class AssembleOrRunTask implements Callable{
        private Process curProcess;
        private Console console;
        private ProcessBuilder processBuilder;

        /**
         * Initializes this compile/run task
         * @param console where to write output to
         * @param processBuilder the ProcessBuilder we have used to call javac/java
         */
        public AssembleOrRunTask(Console console, ProcessBuilder processBuilder){
            this.console = console;
            this.processBuilder = processBuilder;
        }

        /**
         * Starts the process
         * @return will return false if there is an error, true otherwise.
         * @throws IOException error reading input/output to/from console
         */
        @Override
        public Boolean call() throws IOException{
            // we use Boolean because this is required by the Callable interface
            this.curProcess = this.processBuilder.start();
            BufferedReader stdInput, stdError;
            BufferedWriter stdOutput;
            stdInput = new BufferedReader(new InputStreamReader(this.curProcess.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(this.curProcess.getErrorStream()));
            stdOutput = new BufferedWriter((new OutputStreamWriter(this.curProcess.getOutputStream())));

            // True if there are no errors
            boolean taskSuccessful = true;

            // A separate thread that checks for user input to the console
            new Thread(()->{
                while(this.curProcess.isAlive()){
                    if(this.console.getReceivedCommand()){
                        try {
                            stdOutput.write(this.console.getConsoleCommand());
                            this.console.setReceivedCommand(false);
                            stdOutput.flush();
                        }catch (IOException e){this.stop();}
                    }
                }
            }).start();

            int inp;
            int err = -1;
            // While there is some input to the console, or errors that have occurred,
            // append them to the console for the user to see.
            while ((inp = stdInput.read()) >= 0 || (err = stdError.read()) >= 0){

                final char finalInput = (char)inp;
                final char finalError = (char)err;

                if (inp >= 0) {
                    Platform.runLater(() -> this.console.writeToConsole(Character.toString(finalInput), "Output"));
                }
                if(err >= 0) {
                    taskSuccessful = false;
                    Platform.runLater(() -> this.console.writeToConsole(Character.toString(finalError), "Error"));
                }
                try {
                    Thread.sleep(2);
                }catch (InterruptedException e){
                    this.stop();
                    return taskSuccessful;
                }
            }
            stdError.close();
            stdInput.close();
            stdOutput.close();
            return taskSuccessful;
        }

        /**
         * Stop the current process
         */
        public void stop(){
            if(this.curProcess != null){
                curProcess.destroyForcibly();
            }
        }
    }
}