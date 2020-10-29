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

    private Integer getColumnMinValue(int columnIndex, List<MyRow> data) {
        List<Integer> columnData=this.getColumnData(columnIndex, data);
        Integer min=columnData.stream()
                .mapToInt(v -> v)
                .min().orElseThrow(NoSuchElementException::new);
        return min;
    }

    private Integer getColumnMaxValue(int columnIndex, List<MyRow> data) {
        List<Integer> columnData=this.getColumnData(columnIndex, data);
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
}
