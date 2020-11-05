package view;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Colleration;

import java.util.*;
import java.util.stream.Collectors;

public class MyTable {

    private List<Colleration> data;
    private GridPane table;
    private VBox container;
    private Integer size;
    private Map<String, String> encodedTitles;

    private static final String NAME_CODE = "X";

    public MyTable(List<Colleration> data) {
        this.data=data;
        init();
    }

    private void init() {
        this.size = this.getTableSize();
        this.initTitles();
        this.table= new GridPane();
        this.container = new VBox();
        this.container.getChildren().add(table);
        insertTitles();
        insertData();
        legend();
    }


    private void insertTitles() {
        for (int rowIndex=0; rowIndex < data.size()+1; rowIndex++) {
            for (int columnIndex=0; columnIndex < data.size(); columnIndex++) {
                if (columnIndex == 0 || rowIndex == 0 ) {
                    insertTitle(columnIndex, rowIndex);
                }
            }
        }
    }

    private void insertData() {
        for (int rowIndex=0; rowIndex < data.size(); rowIndex++) {
            for (int columnIndex=0; columnIndex < data.size(); columnIndex++) {
                    insertData(columnIndex, rowIndex);
            }
        }
    }

    public Node getNode(){
        return this.container;
    }

    private void insertData(int column, int row) {
        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("data-cell");
        Double value=this.getValue(row, column);

        if(value <= 1 && value > 0.6) {
            borderPane.getStyleClass().add("level-1");
        }else if(value <= 0.6 && value > 0) {
            borderPane.getStyleClass().add("level-2");
        } else if(value <= 0 && value > -0.6){
            borderPane.getStyleClass().add("level-3");
        }else if(value <= -0.6 && value >= -1){
            borderPane.getStyleClass().add("level-4");
        }

        Text cell = this.getCell(value.toString());
        borderPane.setCenter(cell);
        this.table.add(borderPane, column+1, row+1);
    }

    private void insertTitle(int column, int row) {
        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("header-cell");
        if (row == 0) {
            Text cell=this.getCell(this.getEncodedName(column));
            borderPane.setCenter(cell);
            this.table.add(borderPane, column+1, row);
        }
        else if (column == 0){
            Text cell=this.getCell(this.getEncodedName(row-1));
            borderPane.setCenter(cell);
            this.table.add(borderPane, column, row);
        }
    }

    private void legend() {
        TilePane tilePane = new TilePane();

        encodedTitles.forEach((encoded, title) -> {
            Text text = new Text();
            text.setText(encoded+" = "+title);
            tilePane.getChildren().add(text);
        });

        this.container.getChildren().add(tilePane);
    }

    private Double getValue(int index1, int index2) {
        List<Double> values = this.data.get(index1).getCollerationTo();
        return values.get(index2);
    }

    private String getEncodedName(int index) {
        String[] strings=this.encodedTitles.values().toArray(new String[0]);
        if (strings.length < index) throw new IllegalArgumentException("Poza tablicą");
        else return strings[index];
    }

    private String getName(int index) {
        String[] strings=this.encodedTitles.keySet().toArray(new String[0]);
        if (strings.length < index) throw new IllegalArgumentException("Poza tablicą");
        else return strings[index];
    }

    private Text getCell(String text) {
        Text textNode = new Text();
        textNode.setText(text);
        return textNode;
    }


    private Integer getTableSize() {
        return this.data.size();
    }

    private void initTitles() {
        Map<String, String> tmp = new LinkedHashMap<>();
        List<String> titles=this.data.stream()
                .map(Colleration::getPropertyName)
                .collect(Collectors.toList());

        for (int i=0; i < titles.size(); i++) {
            String encoded = NAME_CODE+i;
            tmp.put(titles.get(i), encoded);
        }
        this.encodedTitles = tmp;
    }
}
