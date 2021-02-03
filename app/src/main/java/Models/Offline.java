package Models;

public class Offline {
    private String id;
    private String path;
    private String user;

    public Offline() {
    }

    public Offline(String id, String path, String user) {
        this.id = id;
        this.path = path;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getUser() {
        return user;
    }
}
