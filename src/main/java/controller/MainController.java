package controller;

import model.MinMaxColumns;
import model.MyRow;
import model.WorkbookData;
import service.DataFixer;
import service.DataGetter;
import service.ExcelParser;
import service.MyMath;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainController {

    private ExcelParser parser;
    private DataFixer fixer;
    private DataGetter dataGetter;
    private MyMath myMath;
    private String pathToFile;

    private List<MyRow> data;
    private List<String> titlies;

    public MainController() {

    }

    public Future<?> prepareData(String path) {
        this.pathToFile=path;
        this.myMath = new MyMath();
//        this.parser = new ExcelParser("D:\\weaii\\magisterka\\semestr2\\Analiza i wizualizacja danych\\statistical_analysis\\grzyby.xlsx");
        this.parser=new ExcelParser(path);
        this.fixer=new DataFixer();
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        Future<?> submit=executorService.submit(() -> {
            try {
                WorkbookData parse=this.parser.parse(true);
                this.data=this.fixer.fix(parse.getData());
                this.titlies=parse.getTitlies();
                this.dataGetter=new DataGetter();
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
}
