package service;


import java.util.List;
import java.util.OptionalDouble;

public class MyMath {

    public OptionalDouble getAverage(List<Integer> data)  {
        return data.stream()
                .mapToDouble(Integer::doubleValue)
                .average();
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
