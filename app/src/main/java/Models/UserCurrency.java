package Models;

public class UserCurrency {
    private String id;
    private String user;
    private String currency;

    public UserCurrency() {
    }

    public UserCurrency(String id, String user, String currency) {
        this.id = id;
        this.user = user;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getCurrency() {
        return currency;
    }
}
