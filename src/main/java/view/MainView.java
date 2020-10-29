package view;

import controller.MainController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
//        this.container.getChildren().add(l);

//        HBox hBox1 = new HBox();
//        hBox1.getStyleClass().add("small-box");
//        HBox hBox2 = new HBox();
//        hBox2.getStyleClass().add("medium-box");
//        HBox hBox3 = new HBox();
//        hBox3.getStyleClass().add("big-box");
//        this.container.getChildren().addAll(hBox1, hBox2, hBox3);

        this.initFileChooser();

        ScrollPane scrollPane = new ScrollPane(this.container);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        Scene scene=new Scene(scrollPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        Stage stage=new Stage(StageStyle.DECORATED);
        stage.setTitle("XmlToJson");
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
        button.setOnAction(e -> {
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
        this.container.add(this.showAverageAndDeviation(), 1,0, 1, 3);


    }

    private VBox showAverageAndDeviation() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("small-box");

        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text = new Text();
            text.setText(titlies.get(i)+": \n" +
                    "Average: "+this.mainController.getColumnAverage(i)+"\n" +
                    "Standart deviation: "+this.mainController.getStandartDeviation(i)+"\n");
            vBox.getChildren().add(text);
        }
        return vBox;
    }

    private VBox showMinMaxValues() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("small-box");
        List<MinMaxColumns> result =this.mainController.getColumnsMinMax();
        result.forEach(o -> {
            Text text = new Text();
            text.setText(o.getColumnName()+": <"+o.getMinValue()+":"+o.getMaxValue()+">");
            vBox.getChildren().add(text);
        });
       return vBox;
    }
}
