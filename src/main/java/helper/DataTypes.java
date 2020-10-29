package helper;

public enum DataTypes {
    BAD_CELL_DATA(-1);

    private final Integer data;

    DataTypes(Integer data) {
        this.data=data;
    }

    public Integer getValue() {
        return data;
    }
}
