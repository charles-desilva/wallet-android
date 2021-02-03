package Models;

public class PayModes {
    private String id;
    private String mode;
    private String user;
    private int status;

    public PayModes() {
    }

    public PayModes(String id, String mode, String user, int status) {
        this.id = id;
        this.mode = mode;
        this.user = user;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getMode() {
        return mode;
    }

    public String getUser() {
        return user;
    }

    public int getStatus() {
        return status;
    }
}
