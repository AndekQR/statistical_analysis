package view;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MyContainers {

    public static VBox smollBox(String title) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add("small-box");
        Text text  = new Text();
        text.setText(title+": \n");
        vbox.getChildren().add(text);
        return vbox;
    }
}
