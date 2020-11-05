package view;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MyContainers {

    private static VBox getBox(String title) {
        VBox vbox = new VBox();
        Text text  = new Text();
        text.setText(title+": \n");
        vbox.getChildren().add(text);
        return vbox;
    }

    public static VBox smollBox(String title) {
        VBox vbox = getBox(title);
        vbox.getStyleClass().add("small-box");
        return vbox;
    }

    public static VBox bigBox(String title) {
        VBox vbox = getBox(title);
        vbox.getStyleClass().add("big-box");
        return vbox;
    }
}
