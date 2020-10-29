package controller;

import model.MyRow;
import service.DataFixer;
import service.ExcelParser;

import java.io.IOException;
import java.util.List;

public class MainController {

    public MainController() {
        ExcelParser parser = new ExcelParser();
        try {
            List<MyRow> parse=parser.parse("D:\\weaii\\magisterka\\semestr2\\Analiza i wizualizacja danych\\statistical_analysis\\grzyby.xlsx", true);
            DataFixer dataFixer = new DataFixer();
            List<MyRow> fix=dataFixer.fix(parse);
            parser.saveWorkbook(fix);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
