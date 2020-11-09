package view;

import controller.MainController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Colleration;
import model.MinMaxColumns;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MainView {

    private static final int HEIGHT=700;
    private static final int WIDTH=800;

    private final MainController mainController;
    private GridPane container;
    private Stage primaryStage;

    public MainView(MainController mainController, Stage primaryStage) {
        this.mainController=mainController;
        this.primaryStage=primaryStage;
        this.initialize();
    }

    private void initialize() {
        this.container=new GridPane();
        this.container.setPrefSize(WIDTH, HEIGHT);
        this.container.setAlignment(Pos.TOP_CENTER);
        this.container.getStyleClass().add("back");

        this.initFileChooser();

        ScrollPane scrollPane=new ScrollPane(this.container);
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
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files", "*.xlsx"));

        HBox hbox=new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("small-box");
        Button button=new Button("Choose file");
        button.setOnAction(e -> { //TODO: UNCOMMENT
            File selectedFile=fileChooser.showOpenDialog(this.primaryStage);
            Future<?> future=this.mainController.prepareData(selectedFile.getAbsolutePath());
//            Future<?> future=this.mainController.prepareData("D:\\weaii\\magisterka\\semestr2\\Analiza i wizualizacja danych\\statistical_analysis\\grzyby.xlsx");
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
        this.container.add(this.showMinMaxValues(), 0, 0, 1, 2);
        this.container.add(this.medianValues(), 1, 0, 1, 3);
        this.container.add(this.showAverageAndDeviation(), 2, 0, 1, 7);

        this.container.add(this.interquartileRange(), 0, 2, 1, 3);
        this.container.add(this.quantiles(), 1, 3, 1, 9);

        VBox vBox=new VBox();
        vBox.getChildren().addAll(this.collerationTable(), this.attributesValueChart(), this.attributesValuesHistogram(), this.boxPlot(), this.scatterPlotJFree());
        this.container.add(vBox, 0, 13, 3, 30);


    }

    private VBox regressionResults() {
        List<String> titlies=this.mainController.getTitlies();
        RegressionResults regression=this.mainController.regression(3, 4);
        VBox vBox=MyContainers.smollBox("Regresja: " + titlies.get(3) + " - " + titlies.get(4));

        Text text=new Text();
//        text.setText(regression.);

        return vBox;
    }


    private VBox showAverageAndDeviation() {
        VBox vBox=MyContainers.smollBox("Srednie i odchylenie");

        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text=new Text();
            text.setText(titlies.get(i) + ": \n" +
                    "Average: " + this.mainController.getColumnAverage(i) + "\n" +
                    "Standart deviation: " + this.mainController.getStandartDeviation(i));
            vBox.getChildren().add(text);
        }
        return vBox;
    }

    private VBox showMinMaxValues() {
        VBox vBox=MyContainers.smollBox("Wartosci min, max");
        List<MinMaxColumns> result=this.mainController.getColumnsMinMax();
        result.forEach(o -> {
            Text text=new Text();
            text.setText(o.getColumnName() + ": <" + o.getMinValue() + ":" + o.getMaxValue() + ">");
            vBox.getChildren().add(text);
        });
        return vBox;
    }

    private VBox medianValues() {
        VBox vbox=MyContainers.smollBox("Mediana");
        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text1=new Text();
            text1.setText(titlies.get(i) + ": " + this.mainController.getMedian(i));
            vbox.getChildren().add(text1);
        }
        return vbox;
    }

    private VBox interquartileRange() {
        VBox vbox=MyContainers.smollBox("Rozstep miedzykwartylowy");
        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text=new Text();
            text.setText(titlies.get(i) + ": " + this.mainController.getInterquartileRange(i));
            vbox.getChildren().add(text);
        }
        return vbox;
    }

    private VBox quantiles() {
        VBox vBox=MyContainers.smollBox("Kwartyle 0.1 i 0.9");
        List<String> titlies=this.mainController.getTitlies();
        for (int i=0; i < titlies.size(); i++) {
            Text text=new Text();
            text.setText(titlies.get(i) + "\n" +
                    "Kwartyl 0.1: " + this.mainController.getQuantile(10, i) + "\n" +
                    "Kwartyl 0.9: " + this.mainController.getQuantile(90, i));
            vBox.getChildren().add(text);
        }
        return vBox;
    }

    private VBox collerationTable() {
        VBox vBox=MyContainers.bigBox("Korelacja Pearsona");
        List<String> titlies=this.mainController.getTitlies();
        List<Colleration> result=new ArrayList<>();
        for (int i=0; i < titlies.size(); i++) {
            Colleration colleration=new Colleration();
//            Map<String, Double> collerTo = new HashMap<>();
            List<Double> collerTo=new ArrayList<>();
            colleration.setPropertyName(titlies.get(i));
            for (int i1=0; i1 < titlies.size(); i1++) {
                double coller=this.mainController.getPearsonColleration(i, i1);
                collerTo.add(coller);
            }
            colleration.setCollerationTo(collerTo);
            result.add(colleration);
        }

        MyTable myTable=new MyTable(result);
        vBox.getChildren().add(myTable.getNode());
        return vBox;
    }

    private VBox attributesValueChart() {
        VBox vBox=MyContainers.bigBox("Wykres prezentujacy wartosci zmiennych");

        List<String> titlies=this.mainController.getTitlies();

        XYSeriesCollection dataset=new XYSeriesCollection();
        for (int i=1; i < titlies.size() / 2; i++) {
            List<Integer> columnData=this.mainController.getColumnData(i);
            Map<Integer, Long> collect=columnData.stream()
                    .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

            XYSeries series=new XYSeries(titlies.get(i));
            for (int i1=0; i1 < columnData.size(); i1++) {
                series.add(columnData.get(i1), collect.get(columnData.get(i1)));
            }
            dataset.addSeries(series);
        }

        JFreeChart chart=ChartFactory.createXYLineChart("Wykres prezentujacy rozklad wartosci zmiennych",
                "wartosc argumentow", "Ilosc argumentow", dataset, PlotOrientation.HORIZONTAL, true, true, false);


        ChartViewer chartViewer=new ChartViewer(chart);
        chartViewer.setPrefHeight(600);
        chartViewer.setPrefWidth(300);
        vBox.getChildren().add(chartViewer);
        return vBox;
    }

    private VBox attributesValuesHistogram() {
        VBox vBox=MyContainers.bigBox("Histogram prezentujacy wartosci zmiennych");
        List<String> titlies=this.mainController.getTitlies();

        HistogramDataset histogramDataset=new HistogramDataset();
        for (int i=1; i < titlies.size()/4; i++) {
            List<Integer> columnData=this.mainController.getColumnData(i);
            histogramDataset.addSeries(titlies.get(i), this.mainController.toDoublePrimitives(columnData), 500);
        }

        JFreeChart histogram=ChartFactory.createHistogram("Histogram prezentujacy wartosci zmiennych",
                "y values", "x values", histogramDataset, PlotOrientation.HORIZONTAL, true, true, false);

        ChartViewer chartViewer=new ChartViewer(histogram);
        chartViewer.setPrefHeight(600);
        chartViewer.setPrefWidth(300);
        vBox.getChildren().add(chartViewer);
        return vBox;
    }

    private VBox scatterPlotJFree() {
        VBox vBox=MyContainers.bigBox("Rozrzut");
        List<String> titlies=this.mainController.getTitlies();


        XYSeriesCollection resul=new XYSeriesCollection();
        XYSeries series=new XYSeries("random");
        double[] doubles=mainController.toDoublePrimitives(this.mainController.getColumnData(1));
        double[] doubles1=mainController.toDoublePrimitives(this.mainController.getColumnData(2));


        for (int i=0; i < doubles.length; i++) {
            series.add(doubles[i], doubles1[i]);
        }
        resul.addSeries(series);
        JFreeChart chart=ChartFactory.createScatterPlot("rozrzut", titlies.get(1), titlies.get(2), resul, PlotOrientation.HORIZONTAL, false, true, false);

        double[] olsRegression=Regression.getOLSRegression(resul, 0);
        LineFunction2D lineFunction2D=new LineFunction2D(olsRegression[0], olsRegression[1]);
        XYDataset dataset=DatasetUtilities.sampleFunction2D(lineFunction2D, 0D, 8, 50, "linia regresji");
        XYPlot xyPlot=chart.getXYPlot();
        xyPlot.setDataset(1, dataset);
        XYLineAndShapeRenderer xylineandshaperenderer=new XYLineAndShapeRenderer(
                true, false);
        xylineandshaperenderer.setSeriesPaint(0, Color.YELLOW);
        xyPlot.setRenderer(1, xylineandshaperenderer);

        ChartViewer chartViewer=new ChartViewer(chart);
        chartViewer.setPrefHeight(600);
        chartViewer.setPrefWidth(300);
        vBox.getChildren().add(chartViewer);
        return vBox;
    }


    private VBox boxPlot() {
        VBox vBox=MyContainers.bigBox("Wykres pudelkowy");
        List<String> titlies=this.mainController.getTitlies();

        DefaultBoxAndWhiskerCategoryDataset dataset=new DefaultBoxAndWhiskerCategoryDataset();
        for (int i=1; i < titlies.size() / 2; i++) {
            List<Integer> columnData=this.mainController.getColumnData(i);
            dataset.add(columnData, titlies.get(i), "type: " + i);

        }
        JFreeChart chart=ChartFactory.createBoxAndWhiskerChart("box", "category", "value", dataset, true);
        ChartViewer chartViewer=new ChartViewer(chart);
        chartViewer.setPrefHeight(600);
        chartViewer.setPrefWidth(300);
        vBox.getChildren().add(chartViewer);

        return vBox;
    }
}
