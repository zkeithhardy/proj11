/*
 * File: PreferenceController.css
 * Names: Michael Li, Iris Lian, Zeb Keith-Hardy, Kevin Zhou
 * Class: CS 361
 * Project 6/7
 * Date: October 26, 2018/November 3, 2018
 */

package proj7LiLianKeithHardyZhou;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This is the controller class for the preference menu
 * At this point it only has one choice - night mode
 * @author Michael Li, Iris Lian, Zeb Keith-Hardy, Kevin Zhou
 */
public class PreferenceController {
    private VBox vBox;
    private Console console;

    /**
     * This is the constructor of PreferenceController
     *
     * @param vBox    the container
     * @param console the console
     */
    public PreferenceController(VBox vBox, Console console) {
        this.console = console;
        this.vBox = vBox;
    }

    /**
     * When night mode is checked or unchecked, switches to another style sheet
     */
    public void handleNightMode() {
        String nightMode = getClass().getResource("resources/NightMode.css").toExternalForm();
        if (vBox.getStylesheets().contains(nightMode)) {
            vBox.getStylesheets().removeAll(nightMode);
        } else {
            this.console.setStyleClass(this.console.getText().length(), this.console.getText().length(),
                    "default");
            this.vBox.getStylesheets().add(1, nightMode);
        }
    }

    /**
     * Handle change color action for the given type
     * Pops up a dialog window displaying the color selections for the given type
     * Change the color of the given type base on selection in the choiceBox
     * @param type "Int", "Str", "Keyword", "Paren". Type to change color.
     */
    public void handleColorAction(String type) {
        Stage keywordColorWin = new Stage();
        keywordColorWin.setTitle(type + " Color");
        VBox parentVBox = this.vBox;
        VBox colorRoot = new VBox();
        colorRoot.setAlignment(Pos.CENTER);
        colorRoot.setSpacing(10);

        final Rectangle rect = new Rectangle(75, 75);

        ChoiceBox colorCheckBox = new ChoiceBox(FXCollections.observableArrayList(
                "Current","Default", "Black", "Blue", "Green", "Pink", "Yellow", "Red"));
        colorCheckBox.setValue("Current");

        String nightMode = getClass().getResource("resources/NightMode.css").toExternalForm();
        String black = getClass().getResource("resources/"+type + "ColorCSS/" +
                type + "Black.css").toExternalForm();
        String red = getClass().getResource("resources/"+type + "ColorCSS/" +
                type + "Red.css").toExternalForm();
        String blue = getClass().getResource("resources/"+type + "ColorCSS/" +
                type + "Blue.css").toExternalForm();
        String yellow = getClass().getResource("resources/"+type + "ColorCSS/" +
                type + "Yellow.css").toExternalForm();
        String pink = getClass().getResource("resources/"+type + "ColorCSS/" +
                type + "Pink.css").toExternalForm();
        String green = getClass().getResource("resources/"+type + "ColorCSS/" +
                type + "Green.css").toExternalForm();

        Text message = new Text(type + " Color");
        message.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ObservableList<String> currentStyleSheets = parentVBox.getStylesheets();

        Color defaultColor;
        if(!currentStyleSheets.contains(nightMode)) {
            switch (type) {
                case "Str":
                    defaultColor = Color.BLUE;
                    break;
                case "Int":
                    defaultColor = Color.FIREBRICK;
                    break;
                case "Keyword":
                    defaultColor = Color.PURPLE;
                    break;
                default:
                    defaultColor = Color.TEAL;
                    break;
            }
        }
        else{
            switch (type) {
                case "Str":
                    defaultColor = Color.LIGHTGREEN;
                    break;
                case "Int":
                    defaultColor = Color.BLUE;
                    break;
                case "Keyword":
                    defaultColor = Color.ORANGE;
                    break;
                default:
                    defaultColor = Color.SALMON;
                    break;
            }
        }

        Color currentColor;
        if (currentStyleSheets.contains(black))
            currentColor = Color.BLACK;
        else if(currentStyleSheets.contains(red))
            currentColor = Color.RED;
        else if(currentStyleSheets.contains(blue))
            currentColor= Color.BLUE;
        else if(currentStyleSheets.contains(yellow))
            currentColor = Color.YELLOW;
        else if(currentStyleSheets.contains(pink))
            currentColor = Color.PINK;
        else if(currentStyleSheets.contains(green))
            currentColor = Color.GREEN;
        else
            currentColor = defaultColor;

        rect.setFill(currentColor);
        message.setFill(currentColor);

        colorCheckBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String color = (String)colorCheckBox.getValue();
                if(color.equals("Current")){
                    rect.setFill(currentColor);
                    message.setFill(currentColor);
                }
                else{
                    parentVBox.getStylesheets().removeAll(black, blue, red, yellow, pink, green);
                    switch (color){
                        case("Default"):
                            rect.setFill(defaultColor);
                            message.setFill(defaultColor);
                            break;
                        case("Black"):
                            rect.setFill(Color.BLACK);
                            message.setFill(Color.BLACK);
                            parentVBox.getStylesheets().add(black);
                            break;
                        case("Blue"):
                            rect.setFill(Color.ROYALBLUE);
                            message.setFill(Color.ROYALBLUE);
                            parentVBox.getStylesheets().add(blue);
                            break;
                        case("Red"):
                            rect.setFill(Color.FIREBRICK);
                            message.setFill(Color.FIREBRICK);
                            parentVBox.getStylesheets().add(red);
                            break;
                        case("Yellow"):
                            rect.setFill(Color.ORANGE);
                            message.setFill(Color.ORANGE);
                            parentVBox.getStylesheets().add(yellow);
                            break;
                        case("Pink"):
                            rect.setFill(Color.ORCHID);
                            message.setFill(Color.ORCHID);
                            parentVBox.getStylesheets().add(pink);
                            break;
                        default: // green
                            rect.setFill(Color.TEAL);
                            message.setFill(Color.TEAL);
                            parentVBox.getStylesheets().add(green);
                    }
                }
            }
        });

        colorRoot.getChildren().addAll(message, rect, colorCheckBox);
        Scene keywordColorScene = new Scene(colorRoot, 200, 200);
        keywordColorWin.setScene(keywordColorScene);
        keywordColorWin.show();

    }
}