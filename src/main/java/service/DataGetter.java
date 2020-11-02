package service;

import model.MinMaxColumns;
import model.MyRow;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DataGetter {

    public List<Integer> getColumnData(int columnNumber, List<MyRow> data) {
        List<Integer> result=new ArrayList<>();
        for (int i=0; i < data.size(); i++) { //rows
            for (int i1=0; i1 < data.get(i).getCellsData().size(); i1++) { //column
                if (i1 == columnNumber) result.add(data.get(i).getCellDataValue(i1));
            }
        }
        return result;
    }
}
