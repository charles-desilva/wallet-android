package Models;

public class ReportTable {
    private String name;
    private String value;

    public ReportTable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
