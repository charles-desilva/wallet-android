package Models;

public class Account {
    private String id;
    private String accountName;
    private String shortCode;
    private int status;
    private String user;

    public Account() {
    }

    public Account(String id, String accountName, String shortCode, int status, String user) {
        this.id = id;
        this.accountName = accountName;
        this.shortCode = shortCode;
        this.status = status;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getShortCode() {
        return shortCode;
    }

    public int getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }
}
