package service;

import model.MyRow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DataGetter {

    private final List<MyRow> data;

    public DataGetter(List<MyRow> data) {
        this.data=data;
    }

    public List<Integer> getColumnData(int columnNumber) {
        List<Integer> result = new ArrayList<>();
        for (int i=0; i < data.size(); i++) { //rows
            for (int i1=0; i1 < data.get(i).getCellsData().size(); i1++) { //column
                if(i1 == columnNumber) result.add(data.get(i).getCellDataValue(i1));
            }
        }
        return result;
    }
}
