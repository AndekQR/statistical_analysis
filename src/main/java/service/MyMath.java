package service;


import helper.Quartile;
import model.MinMaxColumns;
import model.MyPoint;
import model.MyRow;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;

public class MyMath {

    public OptionalDouble getAverage(List<Integer> data) {
        return data.stream()
                .mapToDouble(Integer::doubleValue)
                .average();
    }

    public double toTwoDecimalPlaces(double value) {
        return Math.floor(value * 100) / 100;
    }

    public OptionalDouble getAverageOfColumnValues(int columnIndex, List<MyRow> data) {
        DataGetter dataGetter=new DataGetter();
        return this.getAverage(dataGetter.getColumnData(columnIndex, data));
    }

    public double[] toDoubles(List<Integer> data) {
        return data.stream()
                .mapToDouble(Integer::doubleValue)
                .toArray();
    }

    public double getStandartDevition(int columnIndex, List<MyRow> data) {
        DataGetter dataGetter=new DataGetter();
        StandardDeviation standardDeviation=new StandardDeviation();
        double[] doubles=this.toDoubles(dataGetter.getColumnData(columnIndex, data));
        double evaluate=standardDeviation.evaluate(doubles);
        return this.toTwoDecimalPlaces(evaluate);
    }

    public Integer getMedian(int columnIndex, List<MyRow> data) {
        DataGetter dataGetter=new DataGetter();
        Median median=new Median();
        double evaluate=median.evaluate(this.toDoubles(dataGetter.getColumnData(columnIndex, data)));
        return Math.toIntExact(Math.round(evaluate));
    }

    private Integer getColumnMinValue(int columnIndex, List<MyRow> data) {
        DataGetter dataGetter=new DataGetter();
        List<Integer> columnData=dataGetter.getColumnData(columnIndex, data);
        Integer min=columnData.stream()
                .mapToInt(v -> v)
                .min().orElseThrow(NoSuchElementException::new);
        return min;
    }

    private Integer getColumnMaxValue(int columnIndex, List<MyRow> data) {
        DataGetter dataGetter=new DataGetter();
        List<Integer> columnData=dataGetter.getColumnData(columnIndex, data);
        Integer min=columnData.stream()
                .mapToInt(v -> v)
                .max().orElseThrow(NoSuchElementException::new);
        return min;
    }

    public List<MinMaxColumns> getColumnMinMax(List<String> titlies, List<MyRow> data) { //tytułów powinno być tyle ile kolumn w arkuszu
        List<MinMaxColumns> result=new ArrayList<>();
        for (int i=0; i < titlies.size(); i++) {
            MinMaxColumns tmp=new MinMaxColumns();
            tmp.setColumnName(titlies.get(i));
            tmp.setMaxValue(this.getColumnMaxValue(i, data));
            tmp.setMinValue(this.getColumnMinValue(i, data));
            result.add(tmp);
        }
        return result;
    }


    public double getQuantile(double which, int columnIndex, List<MyRow> data) {
        Percentile percentile=new Percentile();
        DataGetter dataGetter=new DataGetter();
        List<Integer> columnData=dataGetter.getColumnData(columnIndex, data);
        percentile.setData(this.toDoubles(columnData));
        if (which > 0 && which <= 100) {
            return percentile.evaluate(which);
        }
        return 0D;
    }

    public double getInterquartileRange(int columnIndex, List<MyRow> data) {
        double first=this.getQuantile(Quartile.FIRST_QUARTILE.getValue(), columnIndex, data);
        double third=this.getQuantile(Quartile.THIRD_QUARTILE.getValue(), columnIndex, data);
        return this.toTwoDecimalPlaces(third - first);
    }

    public double getPearsonColleration(int columnOne, int columnTwo, List<MyRow> data) {
        DataGetter dataGetter=new DataGetter();
        double[] columnOneData=this.toDoubles(dataGetter.getColumnData(columnOne, data));
        double[] columnTwoData=this.toDoubles(dataGetter.getColumnData(columnTwo, data));

        PearsonsCorrelation pearsonsCorrelation=new PearsonsCorrelation();
        double correlation=pearsonsCorrelation.correlation(columnOneData, columnTwoData);
        if (Double.isNaN(correlation)) correlation=0;
        return this.toTwoDecimalPlaces(correlation);
    }

    public Integer getMax(List<MyRow> data) {
        Integer max=data.stream()
                .flatMap(e -> e.getCellsData().stream())
                .max(Integer::compareTo)
                .orElseThrow(RuntimeException::new);
        return max;
    }

    public Integer getMin(List<MyRow> data) {
        Integer min=data.stream()
                .flatMap(e -> e.getCellsData().stream())
                .min(Integer::compareTo)
                .orElseThrow(RuntimeException::new);
        return min;
    }

    public SimpleRegression regression(int columnOne, int columnTwo, List<MyRow> data) {
        SimpleRegression regression = new SimpleRegression();
        DataGetter dataGetter=new DataGetter();
        double[] columnOneData=this.toDoubles(dataGetter.getColumnData(columnOne, data));
        double[] columnTwoData=this.toDoubles(dataGetter.getColumnData(columnTwo, data));

        for (int i=0; i < data.size(); i++) {
            regression.addData(columnOneData[i], columnTwoData[i]);
        }
        return regression;
    }

    public List<MyPoint> getAwayPoints(int columnIndex, List<MyRow> data) {
        double interquartileRange=this.getInterquartileRange(columnIndex, data);
        double firstQuartile=this.getQuantile(Quartile.FIRST_QUARTILE.getValue(), columnIndex, data);
        double thirdQuartile=this.getQuantile(Quartile.THIRD_QUARTILE.getValue(), columnIndex, data);

        double min=firstQuartile - (1.5 * interquartileRange);
        double max=thirdQuartile + (1.5 * interquartileRange);

        DataGetter dataGetter = new DataGetter();
        List<Integer> columnData=dataGetter.getColumnData(columnIndex, data);
        List<MyPoint> result = new ArrayList<>();

        for (int i=0; i < columnData.size(); i++) {
            Integer tmp=columnData.get(i);
            if(tmp < min || tmp > max) {
                MyPoint myPoint = new MyPoint();
                myPoint.setColumnIndex(columnIndex);
                myPoint.setRowIndex(i);
                myPoint.setValue(tmp.doubleValue());
                result.add(myPoint);
            }
        }
        return result;
    }

}
