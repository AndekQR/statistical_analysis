package view;

import controller.MainController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.MinMaxColumns;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MainView {

    private static final int HEIGHT=500;
    private static final int WIDTH=630;

    private final MainController mainController;
    private GridPane container;
    private Stage primaryStage;

    public MainView(MainController mainController, Stage primaryStage) {
        this.mainController = mainController;
        this.primaryStage = primaryStage;
        this.initialize();
    }

    private void initialize() {
        this.container = new GridPane();
        this.container.setPrefSize(WIDTH, HEIGHT);
        this.container.setAlignment(Pos.TOP_CENTER);

        this.initFileChooser();

        ScrollPane scrollPane = new ScrollPane(this.container);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        Scene scene=new Scene(scrollPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        Stage stage=new Stage(StageStyle.DECORATED);
        stage.setTitle("Analiza danych");
        stage.setScene(scene);

        stage.show();
    }

    private void initFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files", "*.xlsx"));

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("small-box");
        Button button = new Button("Choose file");
        button.setOnAction(e -> { //TODO: UNCOMMENT
//            File selectedFile = fileChooser.showOpenDialog(this.primaryStage);
//            this.mainController.prepareData(selectedFile.getAbsolutePath());
            Future<?> future=this.mainController.prepareData("D:\\weaii\\magisterka\\semestr2\\Analiza i wizualizacja danych\\statistical_analysis\\grzyby.xlsx");
            this.container.getChildren().clear();
            try {
                future.get();
                //TODO: można dać jakies kręcoce się kółko
            } catch (InterruptedException | ExecutionException interruptedException) {
                System.out.println(e.getSource());
            }
            this.drawInterface();
        });

        hbox.getChildren().add(button);
        this.container.getChildren().add(hbox);

    }

    private void drawInterface() {
        this.container.add(this.showMinMaxValues(), 0,0, 1, 2);
        this.container.add(this.medianValues(), 1, 0, 1,2);
        this.container.add(this.showAverageAndDeviation(), 2,0, 1, 4);
        this.container.add(this.interquartileRange(), 0, 2, 1, 1);
        this.container.add(this.quantiles(), 1, 2, 1, 3);
    }


    private VBox showAverageAndDeviation() {
        VBox vBox = MyContainers.smollBox("Srednie i odchylenie");

        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text = new Text();
            text.setText(titlies.get(i)+": \n" +
                    "Average: "+this.mainController.getColumnAverage(i)+"\n" +
                    "Standart deviation: "+this.mainController.getStandartDeviation(i));
            vBox.getChildren().add(text);
        }
        return vBox;
    }

    private VBox showMinMaxValues() {
        VBox vBox = MyContainers.smollBox("Wartosci min, max");
        List<MinMaxColumns> result =this.mainController.getColumnsMinMax();
        result.forEach(o -> {
            Text text = new Text();
            text.setText(o.getColumnName()+": <"+o.getMinValue()+":"+o.getMaxValue()+">");
            vBox.getChildren().add(text);
        });
       return vBox;
    }

    private VBox medianValues() {
        VBox vbox = MyContainers.smollBox("Mediana");
        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text1  = new Text();
            text1.setText(titlies.get(i)+": "+this.mainController.getMedian(i));
            vbox.getChildren().add(text1);
        }
        return vbox;
    }

    private VBox interquartileRange() {
        VBox vbox = MyContainers.smollBox("Rozstep miedzykwartylowy");
        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text  = new Text();
            text.setText(titlies.get(i)+": "+this.mainController.getInterquartileRange(i));
            vbox.getChildren().add(text);
        }
        return vbox;
    }

    private VBox quantiles() {
        VBox vBox = MyContainers.smollBox("Kwartyle 0.1 i 0.9");
        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text  = new Text();
            text.setText(titlies.get(i)+"\n" +
                    "Kwartyl 0.1: "+this.mainController.getQuantile(10, i)+"\n" +
                    "Kwartyl 0.9: "+this.mainController.getQuantile(90, i));
            vBox.getChildren().add(text);
        }
        return vBox;
    }
}
