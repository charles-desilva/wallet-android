package Models;

public class expenceSubCategory {
    private String id;
    private String subCategory;
    private String category;
    private int status;

    public expenceSubCategory() {
    }

    public expenceSubCategory(String id, String subCategory, String category, int status) {
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
