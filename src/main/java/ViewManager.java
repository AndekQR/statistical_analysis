import controller.MainController;
import view.MainView;

public class ViewManager {

    public void showMainPanel() {
        MainController mainController = new MainController();
        new MainView(mainController);
    }
}
