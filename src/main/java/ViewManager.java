import controller.MainController;
import javafx.stage.Stage;
import view.MainView;

public class ViewManager {

    public void showMainPanel(Stage primaryStage) {
        MainController mainController = new MainController();
        new MainView(mainController, primaryStage);
    }
}
