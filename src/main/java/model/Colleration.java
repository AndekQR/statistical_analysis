package model;

import lombok.Data;

import java.util.List;

@Data
public class Colleration {
    private String propertyName;
    private List<Double> collerationTo;
}
