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
 * Project 6/7
 * Date: October 26, 2018/November 3, 2018
 */

package proj7LiLianKeithHardyZhou;

import javafx.application.Platform;
import javafx.scene.control.*;
import java.io.*;
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
    private Console console;
    private Button stopButton;
    private Button compileButton;
    private Button compileRunButton;
    private TabPane tabPane;
    private boolean compSuccessful;

    /**
     * This is the constructor of ToolbarController.
     * @param console the console
     * @param stopButton the stop button
     * @param compileButton the compile button
     * @param compileRunButton the compileRun button
     * @param tabPane the tab pane
     */
    public ToolbarController(Console console, Button stopButton, Button compileButton,
                             Button compileRunButton, TabPane tabPane){
        this.console = console;
        this.tabPane = tabPane;
        this.stopButton = stopButton;
        this.compileButton = compileButton;
        this.compileRunButton = compileRunButton;
    }

    /**
     * Start the thread to compile or to compile&run the input file
     * @param filename the name of the file to compile or compile and run
     * @param method to compile or to compile and run
     */
    public void startCompileOrCompileRun(String filename, String method){
        Thread thread;
        if(method.equals("compile"))
            thread = new Thread(()->compileFile(filename));
        else
            thread = new Thread(()->compileRunFile(filename));
        thread.start();
    }

    /**
     * Stops all currently compiling files and any currently running Java programs
     */
    public void handleStop(){
        if(this.curFutureTask!=null) {
            this.curFutureTask.cancel(true);
            this.console.WriteToConsole("Process terminated.\n", "ProcessInfo");
        }
    }

    /**
     * Compiles the specified file using the javac command
     * @param filename the name of the file to compile
     */
    private void compileFile(String filename) {
        // create and run the compile process
        ProcessBuilder processBuilder = new ProcessBuilder("javac", filename);
        CompileOrRunTask compileTask = new CompileOrRunTask(this.console, processBuilder);
        this.curFutureTask = new FutureTask<Boolean>(compileTask);
        ExecutorService compileExecutor = Executors.newFixedThreadPool(1);
        compileExecutor.execute(this.curFutureTask);

        // Check if compile was successful, and if so, indicate this in the console
        this.compSuccessful = false;
        try {
            this.compSuccessful = this.curFutureTask.get();
            if (this.compSuccessful) {
                Platform.runLater(() ->
                        this.console.WriteToConsole("\nCompilation was Successful.\n",
                                "ProcessInfo"));
            }
            compileExecutor.shutdown();
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            compileTask.stop();
        }

        //The stop button would not be disabled when there is a process running
        //even if there is no tabs opened
        //This if statements checks if the user closed a tab when compiling and disables
        //the stop button after compile finishes.
        if (this.tabPane.getTabs().isEmpty()){
            this.stopButton.setDisable(true);
        }
        else{
            this.enableCompRun();
        }
    }

    /**
     * Compiles and runs the specified file using the java command
     * @param fileNameWithPath the file name, including its path
     */
    private void compileRunFile(String fileNameWithPath){
        // Try to compile
        if(!this.compSuccessful){
            return;
        }
        // Disable appropriate compile buttons
        this.disableCompRun();

        // set up the necessary file path elements
        int pathLength = fileNameWithPath.length();
        File file = new File(fileNameWithPath);
        String filename = file.getName();
        String filepath = fileNameWithPath.substring(0,pathLength-filename.length());
        int nameLength = filename.length();
        String classFilename = filename.substring(0, nameLength - 5);

        // Run the java program
        ProcessBuilder processBuilder = new ProcessBuilder("java","-cp",filepath ,classFilename);
        CompileOrRunTask runTask = new CompileOrRunTask(this.console,processBuilder);
        this.curFutureTask = new FutureTask<Boolean>(runTask);
        ExecutorService curExecutor = Executors.newFixedThreadPool(1);
        curExecutor.execute(this.curFutureTask);

        try{
            this.curFutureTask.get();
            curExecutor.shutdown();
        }
        // if the program is interrupted, stop running
        catch (InterruptedException|ExecutionException|CancellationException e){
            runTask.stop();
        }

        //If the user close the tab when the process is running, the stop button would not
        //be disabled. This statement checks is the stop button should be disabled.
        if (this.tabPane.getTabs().isEmpty()){
            this.stopButton.setDisable(true);
        }
        else{
            this.enableCompRun();
        }
    }

    /**
     * Check if the task is still running.
     * @return true if this task is running, and false otherwise
     */
    public boolean getTaskStatus(){
        if(this.curFutureTask == null){
            return false;
        }
        else{
            return !this.curFutureTask.isDone();
        }
    }

    /**
     * Disables the Compile and Compile and Run buttons, enables the Stop button.
     */
    public void disableCompRun() {
        this.compileButton.setDisable(true);
        this.compileRunButton.setDisable(true);
        this.stopButton.setDisable(false);
    }

    /**
     * Enables the Compile and Compile and Run buttons, disables the Stop button.
     */
    public void enableCompRun() {
        this.compileButton.setDisable(false);
        this.compileRunButton.setDisable(false);
        this.stopButton.setDisable(true);
    }

    /**
     * An inner class used for a thread to execute the run task
     * Designed to be used for compilation or running.
     * Writes the input/output error to the console.
     */
    private class CompileOrRunTask implements Callable{
        private Process curProcess;
        private Console console;
        private ProcessBuilder processBuilder;

        /**
         * Initializes this compile/run task
         * @param console where to write output to
         * @param processBuilder the ProcessBuilder we have used to call javac/java
         */
        public CompileOrRunTask(Console console, ProcessBuilder processBuilder){
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
                    Platform.runLater(() -> this.console.WriteToConsole(Character.toString(finalInput), "Output"));
                }
                if(err >= 0) {
                    taskSuccessful = false;
                    Platform.runLater(() -> this.console.WriteToConsole(Character.toString(finalError), "Error"));
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