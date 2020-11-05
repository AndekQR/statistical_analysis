package model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Colleration {
    private String propertyName;
    private List<Double> collerationTo;
}
