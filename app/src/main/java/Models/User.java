package Models;

public class User {
    private String id;
    private String name;
    String email;
    private String password;
    private int usertype;
    private int status;

    public User() {
    }

    public User(String id, String name, String email, String password, int usertype, int status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.usertype = usertype;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getUsertype() {
        return usertype;
    }

    public int getStatus() {
        return status;
    }
}
