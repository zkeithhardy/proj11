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
 */

package proj10LiLianKeithHardyZhou;

import javafx.application.Platform;
import proj10LiLianKeithHardyZhou.bantam.ast.Program;
import proj10LiLianKeithHardyZhou.bantam.parser.Parser;
import proj10LiLianKeithHardyZhou.bantam.treedrawer.Drawer;
import proj10LiLianKeithHardyZhou.bantam.util.CompilationException;
import proj10LiLianKeithHardyZhou.bantam.util.ErrorHandler;
import proj10LiLianKeithHardyZhou.bantam.util.Error;
import proj10LiLianKeithHardyZhou.bantam.lexer.Scanner;
import proj10LiLianKeithHardyZhou.bantam.lexer.Token;

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

    private boolean scanIsDone;
    private boolean parseIsDone;
    private Console console;
    private CodeTabPane codeTabPane;
    private String tokenString;
    private Integer errorCounter = 0;

    /**
     * This is the constructor of ToolbarController.
     * @param console the console
     * @param codeTabPane the tab pane
     */
    public ToolbarController(Console console, CodeTabPane codeTabPane){
        this.console = console;
        this.codeTabPane = codeTabPane;
        this.scanIsDone = true;
        this.parseIsDone = true;
    }

    /**
     * Handles actions for scan or scanParse buttons
     * @param method
     */
    public void handleScanOrScanParse(String method){
        if(method.equals("scan")){
            this.handleScan();
        }else if(method.equals("scanParse")){
            this.handleScanAndParse();
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
     * handles scanning and parsing in the code area.
     * Draw the AST to a Java Swing window
     */
    public void handleScanAndParse(){
        this.parseIsDone = false;
        new Thread (()->{
            ParseTask parseTask = new ParseTask();
            FutureTask<Program> curFutureTask = new FutureTask<Program>(parseTask);
            ExecutorService curExecutor = Executors.newFixedThreadPool(1);
            curExecutor.execute(curFutureTask);
            try{
                Program AST = curFutureTask.get();
                if(AST != null){
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
}