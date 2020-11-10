package controller;

import model.MinMaxColumns;
import model.MyPoint;
import model.MyRow;
import model.WorkbookData;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import service.DataFixer;
import service.DataGetter;
import service.ExcelParser;
import service.MyMath;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class MainController {

    private ExcelParser parser;
    private DataFixer fixer;
    private MyMath myMath;
    private String pathToFile;

    private List<MyRow> data;
    private List<String> titlies;

    public MainController() {

    }

    public Future<?> prepareData(String path) {
        this.pathToFile=path;
        this.myMath=new MyMath();
        this.parser=new ExcelParser(path);
        this.fixer=new DataFixer();
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        Future<?> submit=executorService.submit(() -> {
            try {
                WorkbookData parse=this.parser.parse(true);
                this.data=this.fixer.fix(parse.getData());
                this.titlies=parse.getTitlies();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        executorService.shutdown();
        return submit;
    }

    public List<String> getTitlies() {
        return this.titlies;
    }

    public List<MinMaxColumns> getColumnsMinMax() {
        return myMath.getColumnMinMax(this.titlies, data);
    }

    public double getColumnAverage(int columnIndex) {
        double v=this.myMath.getAverageOfColumnValues(columnIndex, data).orElse(-1D);
        return this.myMath.toTwoDecimalPlaces(v);
    }

    public double getStandartDeviation(int columnIndex) {
        return this.myMath.getStandartDevition(columnIndex, data);
    }

    public Integer getMedian(int columnIndex) {
        return this.myMath.getMedian(columnIndex, this.data);
    }

    public double getQuantile(double which, int columnIndex) {
        return this.myMath.getQuantile(which, columnIndex, this.data);
    }

    public double getInterquartileRange(int columnIndex) {
        return this.myMath.getInterquartileRange(columnIndex, this.data);
    }

    public double getPearsonColleration(int columnOne, int columnTwo) {
        return this.myMath.getPearsonColleration(columnOne, columnTwo, this.data);
    }

    public List<Integer> getColumnData(int columnIndex) {
        DataGetter dataGetter=new DataGetter();
        return dataGetter.getColumnData(columnIndex, this.data);
    }

    public Double[] getMinToMaxSequence() {
        int min=myMath.getMin(this.data) * 10;
        int max=myMath.getMax(this.data) * 10;

        return IntStream.rangeClosed(min, max)
                .boxed()
                .mapToDouble(i -> i * 0.01)
                .boxed()
                .toArray(Double[]::new);

    }

    public Double[] getSquenceToDataSize() {
        return IntStream.rangeClosed(0, data.size())
                .boxed()
                .mapToDouble(Integer::doubleValue)
                .boxed()
                .toArray(Double[]::new);
    }

    public Double[] toDouble(List<Integer> data) {
        return data.stream()
                .mapToDouble(Integer::doubleValue)
                .boxed()
                .toArray(Double[]::new);
    }

    public double[] toDoublePrimitives(List<Integer> data) {
        return this.myMath.toDoubles(data);
    }

    public SimpleRegression regression(int columnOne, int columnTwo) {
        return this.myMath.regression(columnOne, columnTwo, this.data);
    }

    public double toTwoDecimalPlaces(double value) {
        return this.myMath.toTwoDecimalPlaces(value);
    }

    public List<MyPoint> getAwayPoints(int columnIndex) {
        return myMath.getAwayPoints(columnIndex, this.data);
    }

}
