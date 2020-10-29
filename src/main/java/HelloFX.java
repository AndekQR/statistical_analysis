import javafx.application.Application;
import javafx.stage.Stage;

public class HelloFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewManager manager = new ViewManager();
        manager.showMainPanel();
    }
}
