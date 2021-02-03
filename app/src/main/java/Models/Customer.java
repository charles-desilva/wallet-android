package Models;

public class Customer {
    private String id;
    private String name;
    private String user;

    public Customer() {
    }

    public Customer(String id, String name, String user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }
}
