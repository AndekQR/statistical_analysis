package model;

import lombok.Data;

import java.util.List;

@Data
public class WorkbookData {
    private List<String> titlies;
    private List<MyRow> data;
}
