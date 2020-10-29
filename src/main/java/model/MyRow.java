package model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MyRow {

    private Integer rowNumber;
    private List<Integer> cellsData = new ArrayList<>();

    public void addCellData(Integer data) {
        this.cellsData.add(data);
    }

    public Integer getCellDataValue(int columnNumber) {
        return this.cellsData.get(columnNumber);
    }

}
