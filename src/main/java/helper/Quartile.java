package helper;

public enum Quartile {
    FIRST_QUARTILE(25),
    SECOND_QUARTILE(50),
    THIRD_QUARTILE(75),
    FOURTH_QUARTILE(100);

    private final Integer data;

    Quartile(Integer data) {
        this.data=data;
    }

    public Integer getValue() {
        return this.data;
    }
}
