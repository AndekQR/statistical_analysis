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
import org.charts.dataviewer.api.config.DataViewerConfiguration;
import org.charts.dataviewer.api.data.PlotData;
import org.charts.dataviewer.api.trace.HistogramTrace;
import org.charts.dataviewer.api.trace.LineTrace;
import org.charts.dataviewer.api.trace.ScatterTrace;
import org.charts.dataviewer.javafx.JavaFxDataViewer;
import org.charts.dataviewer.utils.TraceType;
import org.charts.dataviewer.utils.TraceVisibility;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
        this.container.add(this.showMinMaxValues(), 0, 0, 1, 2);
        this.container.add(this.medianValues(), 1, 0, 1, 3);
        this.container.add(this.showAverageAndDeviation(), 2, 0, 1, 7);

        this.container.add(this.interquartileRange(), 0, 2, 1, 3);
        this.container.add(this.quantiles(), 1, 3, 1, 8);

        this.container.add(this.collerationTable(), 0, 11, 3, 6);
        this.container.add(this.attributesValueChart(), 0, 17, 3, 6);
        this.container.add(this.attributesValuesHistogram(), 0, 23, 3, 5);
        this.container.add(this.scatterPlotJFree(), 0, 28, 3, 5);
        this.container.add(this.boxPlot(), 0, 32, 3, 5);
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

        JavaFxDataViewer dataViewer=new JavaFxDataViewer();
        DataViewerConfiguration config=new DataViewerConfiguration();
        config.setPlotTitle("Wykres prezentujacy rozklad wartosci zmiennych");
        config.setxAxisTitle("rekord*60");
        config.setyAxisTitle("wartosc zmiennej");
        dataViewer.updateConfiguration(config);

        PlotData plotData=new PlotData();

        for (int i=0; i < titlies.size(); i++) {
            LineTrace<Double> doubleLineTrace=new LineTrace<>();
            doubleLineTrace.setTraceVisibility(TraceVisibility.LEGENDONLY);
            List<Integer> columnData=this.mainController.getColumnData(i);
            doubleLineTrace.setTraceName(titlies.get(i));
            doubleLineTrace.setxArray(this.mainController.getSquenceToDataSize());
            doubleLineTrace.setyArray(mainController.toDouble(columnData));
            doubleLineTrace.setTraceType(TraceType.LINE);
            plotData.addAll(doubleLineTrace);
        }
        dataViewer.updatePlot(plotData);

        vBox.getChildren().add(dataViewer);

        return vBox;
    }

    private VBox attributesValuesHistogram() {
        VBox vBox=MyContainers.bigBox("Histogram prezentujacy wartosci zmiennych");
        List<String> titlies=this.mainController.getTitlies();

        JavaFxDataViewer dataViewer=new JavaFxDataViewer();
        DataViewerConfiguration config=new DataViewerConfiguration();
        config.setPlotTitle("Wykres prezentujacy rozklad wartosci zmiennych");
        config.setxAxisTitle("wartosc zmiennej");
        config.setyAxisTitle("liczba");
        dataViewer.updateConfiguration(config);

        PlotData plotData=new PlotData();

        for (int i=0; i < titlies.size(); i++) {
            HistogramTrace<Double> his=new HistogramTrace<>();
            his.setTraceVisibility(TraceVisibility.LEGENDONLY);
            List<Integer> columnData=this.mainController.getColumnData(i);
            his.setTraceName(titlies.get(i));
            his.setxArray(this.mainController.toDouble(columnData));
            his.setyArray(this.mainController.getMinToMaxSequence());
            plotData.addAll(his);
        }

        dataViewer.updatePlot(plotData);

        vBox.getChildren().add(dataViewer);
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
        LineFunction2D lineFunction2D = new LineFunction2D(olsRegression[0], olsRegression[1]);
        XYDataset dataset =DatasetUtilities.sampleFunction2D(lineFunction2D, 0D, 8, 50, "linia regresji");
        XYPlot xyPlot=chart.getXYPlot();
        xyPlot.setDataset(1, dataset);
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(
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
        for (int i=0; i < titlies.size()/4; i++) {
            List<Integer> columnData=this.mainController.getColumnData(i);
            dataset.add(columnData, titlies.get(i), "type: "+i);

        }
//        BoxAndWhiskerRenderer boxRenderer = new BoxAndWhiskerRenderer();
//        DefaultCategoryDataset catData = new DefaultCategoryDataset();
//        catData.addValue(dataset.getMeanValue(0, 0), "Mean", dataset.getColumnKey(0));
//        catData.addValue(dataset.getMeanValue(0, 1), "Mean", dataset.getColumnKey(1));
//        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
//        CategoryAxis xAxis = new CategoryAxis("Type");
//        NumberAxis yAxis = new NumberAxis("Value");
//        yAxis.setAutoRangeIncludesZero(false);
//        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, boxRenderer);
//        plot.setDataset(1, catData);
//        plot.setRenderer(1, lineRenderer);
//        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
//
//        JFreeChart chart = new JFreeChart("test", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart("box", "category", "value", dataset, true);
        ChartViewer chartViewer=new ChartViewer(chart);
        chartViewer.setPrefHeight(600);
        chartViewer.setPrefWidth(300);
        vBox.getChildren().add(chartViewer);

        return vBox;
    }
}
