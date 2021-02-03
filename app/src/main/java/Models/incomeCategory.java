package Models;

public class incomeCategory {
    private String id;
    private String category;
    private String user;
    private int status;

    public incomeCategory() {
    }

    public incomeCategory(String id, String category, String user, int status) {
        this.id = id;
        this.category = category;
        this.user = user;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getUser() {
        return user;
    }

    public int getStatus() {
        return status;
    }
}
