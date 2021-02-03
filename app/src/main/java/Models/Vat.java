package Models;

public class Vat {
    private String id;
    private double value;
    private String user;
    private int vatselectionid;
    private String vatselectionname;
    private String vatregname;
    private String vatregno;


    public Vat() {
    }

    public Vat(String id, double value, String user, int vatselectionid, String vatselectionname, String vatregname, String vatregno) {
        this.id = id;
        this.value = value;
        this.user = user;
        this.vatselectionid = vatselectionid;
        this.vatselectionname = vatselectionname;
        this.vatregname = vatregname;
        this.vatregno = vatregno;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public String getUser() {
        return user;
    }

    public int getVatselectionid() {
        return vatselectionid;
    }

    public String getVatselectionname() {
        return vatselectionname;
    }

    public String getVatregname() {
        return vatregname;
    }

    public String getVatregno() {
        return vatregno;
    }
}
