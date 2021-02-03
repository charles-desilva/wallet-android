package Models;

public class addincome {
    private String id;
    private String date;
    private String accountid;
    private String accountname;
    private String categoryid;
    private String categoryname;
    private String subcategoryid;
    private String subcategoryname;
    private String totlareceived;
    private String internalrefno;
    private String customerid;
    private String customername;
    private String note;
    private String imagepath;
    private int status;
    private String user;

    public addincome() {
    }

    public addincome(String id, String date, String accountid, String accountname, String categoryid, String categoryname, String subcategoryid, String subcategoryname, String totlareceived, String internalrefno, String customerid, String customername, String note, String imagepath, int status, String user) {
        this.id = id;
        this.date = date;
        this.accountid = accountid;
        this.accountname = accountname;
        this.categoryid = categoryid;
        this.categoryname = categoryname;
        this.subcategoryid = subcategoryid;
        this.subcategoryname = subcategoryname;
        this.totlareceived = totlareceived;
        this.internalrefno = internalrefno;
        this.customerid = customerid;
        this.customername = customername;
        this.note = note;
        this.imagepath = imagepath;
        this.status = status;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getAccountid() {
        return accountid;
    }

    public String getAccountname() {
        return accountname;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public String getSubcategoryid() {
        return subcategoryid;
    }

    public String getSubcategoryname() {
        return subcategoryname;
    }

    public String getTotlareceived() {
        return totlareceived;
    }

    public String getInternalrefno() {
        return internalrefno;
    }

    public String getCustomerid() {
        return customerid;
    }

    public String getCustomername() {
        return customername;
    }

    public String getNote() {
        return note;
    }

    public String getImagepath() {
        return imagepath;
    }

    public int getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public void setSubcategoryid(String subcategoryid) {
        this.subcategoryid = subcategoryid;
    }

    public void setSubcategoryname(String subcategoryname) {
        this.subcategoryname = subcategoryname;
    }

    public void setTotlareceived(String totlareceived) {
        this.totlareceived = totlareceived;
    }

    public void setInternalrefno(String internalrefno) {
        this.internalrefno = internalrefno;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
