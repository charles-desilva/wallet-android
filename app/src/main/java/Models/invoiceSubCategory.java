package Models;

public class invoiceSubCategory {
    private String id;
    private String subCategory;
    private String category;
    private int status;

    public invoiceSubCategory() {
    }

    public invoiceSubCategory(String id, String subCategory, String category, int status) {
        this.id = id;
        this.subCategory = subCategory;
        this.category = category;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getCategory() {
        return category;
    }

    public int getStatus() {
        return status;
    }
}
