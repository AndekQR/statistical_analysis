package service;

import helper.DataTypes;
import model.MyRow;

import java.util.List;
import java.util.OptionalDouble;

public class DataFixer {

    private List<MyRow> data;
    private DataGetter dataGetter;
    private MyMath myMath;



    public List<MyRow> fix(final List<MyRow> dataToFix) {
        this.data = dataToFix;
        this.dataGetter = new DataGetter(this.data);
        this.myMath = new MyMath();

        fixBadCellsValues();

        return data;
    }

    private void fixBadCellsValues() {
        for (int i=0; i < data.size(); i++) {
            for (int i1=0; i1 < data.get(i).getCellsData().size(); i1++) {
                Integer value = data.get(i).getCellDataValue(i1);
                if (value.equals(DataTypes.BAD_CELL_DATA.getValue())) {
                    this.setNewDataCellValue(i, i1, this.getAverageOfColumn(i1));
                }
            }
        }
    }

    private void setNewDataCellValue(int rowNumber, int columnNumber, Integer value) {
        this.data.get(rowNumber).getCellsData().set(columnNumber, value);
    }

    private int getAverageOfColumn(int columnNumber) {
        List<Integer> columnData=this.dataGetter.getColumnData(columnNumber);
        OptionalDouble average=myMath.getAverage(columnData);
        if (average.isPresent()) {
            return (int)Math.round(average.getAsDouble());
        } else {
            return DataTypes.BAD_CELL_DATA.getValue();
        }
    }
}
