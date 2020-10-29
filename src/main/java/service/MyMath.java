package service;


import model.MyRow;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalDouble;

public class MyMath {

    public OptionalDouble getAverage(List<Integer> data) {
        return data.stream()
                .mapToDouble(Integer::doubleValue)
                .average();
    }

    public double toTwoDecimalPlaces(double value) {
        return Math.floor(value * 100)/100;
    }

    public OptionalDouble getAverageOfColumnValues(int columnIndex, List<MyRow> data) {
        DataGetter dataGetter = new DataGetter();
        return this.getAverage(dataGetter.getColumnData(columnIndex, data));
    }

    public double getStandartDevition(int columnIndex, List<MyRow> data) {
        DataGetter dataGetter = new DataGetter();
        StandardDeviation standardDeviation = new StandardDeviation();
        double[] doubles=dataGetter.getColumnData(columnIndex, data).stream()
                .mapToDouble(Integer::doubleValue)
                .toArray();
        double evaluate=standardDeviation.evaluate(doubles);
        return this.toTwoDecimalPlaces(evaluate);
    }
//
//    public <T extends Number> T getAverage(List<Integer> data, Class<T> returnType) {
//        Mean mean=new Mean();
//        double[] doubles=data.stream()
//                .mapToDouble(Integer::doubleValue)
//                .toArray();
//
//        double average=mean.evaluate(doubles);
//
//        if (returnType.equals(Integer.class))
//            return (T) (Object) Math.round(average);
//        else if(returnType.equals(Double.class))
//            return average;
//    }
}
