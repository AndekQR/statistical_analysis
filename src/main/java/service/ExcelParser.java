package service;

import helper.DataTypes;
import model.MyRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

    public List<MyRow> parse(String pathToFile, Boolean header) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(new File(pathToFile));
        Workbook workbook=new XSSFWorkbook(fileInputStream);

        Sheet sheet=workbook.getSheetAt(0); //dane są parsowane tylko z pierwszego arkusza
        List<MyRow> data=new ArrayList<>();
        int i=0;

        for (int j=0; j < sheet.getLastRowNum(); j++) {
            if (header && j == 0) {
                continue;
            }
            MyRow tmp=new MyRow();
            Row row = sheet.getRow(j);
            tmp.setRowNumber(j);
            for (int k=0; k < row.getLastCellNum(); k++) {
                Cell cell = row.getCell(k, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell == null) {
                    tmp.addCellData(DataTypes.BAD_CELL_DATA.getValue());
                } else {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        tmp.addCellData((int)cell.getNumericCellValue());
                    } else {
                        tmp.addCellData(DataTypes.BAD_CELL_DATA.getValue());
                    }
                }
            }
            data.add(tmp);
        }
        return data;
    }

    public void saveWorkbook(List<MyRow> data) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        for (int i=0; i < data.size(); i++) {
            Row row = sheet.createRow(i);
            for (int i1=0; i1 < data.get(i).getCellsData().size(); i1++) {
                Cell cell = row.createCell(i1);
                cell.setCellValue(data.get(i).getCellDataValue(i1));
            }
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() -1) + "temp.xlsx";
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }
}
