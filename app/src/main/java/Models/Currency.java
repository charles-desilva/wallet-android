package Models;

public class Currency {
    private String id;
    private String currency;
    private String user;
    private int status;

    public Currency() {
    }

    public Currency(String id, String currency, String user, int status) {
        this.id = id;
        this.currency = currency;
        this.user = user;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUser() {
        return user;
    }

    public int getStatus() {
        return status;
    }
}
